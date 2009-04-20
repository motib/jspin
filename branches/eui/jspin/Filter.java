// Copyright 2004-8 by Mordechai (Moti) Ben-Ari. See copyright.txt.
/**
 Filter Spin output.
 Display a table: process, statement, variables.
*/
package jspin;
import java.util.*;
public class Filter {
    // Properties and copies of the data from the properties
    private Properties properties;
    private static String processTitle;
    private static String statementTitle;
    private static int processWidth;
    private static int variableWidth;
    private static int statementWidth;
    private static int linesPerTitle;
    private static boolean msc;
    
    private String title;  		// Title string
    private String process;
    private String statement;
    private int lines;		    // Line counter to redisplay title
    private boolean program;

    private static final int COLS_LINE_NUMBER = 2;
    private static final String blanks = "    ";

    // Map from variable names to values
    private TreeMap<String, String> variables = new TreeMap<String, String>();

    // Excluded variable and statements
    private ArrayList<String> excludedVar = new ArrayList<String>();
    private ArrayList<String> excludedState = new ArrayList<String>();

	/**
    Initialize variables and local copies of properties
  */
	public void init (Properties properties) {
		variables.clear();
		title = "";
		lines = -1;
    program = true;
    this.properties = properties;
    processTitle    = properties.getProperty("PROCESS_TITLE");
    statementTitle  = properties.getProperty("STATEMENT_TITLE");
    processWidth    = Integer.valueOf(properties.getProperty("PROCESS_WIDTH"));
    variableWidth   = Integer.valueOf(properties.getProperty("VARIABLE_WIDTH"));
    statementWidth  = Integer.valueOf(properties.getProperty("STATEMENT_WIDTH"));
    linesPerTitle   = Integer.valueOf(properties.getProperty("LINES_PER_TITLE"));
    msc             = Boolean.valueOf(properties.getProperty("MSC"));
    process   = formatItem("", processWidth);
    statement = formatItem("", statementWidth);
	}

  // Parse string to initialize excluded arrays
  public void setExcluded(String s, boolean exVar) {
    ArrayList<String> excluded = exVar ? excludedVar : excludedState;
    // Replace whitespace by separator
    s = s.replaceAll("\\s+", Config.SEPARATOR) + Config.SEPARATOR;
    excluded.clear();
    do {
      int nl = s.indexOf(Config.SEPARATOR);
      if (nl == -1) break;
      if (!s.substring(0,nl).equals(""))
        excluded.add(s.substring(0, nl));
      s = s.substring(nl+1);
    } while (true);
  }

  /**
    Check if name is excluded variable (exVar=true) or statement (exVar=false)
    For each excluded string S;
      If variable name includes S, do not display,
      but if some +T in file is in name, display anyway.
  */
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

  public String filterVerification(String s) {
    int i;
    if (s.startsWith("Erigone"))
        return s + "\n";
    else if (s.startsWith("execution mode")) {
      i = s.indexOf(",");
      return s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n";
    }
    else if (s.startsWith("verification terminated=successfully,"))
      return "\n" + s + "\n\n";
    else if (s.startsWith("verification terminated")) {
      i = s.indexOf(",");
      return "\n" + s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n\n";
    }
    else
      return s + "\n";
  }

  private String extract(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
    return s.substring(i, s.indexOf(",", i+1));
  }

  private String extractBraces(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
    String t = s.substring(i, s.indexOf(",", i+1));
    return t.substring(1, t.length()-1);
  }

  private int extractNum(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
    String t = s.substring(i, s.indexOf(",", i+1));
    try {
      return Integer.parseInt(t);
    }
    catch(NumberFormatException e) {
      return -1;
    }
  }

  public String filterSimulation(String s) {
    int i;
    if (program) {
      if (s.startsWith("Erigone"))
          return s + "\n";
      else if (s.startsWith("execution mode")) {
        i = s.indexOf(",");
        return s.substring(0, i+1) + "\n" +
               s.substring(i+1) + "\n";
      }
      else if (s.startsWith("type=")) {
        variables.put(extract(s, "name="), "");
        return "";
      }
      else if (s.startsWith("symbol table end=")) {
        title = formatItem(processTitle,   processWidth)   + " " +
                formatItem(statementTitle, statementWidth) + " " + 
                collectionToString(variables.keySet()) + "\n";
        lines = -1;
        return "";
      }
      else if (s.startsWith("transitions end=")) {
        program = false;
        return "";
      }
      else
        return "";
    }
    else {
      if (s.startsWith("initial state=") || s.startsWith("next state=")) {
        storeVariables(s);
        return "";
      }
      else if (s.startsWith("process=") && (s.indexOf("initial=") == -1)) {
        process   = formatItem(extract(s, "process="), processWidth);
        String ln = extract(s, "line=");
        statement = formatItem(
          (ln.length() < COLS_LINE_NUMBER ? 
            blanks.substring(0, COLS_LINE_NUMBER - ln.length()) : "") + 
          ln + ". " +
          extractBraces(s, "statement="), statementWidth); 
        lines = (lines + 1) % linesPerTitle;
        if (lines == 0)
          return title + variablesToString(s);
        else
          return variablesToString(s);
      }
      else if (s.startsWith("simulation terminated")) {
        i = s.indexOf(",");
        return "\n" + s.substring(0, i+1) + "\n" +
               s.substring(i+1) + "\n\n";
      }
      else
        return "";
    }
  }

  // Filter string s and return new string or "" to ignore
  public String filterSimulationx(String s) {
    try {
      // Variables and queues start with double tab
      if (s.startsWith("\t\t"))
        return filterVariable(s);

      // Filter statements
      else if ((s.indexOf("proc") != -1) && (s.indexOf("line") != -1))
        return filterStatement(s);

      // Display some messages unmodified
      else if ((s.startsWith("pan:")) || 
               (s.startsWith("spin:")) ||  
               (s.startsWith("tl_spin:")) ||
               (s.startsWith("Error:")) ||
               (s.startsWith("error:")) )
        return s + "\n";
      else if (s.indexOf("<<<<<") != -1)
        return s + "\n";

      // Display choices that have no proc and line number
      else if (s.indexOf("choice") != -1)
        return s.substring(s.indexOf("choice")) + "\n";

      // Display error count with bullets to emphasize
      else if (s.indexOf("errors:") != -1)
        return 
          s.substring(0, s.indexOf("errors:")) + "\u2022\u2022\u2022 " + 
          s.substring(s.indexOf("errors:")) + " \u2022\u2022\u2022\n";

      // Display MSC lines (without MSC:) if msc is true
      else if (msc && s.trim().startsWith("MSC:"))
          return s.substring(s.indexOf("MSC:")+5) + "\n";

      // Display printf lines if msc is false
      else if (!msc && !s.equals(""))
        return s + "\n";
      else
        return "";
    } catch (Exception e) {
        System.err.println("\n + Error in filter for:\n" + s + "\n");
        e.printStackTrace();
        return "";
    }
  }

  // Filter variables (including queues)
  private String filterVariable(String s) {
    String varName, varValue;
    if (s.startsWith("\t\tqueue")) {
      varName = s.substring(s.indexOf('(')+1, s.indexOf(')'));
      varValue = s.substring(s.indexOf(':')+1).trim();
    } 
    else {
      varName = s.substring(2, s.indexOf('=')-1); 
      varValue = s.substring(s.indexOf('=')+1).trim();
    }
    if (checkExcluded(varName, true)) return "";
    // Construct new title if new variables encountered
    boolean newTitle = !variables.containsKey(varName); 
    variables.put(varName, varValue);
    if (newTitle) {
        title = formatItem(processTitle, processWidth)     + " " +
                formatItem(statementTitle, statementWidth) + " " + 
                collectionToString(variables.keySet()) + "\n";
        lines = -1;
    }
    return "";
  }

  // Filter statements
  private String filterStatement(String s) {
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
      u = formatItem(proc, processWidth)         + " " + 
          formatItem(statement, statementWidth)  + " " + 
          collectionToString(variables.values()) + "\n";

      // Display title if needed
      lines = (lines + 1) % linesPerTitle;
      if (lines == 0) u = title + u;
    }
    return u;
  }
  
  // Strip balanced parentheses
  private String strip(String s) {
    while ( (s.charAt(0)=='(') && (s.charAt(s.length()-1)==')'))
            s = s.substring(1, s.length()-1);
    return s;
  }

	/** 
    Format a variable name or value s into a field of length len
    If item is too long, shorten it
        But, for array variables, shorten the name, not the index
		If item is too short, pad it
  */
	private String formatItem(String s, int len) {
		if (s.length() > len) { 
			if (s.indexOf("[") != -1) { 
				String subscript = 
					s.substring(s.indexOf("["), s.lastIndexOf("]")+1);
          if (subscript.length() < len)
            return s.substring(0, len-subscript.length()) + subscript;
          else
            return s.substring(0, len);
			}
			else
				return s.substring(0,len);
		}
		else if (s.length() < len)
			return (s + "                         ").substring(0,len);
		else
			return s;
	}

	/** Transform a collection of variables to a string, 
      calling formatItem for each element
  */
	private String collectionToString(Collection<String> c) {
		String s = "";
		Iterator<String> it = c.iterator();
		while (it.hasNext()) 
			s = s + formatItem(it.next(), variableWidth) + " ";
		return s;
	}	

	private void storeVariables(String s) {
    Collection<String> c = variables.keySet();
		String t, num;
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
      t = it.next();
      num = extract(s, t + "=");
      variables.put(t, num);
    }
	}	

	private String variablesToString(String s) {
    Collection<String> c = variables.keySet();
		String t = process + " " + statement + " ";
		Iterator<String> it = c.iterator();
		while (it.hasNext())
			t = t + formatItem(variables.get(it.next()), variableWidth) + " ";
		return t + "\n";
	}
}
