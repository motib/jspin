/*
  SpinSpider - Use Spin to Create State Transition Diagrams
  Copyright 2005 by Mordechai (Moti) Ben-Ari.
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.
*/

//package jspin.spinSpider;
package spinSpider;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class SpinSpider {

    // The following two variables are passed from jSpin
	// For standalone execution, properties is set in the main method
	//   and output is to standard output
	Properties properties;
	private javax.swing.JTextArea area;

			String  fileName;    // File name (without extension)
			String  extension;   // Extension of Promela file name
			String  format;      // Graph format (dot/fsm/png ...)
			int     trailCode;   // Use trail file to draw or emphasize a computation
			int     dotSize;     // Size of dot graph
			int     trailStyle;  // Style of trail in dot graph color/bold
      private boolean debug;       // Write debug file
		  // For writing never claim:
	    private int     numProcs;	   // Number of processes -
			//   write never claim if numProcs > 0
      private String[] vars;       // Names of variables
	
    // Databases
	ArrayList<State>      states      = 
		new ArrayList<State>(Config.INITIAL);
	ArrayList<Statement>  statements  = 
		new ArrayList<Statement>(Config.INITIAL);
	ArrayList<Transition> transitions = 
		new ArrayList<Transition>(Config.INITIAL);
	ArrayList<Trail>      trails      = 
		new ArrayList<Trail>(Config.INITIAL);
	
	// For FSM we need to compute the number of statements per process
    ArrayList<Integer>    statementsPerProcess =
    	new ArrayList<Integer>(Config.INITIAL);
	
    public SpinSpider(
    		String fileName,
    		String extension,
    		String format,
    		int numProcs, 
    		String[] vars,
            boolean debug, 
            int trailCode,
            int dotSize,
            int trailStyle,
            javax.swing.JTextArea area,
            Properties properties) {
        this.fileName = fileName;
        this.extension = extension;
        this.format = format;
        this.numProcs = numProcs;
        this.vars = vars;
        this.debug = debug;
        this.trailCode = trailCode;
        this.dotSize   = dotSize;
        this.trailStyle = trailStyle;
        this.area = area;
        this.properties = properties;
        State.numProcs = 0;
        State.numVars  = 0;
    }
    
    // Create array of tokens from a string for ProcessBuilder
    private static String[] stringToArray(String s) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(s);
        int count = st.countTokens();
        String[] sa = new String[count];
        for (int i = 0; i < count; i++) 
        	sa[i] = st.nextToken();
        return sa;
    }

    // Build a process and run it
    //   command is the command string with parameters
    //   ext is the extension for redirecting standard output
    //     if null, output is sent to System.out
    void runProcess(String command, String ext) {
    	PrintWriter outWriter = null;
        progress("Running " + command);
        if (ext != null) 
            try {
                outWriter = new PrintWriter(new FileWriter(fileName + ext));
            } catch (IOException e) { fileError(ext); }
        try {
            ProcessBuilder pb = new ProcessBuilder(stringToArray(command));
            File pf = new File(fileName).getParentFile();
            if (pf != null) pb.directory(pf.getCanonicalFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while (true) {
                s = input.readLine();
                if (s == null) break;
                if (ext == null) System.out.println(s);
                else outWriter.println(s);
            }
            p.waitFor();
            if (ext != null) outWriter.close();
        } catch (IOException e) {
            progress("IO exception " + e.getMessage() + 
            		" while executing " + command);
        } catch (InterruptedException e) {
            progress("Interrupted exception while executing " + command);
        }
    }

    // This method runs SpinSpider
    public boolean runSpider() {
        progress(Config.TITLE);
    	// Create objects for reading and writing files
        Write write = new Write(this, fileName);
        Read read = new Read(this, fileName, properties);

        // Write never claim if requested
    	String never = "";
    	if (trailCode != 3) {
    		if (!write.writeNeverClaim(numProcs, vars)) return false;
    		never = " -N " + fileName + ".nvr ";
    	}

        // Run Spin and C compiler
        runProcess(properties.getProperty("SPIN") + 
        	" -a -o3 " + never + fileName + extension, null);
        runProcess(properties.getProperty("C_COMPILER") + 
            " -DCHECK -DSAFETY -DPRINTF -DNOREDUCE -o pan pan.c", null);
        
        // Run pan twice: once for -DCHECK file and once for -d file
        String pan = "";
        try {
        	File f = new File(fileName).getParentFile();
        	if (f != null) 
        		pan = f.getCanonicalPath() + java.io.File.separator;
        } catch(IOException e) {};
        pan = pan + properties.getProperty("PAN");
        if (trailCode != 3) runProcess(pan, ".chk");
        runProcess(pan + " -d", ".d");

        // Read the file created by -d
        read.createStatements();
        // Read the file created by -DCHECK and never claim
        if (trailCode != 3) read.readCheckFile();

        // Read the trail file
        if ((trailCode == 1) || (trailCode == 2)) {
        	read.readTrail();
        	new SetTrail(this).setTrail();
        }

        // Write output files
        if (debug || (trailCode == 3))
        	write.writeDebug();
        if (format.equals("fsm")) 
        	write.writeFSMGraph();
        else
        	write.writeDotGraph();
        	
        progress("Done");
        return true;
    }

    // Display progress messages in text area of jSpin or on standard output
    void progress(String s) {
    	if (s == null) 
    		return;
    	if (area == null) { 
    		System.out.println(s); 
    		return; 
    	}
    	area.append(s + '\n');
    	int last = area.getText().length();
    	try { 
    		area.scrollRectToVisible(area.modelToView(last)); 
    		area.setCaretPosition(last);
    	}
    	catch (javax.swing.text.BadLocationException e) {
    		System.err.println(
    			"Error setting caret position when writing\n" + s + "\n");
    	}
    }

    // Print error message and exit
    static void error(String e) {
    	System.err.println(e);
    	System.exit(1);
    }
    
    // Print file error message and exit
    void fileError(String ext) {
    	String s = "FILE ERROR " + fileName + ext;
    	if (area == null) error(s); else progress(s);
    }

    // Main method for running independently of jSpin
    public static void main(String[] args) {
        if (args.length == 0) error(Config.USAGE);
        File file = new File(args[args.length-1]);
        String fileName = file.getName();
        String root;
        String extension = "";
        if (fileName.lastIndexOf('.') != -1) {
            extension = fileName.substring(fileName.lastIndexOf('.'));
        }
        if (file.getParentFile() == null)
            root = "";
        else
            root = file.getParentFile().getAbsolutePath();
        fileName  = root + File.separator + 
        	fileName.substring(0, fileName.lastIndexOf('.'));
        
        boolean debug = false;
        boolean automata = false;
        int trailCode = 0;
        int dotSize = 1;
        int trailStyle = 0;
        String format = "png";
        int numProcs = 0;
        // Variable names for automatic writing of never claim
    	ArrayList<String> vars = new ArrayList<String>(Config.INITIAL);

        for (int i = 0; i < args.length-1; i++)
            if (args[i].equals("-dot"))       	format = "dot";
            else if (args[i].equals("-fsm"))  	format = "fsm";
            else if (args[i].equals("-a"))  	automata = true;
            else if (args[i].equals("-debug")) 	debug = true;
            else if (args[i].equals("-small")) 	dotSize = 0;
            else if (args[i].equals("-bold")) 	trailStyle = 1;
            else if (args[i].equals("-t1"))		trailCode = 1;
            else if (args[i].equals("-t2"))		trailCode = 2;
            else if (args[i].startsWith("-T")) 	
            	format = args[i].substring(2);
            else if (args[i].startsWith("-p")) 
            	try {
            		numProcs = Integer.parseInt(args[i].substring(2));
            	} catch (NumberFormatException e) {
            		error(Config.USAGE);
            	}
            else if (args[i].startsWith("-v")) 
            	vars.add(args[i].substring(2));
            else error(Config.USAGE);

        if (!automata && (numProcs <= 0)) error(Config.USAGE);

        String[] varArray = new String[vars.size()];
        varArray = vars.toArray(varArray);

        Properties properties = new Properties();
        Config.init(properties);
        if (automata) {
        	debug = true;
        	trailCode = 3;
        }
        SpinSpider spd = new SpinSpider(
       		fileName, extension, format, numProcs, varArray,
       		debug, trailCode, dotSize, trailStyle, 
       		null, properties);
    	if (!spd.runSpider()) return;
        if (automata)
        	new DrawAutomata(spd).drawAutomata();
    }
}
