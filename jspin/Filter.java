/* Copyright 2004-5 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Filter Spin output.
 * Display a table: process, statement, variables.
 * The width of a variable field is dynamically configurable (VARIABLE_WIDTH).
*/

package jspin;
import java.util.*;

class Filter {
    private static final String spacerTitle = " ";

	// Print the title repeatedly
    private static final int linesPerTitle = 20;
    private String title;  		// String for names of variables
    private int lines;		    // Line counter to redisplay title

    // Map of variable names and values
    private TreeMap<String, String> variables = new TreeMap<String, String>();
    private int oldSize;        // Previous size to see if variable added

    // Excluded variable names and statements
    private ArrayList<String> excludedVar = new ArrayList<String>();
    private ArrayList<String> excludedState = new ArrayList<String>();

	// Called before each new command
	void init () {
		variables.clear();
		oldSize = 0;
		title = "";
		lines = -1;
	}

    // Parse string to initialize excluded array; ignore NL and CR
    void setExcluded(String s, boolean exVar) {
        ArrayList<String> excluded = exVar ? excludedVar : excludedState;
        s = s.replace('\r', '\n');
        excluded.clear();
        do {
            int nl = s.indexOf('\n');
            if (nl == -1) break;
            if (!s.substring(0,nl).equals(""))
                excluded.add(s.substring(0,nl));
            s = s.substring(nl+1);
        } while (true);
    }

    // Check for excluded variables.
    //   For each excluded string S;
    //   If variable name includes S, do not display,
    //   but if some +T in file is in name, display anyway.
    private boolean checkExcluded(String name, boolean exVar) {
        ArrayList<String> excluded = exVar ? excludedVar : excludedState;
        for (int i = 0; i < excluded.size(); i++)
            if ((excluded.get(i).charAt(0) != '+') &&
                (name.indexOf(excluded.get(i)) != -1)) {
                boolean included = false;
                for (int j = 0; j < excluded.size(); j++) 
                    included = included || 
                        ((excluded.get(j).charAt(0) == '+') &&
                         (name.indexOf(excluded.get(j).substring(1)) != -1));
                return !included;
            }
         return false;
    }

    String filter(String s) {
        try {
            // Variables and queues start with double tab
            if (s.startsWith("\t\t")) {
                String varName, varValue;
                // Name and value of variable/queue to TreeMap
                if (s.startsWith("\t\tqueue")) {
                    varName = s.substring(s.indexOf('(')+1, s.indexOf(')'));
                    varValue = s.substring(s.indexOf(':')+1).trim();
                } 
                else {
                    varName = s.substring(2, s.indexOf('=')-1); 
                    varValue = s.substring(s.indexOf('=')+1).trim();
                }
                if (checkExcluded(varName, true)) return "";
                variables.put(varName, varValue);
                // Construct new title if new variables encountered
                if (variables.size() > oldSize) { 
                    title = formatItem(Config.processTitle, 
                                Config.getIntProperty("PROCESS_WIDTH")) +
                            spacerTitle +
                            formatItem(Config.statementTitle, 
                                Config.getIntProperty("STATEMENT_WIDTH")) + 
                            spacerTitle + 
                            collectionToString(variables.keySet()) + "\n";
                    oldSize = variables.size();
                    lines = -1;
                }
                return "";
            }
			
            // Display Spin and pan messages unmodified
			// Also non-prefixed error message
            else if ((s.startsWith("pan:")) || 
                     (s.startsWith("spin:")) ||  
                     (s.startsWith("tl_spin:")) ||
                     (s.startsWith("Error:")) ||
                     (s.startsWith("error:")) )
                return s + "\n";
            // Display error count
            else if (s.indexOf("errors:") != -1)
                return 
                s.substring(0, s.indexOf("errors:")) + "\u2022\u2022\u2022 " + 
                s.substring(s.indexOf("errors:")) + " \u2022\u2022\u2022\n";
            // Display MSC lines (without MSC:)
            else if (Config.getBooleanProperty("MSC") && 
            		 s.trim().startsWith("MSC:"))
                return s.substring(s.indexOf("MSC:")+5) + "\n";
            // Display cycle messages
            else if (s.indexOf("<<<<<") != -1)
                return s + "\n";
			
            // Display lines with statements, including choices
            else if ((s.indexOf("proc") != -1) && (s.indexOf("line") != -1)) {
                String u = "";
                // Get process name
                String proc = 
					s.substring(s.indexOf("proc")+4, s.indexOf(")")+1).trim();
                proc = proc.substring(0, proc.indexOf("(")) + 
                    strip(proc.substring(proc.indexOf("(")));
                // Get line number and statement name
                String statement = "";
                if (s.indexOf('[') != -1) {
                	statement = formatItem(
                				s.substring(s.indexOf("line")+4,s.indexOf("\"")).trim(), 3);
                    statement = statement + " " +
                      strip(s.substring(s.indexOf('[')+1, s.lastIndexOf(']')));
                }
                if (checkExcluded(statement, false)) return "";
                // For choice, display just process and statement
                if (s.indexOf("choice") != -1)
                    u = s.substring(s.indexOf("choice"), s.indexOf(':')+2) + 
                        proc + " " + statement + "\n";
                // Display table line (unless goto/else/break)
                else if ((statement.indexOf("goto") == -1) && 
                    	 !statement.equals("else") &&
                    	 !statement.equals("break")) {
                    u = formatItem(proc, 
                            Config.getIntProperty("PROCESS_WIDTH")) +
                        spacerTitle +
                            formatItem(statement, 
                            Config.getIntProperty("STATEMENT_WIDTH")) + 
                            spacerTitle + 
                            collectionToString(variables.values()) + "\n";
                    lines = (lines + 1) % linesPerTitle;
                    // Display title if needed
                    if (lines == 0) u = title + u;
                }
                return u;
            }
            // Display choices that have no proc and line number
            else if (s.indexOf("choice") != -1)
                return s.substring(s.indexOf("choice")) + "\n";
            else if (!Config.getBooleanProperty("MSC") && !s.equals(""))
            	return s + "\n";
            else
            	return "";
            
        } catch (Exception e) {
            System.err.println("\n + jSpin error in filter for:\n" + s + "\n");
            e.printStackTrace();
            return "";
        }
    }

    // Strip parentheses
    String strip(String s) {
        while ( (s.charAt(0)=='(') && (s.charAt(s.length()-1)==')'))
            s = s.substring(1, s.length()-1);
        return s;
    }

	// Format a variable name or value
	private String formatItem(String s, int len) {
		// Item is too long, must shorten it
		if (s.length() > len) { 
			// Array variable: shorten name, not index
			if (s.indexOf("[") != -1) { 
				String subscript = 
					s.substring(s.indexOf("["), s.lastIndexOf("]")+1);
                if (subscript.length() < len)
                    return s.substring(0, 
					    len-subscript.length()) + subscript;
                else
                    return s.substring(0, len);
			}
			else
				return s.substring(0,len);
		}
		// Item is too short, pad it
		else if (s.length() < len)
			return (s + "                         ").substring(0,len);
		else
			return s;
	}

	// Transform a collection to a string, calling formatItem for each element
	private String collectionToString(Collection<String> c) {
		String s = "";
		Iterator<String> it = c.iterator();
		while (it.hasNext()) 
			s = s + formatItem(it.next(), 
				Config.getIntProperty("VARIABLE_WIDTH")) + " ";
		return s;
	}	
}
