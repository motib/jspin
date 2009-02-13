/* Copyright 2007 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Draw the automata representation of a Promela program.
*/

package spinSpider;
import java.io.*;

public class DrawAutomata {
	SpinSpider spd;

	public DrawAutomata(SpinSpider s) {
		spd = s;
	}

  // Extract an int value that appears between "from" and "to" in "s" 
  private static int extract(String s, String from, String to) {
      return 
      	Integer.parseInt(
      		s.substring(s.indexOf(from)+from.length(), s.indexOf(to))
      		.trim());
  }

  public void drawAutomata() {
	BufferedReader statementReader = null;
  	PrintWriter graphWriter = null;
    String s = "";
    String fn = spd.fileName + Config.names[3];

    spd.progress("Drawing automata");
    try {
        statementReader = new BufferedReader(
            new FileReader(spd.fileName + ".dbg"));
    } catch (IOException e) { spd.fileError(".dbg"); }
    try {
        graphWriter = new PrintWriter(
        	new FileWriter(fn + ".dot"));
    } catch (IOException e) { spd.fileError(".dot"); }

    // Write dot prologue
    graphWriter.println("digraph \"" + fn + "\"" + " {");
    for (int i = 0; i < Config.automataDotPrologue.length; i++)
        graphWriter.println(Config.automataDotPrologue[i]);
    
    // Skip over first parts of debug files
    while (true) {
        try { s = statementReader.readLine(); } 
        catch (IOException e) { spd.fileError(".dbg"); }
        if (s == null) 
        	spd.fileError(".dbg");
        else
        	if (s.equals("Statements from -d file"))
        		break;
    }
    
    // Extract data from each line and write nodes and edges
    // Use DELTA to create unique node numbers for each process
    int     process = 0, proc, line, source, target;
    boolean endState = false, atomic = false, previousAtomic = false;
    String  emphasize = spd.trailStyle == 0 ? Config.dotTrailColor : Config.dotTrailBold;
    while (true) {
        try { s = statementReader.readLine(); } 
        catch (IOException e) { spd.fileError(".dbg"); }
        if (s == null) break;
        if (s.indexOf("process") == -1) break;
       	proc = extract(s, "process=", ", source");
       	// New process
       	if (proc != process) {
       		process = proc;
       		endState = false;
       	}
       	source = extract(s, "source=", ", target") + process*Config.DELTA;
       	line = extract(s, "line=", ".");
       	graphWriter.println(source + " [label=" + line + "]");
       	target = extract(s, "target=", ", id") + process*Config.DELTA;
       	if ((target == process*Config.DELTA) && !endState) {
       		endState = true;
       		graphWriter.println(target + " [label=0]");
       	}
       	atomic = s.indexOf("atomic,") != -1;
       	graphWriter.println(source + " -> " + target + 
       			" [label=\"" + s.substring(s.indexOf('.')+1) + "\"" +
       			(atomic || previousAtomic ? emphasize : "") + "]");
       	previousAtomic = atomic;
    }
    
    try { statementReader.close(); } 
    catch (IOException e) { spd.fileError(".dbg"); }
    graphWriter.println("}");
    graphWriter.close();
    
    // Call dot to format dot file
    if (!spd.format.equals("dot"))
    	spd.runProcess(spd.properties.getProperty("DOT") + 
   			" -T" + spd.format + 
   			" -o " + fn + "." + spd.format + " " + 
   			fn + ".dot", null);
  }

}
