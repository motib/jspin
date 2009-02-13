/* Copyright 2005-6 by Mordechai (Moti) Ben-Ari. See copyright.txt */
/*
 * Process a source statement
*/

package spinSpider;
class Statement {
    int process;        // Process where the statement is declared
    int sourceState;    // Source state
    int targetState;    // Target state
    int id;             // Used for trail
    boolean atomic;     // Atomic statement (for automata)
    int line;           // Line number of source statement
    String source;      // Source code statement

    public Statement(int p, int s, int t, int i, boolean a, int l, String src) {
        process = p; 
        sourceState = s;
        id = i;
        targetState = t; 
        atomic = a;
        line = l;
        if (src.indexOf("printf") != -1)        // Remove printf and quotes
            src = src.substring(src.indexOf("printf")+8, src.indexOf("\\n"));
        src = src.replaceAll("[()]", "");       // Remove parentheses
        source = src.trim();
    }

    public String toString() {
        return 
        	"process=" + process + 
        	", source=" + sourceState + 
        	", target=" + targetState + 
        	", id=" + id +
        	(atomic ? ", atomic" : "") +
        	", line=" + line + 
        	"." + source;
    }
}