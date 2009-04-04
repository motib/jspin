/* Copyright 2006 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Read the data files
*/

package spinSpider;
import java.io.*;

class Read {
	SpinSpider spd;
	String fileName;
	Read(SpinSpider s, String f) { 
		spd = s; 
		fileName = f;
	}

  // Read the state transitions output by -DCHECK and never claim
  // Thanks to Gerard Holzmann for showing me how to do this.
  void readCheckFile() {
	BufferedReader checkReader = null;
	String s = "";              // Read line to s
	String[] tokens;            // For tokenizing
	// Stack of states traversed
	int[] stack = new int[Config.MAX_STACK];
    int newState = 0;           // State number in "New"
    int depth = 0;              // Stack depth
    boolean needNew = false;    // Need state information after "New"
    int offset = 0;             // Offset of variables after procs

    spd.progress("Reading -DCHECK file");
    try {
        checkReader = new BufferedReader(
            new FileReader(fileName + ".chk"));
    } catch (IOException e) { spd.fileError(".chk"); }

    while (true) {
        try { s = checkReader.readLine(); } 
        catch (IOException e) { spd.fileError(".chk"); }
        if ((s == null) || s.indexOf("Spin Version") != -1) break;

        tokens = s.trim().split("\\s");
        // "New" indicates a new state
        //    Push on the stack
        //    Immediately thereafter the *spd* output will appear
        if ((tokens.length > 2) && tokens[0].equals("New")) {
            newState = Integer.parseInt(tokens[2]);
            stack[depth] = newState;
            needNew = true;
        }    
        // "Down" for a program (not a claim) indicates a transition
        //    to the new state that was just pushed on the stack
        else if ((tokens.length > 3) && (tokens[1].equals("Down"))) {
            if (tokens[3].equals("program") && (newState > 0))
            	spd.transitions.add(
                    new Transition(stack[depth-1], newState));
            depth++;
            stack[depth] = stack[depth-1];
        }
        // "Up" - pop the stack
        else if ((tokens.length > 1) && tokens[1].equals("Up")) {
            depth--;
        } 
        // "Old" - just a transition, no need to push a new state
        else if ((tokens.length > 2) && tokens[0].equals("Old")) {
            newState = Integer.parseInt(tokens[2]);
            spd.transitions.add(
                new Transition(stack[depth], newState));
        } 
        // "Stack" - have never occurred
        // When it does you'll have to figure out what to do!
        else if ((tokens.length > 2) && tokens[0].equals("Stack")) {
            // For now, I don't know why this is here.....
            System.out.println("Stack line = " + s); 
            System.exit(3);
        } 
        // "*spd*" - The data created by the never claim
        else if (needNew && (tokens.length > 1) && 
                    tokens[0].equals("*spd*")) {
        	// The first time, set the number of processes and variables
        	if (State.numProcs == 0) {
                State.numProcs = Integer.parseInt(tokens[1]);
                offset = State.numProcs + 3;
                State.numVars = Integer.parseInt(tokens[offset-1]);
            }
            // Parse the pc values and the values of the variables
            //   and construct a new state
            needNew = false;
            int[] st = new int[State.numProcs];
            String[] vn = new String[State.numVars];
            String[] vr = new String[State.numVars];
            for (int proc = 0; proc < State.numProcs; proc++)
                st[proc] = Integer.parseInt(tokens[proc + 2]);
            for (int var = 0; var < State.numVars; var++) {
                vn[var] = tokens[offset + 2*var];
                vr[var] = tokens[offset + 2*var + 1];
            }

            spd.states.add(new State(st, vn, vr));
        }
    }
    try { checkReader.close(); } 
    catch (IOException e) { spd.fileError(".chk"); }
  }

  // Extract an int value that appears between "from" and "to" in "s" 
  private static int extract(String s, String from, String to) {
      return 
      	Integer.parseInt(
      		s.substring(s.indexOf(from)+from.length(), s.indexOf(to))
      		.trim());
  }

  // Create statement database from -d file
  void createStatements() {
	BufferedReader statementReader = null;
    String s = "";
    int proc = 0;             // For detecting change of process
    int countStatements = 0;   // Count statements per process
    spd.progress("Reading -d file");
    try {
        statementReader = new BufferedReader(
            new FileReader(fileName + ".d"));
    } catch (IOException e) { spd.fileError(".d"); }
    
    while (true) {
        try { s = statementReader.readLine(); } 
        catch (IOException e) { spd.fileError(".d"); }
        if (s == null) {
        	if (spd.trailCode != 3)
        		spd.fileError(".d (the never claim may be missing)");
        	break;
        }
        
        // Start of never claim - terminate processing
        else if (s.indexOf(":never:") != -1) {
    		spd.statementsPerProcess.add(
    			new Integer(countStatements)); 
        	break;
        }

        // Beginning of transitions of a process - store and reset count
        else if (s.indexOf("proctype") != -1) {
        	if (proc != 0) 
        		spd.statementsPerProcess.add(
        			new Integer(countStatements));
        	countStatements = 0;
        	proc++;
        }
        
        // Entry for each transition
        else if (s.indexOf("-> state") != -1) {
        	countStatements++;
        	spd.statements.add(new Statement(
        		proc-1,                        // process number 
                extract(s, "state", "-(tr"),   // source state
                extract(s, "-> state", "[id"), // target state
                extract(s, "[id", "tp"),       // id number for trail
                s.indexOf("[A") != -1,         // atomic for automata
                extract(s, "line", "=>"),      // source line number
                                               // source statement
                s.substring(s.indexOf("=>")+2).trim()));
        }
    }
    try { statementReader.close(); } 
    catch (IOException e) { spd.fileError(".d"); }
  }
	
  // Read the trail file whose entries are -
  //   "sequence number : process number : transition id"
  //   Sequence number = -2 means there is a never claim
  //   In that case, ignore its transitions (process number 0)
  void readTrail() {
	BufferedReader trailReader = null;
	boolean claim = false;
    String s = "";
    spd.progress("Reading trail file");
    try {
    	trailReader = new BufferedReader(
    			new FileReader(fileName + ".pml.trail"));
    } catch (IOException e) { spd.fileError(".pml.trail"); return; }
    
    while (true) {
    	try { s = trailReader.readLine(); } 
    	catch (IOException e) { spd.fileError(".pml.tral"); }
    	if (s == null) break;
    	String[] t = s.split(":");
    	if (t[0].startsWith("-2")) { 
    		claim = true; 
    		continue; 
    	}
    	if ((t[0].charAt(0) == '-')) continue;
    	if (claim && (t[1].charAt(0) == '0')) continue;
    	Trail tr = new Trail(Integer.valueOf(t[2]).intValue());
   		spd.trails.add(tr);
    }
    try { trailReader.close(); } 
    catch (IOException e) { spd.fileError(".pml.trail"); }
  }
  
}
