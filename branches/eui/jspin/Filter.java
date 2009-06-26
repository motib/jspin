// Copyright 2004-8 by Mordechai (Moti) Ben-Ari. See copyright.txt.
/**
 Filter Spin output.
 Display a table: process, statement, variables.
*/
package jspin;
import java.util.*;
public class Filter {
    // Number of columns for a line number
    private static final int COLS_LINE_NUMBER = 2;
    private static final String blanks = "                             ";

    private Properties properties;

    private int processWidth;  // Widths of fields
    private int variableWidth;
    private int statementWidth;
    private int linesPerTitle; // Lines between each title
    private int     lines;		 // Line counter to redisplay title

    private String  title;     // Title string
    private String  varTitle;  // Title string, just variables
    private String  process;   // Process string
    private String  statement; // Statement string

    private boolean program;   // Flag for end of program listing
    private boolean buchi;     // Flag for Buchi automaton
    private boolean optbuchi;  // Flag for optimized Buchi automaton
    private boolean ltlonly;   // Flag for only ltl

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
    program  = true;
    buchi    = true;
    optbuchi = false;
    this.properties = properties;
    processWidth    = Integer.valueOf(properties.getProperty("PROCESS_WIDTH"));
    variableWidth   = Integer.valueOf(properties.getProperty("VARIABLE_WIDTH"));
    statementWidth  = Integer.valueOf(properties.getProperty("STATEMENT_WIDTH"));
    linesPerTitle   = Integer.valueOf(properties.getProperty("LINES_PER_TITLE"));
    process   = formatItem("", processWidth,   true);
    statement = formatItem("", statementWidth, true);
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

  /**
    Filter strings from compilation
      Show copyright, mode, error messages, table of variables and
      transitions
  */
  public String filterCompilation(String s) {
    int w = Config.SYMBOL_WIDTH;   // Rename for convenience
    if (s.startsWith("Erigone"))
        return s + "\n";
    else if (s.startsWith("execution mode="))
      return s.substring(0, s.indexOf(",")+1) + "\n\n";
    else if (s.startsWith("message="))
      return "Compilation error\n" + extract(s, "message=") + "\n";
    else if (s.startsWith("variables="))
      return Config.SYMBOL_TITLE + "\n";
    else if (s.startsWith("name=")) {
      // Display a variable: type, name, length
      String type = extract(s, "type=");
      type = type.substring(0, type.indexOf("_"));
      return
        formatItem(type,                  w, true) + " " + 
        formatItem(extract(s, "name="),   w, true) + " " +
        formatItem(extract(s, "length="), w, true) + "\n";
    }
    else if (s.startsWith("processes="))
      return "\n" + Config.PROCESSES_TITLE + "\n";
    else if (s.startsWith("process=")) {
      // Display number of transitions per process
      return
        formatItem(extract(s, "process="),     w, true) + " " + 
        formatItem(extract(s, "transitions="), w, true) + "\n";
    }
    else if (s.startsWith("times="))
      return "\n" + s + "\n";
    else
      return "";
  }

  /**
    Filter strings from verification
      Show copyright, mode, (optimized) BA, termination message
      The flag "buchi" is used to skip display of unoptimized BA
      The flag "optbuchi" is used to display the optimized BA
      The flag "ltlonly" is used to add a new line if LTL_ONLY
  */
  public String filterVerification(String s) {
    int i;
    if (s.startsWith("Erigone"))
      return s + "\n";
    else if (s.startsWith("execution mode=")) {
      i = s.indexOf(",");
      ltlonly = extract(s, "execution mode=").equals("ltl_only");
      return s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n";
    }
    else if (s.startsWith("optimized buchi automaton start=")) {
      buchi = false;
      optbuchi = true;
      return "\n" + Config.BUCHI_TITLE + "\n";
    }
    else if (s.startsWith("optimized buchi automaton end=")) {
      optbuchi = false;
      return (ltlonly ? "\n" : "");
    }
    else if (s.startsWith("verification terminated=successfully,")) {
      return "\n" + s + "\n\n";
    }
    else if (s.startsWith("verification terminated=")) {
      buchi = false;
      i = s.indexOf(",");
      return "\n" + s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n\n";
    }
    else if (optbuchi)
      return filterTranslation(s);
    else if (buchi)
      return "";
    else
      return s + "\n";
  }

  /**
    Filter strings from LTL2BA translation
      Call from filterVerification
      Display transition source->target, statement and
        flags: A=atomic, e=end, a=accept
  */
  public String filterTranslation(String s) {
    int w = Config.SYMBOL_WIDTH;   // Rename for convenience
    return
      formatItem(extract(s, "source=") +"->" + 
                 extract(s, "target="), w, true) + " " +
      formatItem(
        (extract(s, "atomic=").equals("1") ? "A" : "-") +
        (extract(s, "end=").equals("1") ? "e" : "-") +
        (extract(s, "accept=").equals("1") ? "a" : "-"),
        w, true) + " " +
      formatItem(extractBraces(s, "statement="),
        Config.getIntProperty("STATEMENT_WIDTH"), true) + "\n";
  }

  /**
    Filter strings from simulation
      Show copyright, mode, scenario table and simulation message
      The flag "program" is used so that all options need not be
        checked during execution
  */
  public String filterSimulation(String s) {
    int i;
    if (program) {
      if (s.startsWith("Erigone"))
          return s + "\n";
      else if (s.startsWith("execution mode=")) {
        i = s.indexOf(",");
        return s.substring(0, i+1) + "\n" +
               s.substring(i+1) + "\n";
      }
      // Store variables that are not excluded
      else if (s.startsWith("name=")) {
        String varName = extract(s, "name=");
        if (!checkExcluded(varName, true))
          variables.put(varName, "");
        return "";
      }
      // Create title after all variables read
      else if (s.startsWith("symbol table end=")) {
        varTitle = collectionToString(variables.keySet());
        title = formatItem(Config.PROCESS_TITLE,   processWidth,   true) + " " +
                formatItem(Config.STATEMENT_TITLE, statementWidth, true) + " " + 
                varTitle + "\n";
        return "\n";
      }
      else if (s.startsWith("transitions end=")) {
        program = false;
        return "";
      }
      else
        return "";
    }
    else {
      // Display data from execution
      if (s.startsWith("next state=") || s.startsWith("initial state=")) {
        // Store state; it is display after the transition is chosen
        storeVariables(s);
        return "";
      }
      else if (s.startsWith("process=")) {
        // Display scenario line with chosen process
        String st = extractBraces(s, "statement=");
        if (checkExcluded(st, false)) return "";
        String ln = formatItem(extract(s, "line="), COLS_LINE_NUMBER, false);
        process   = formatItem(extract(s, "process="), processWidth, true);
        statement = formatItem(ln + " " + st, statementWidth, true);
        // Display title is needed
        lines = (lines + 1) % linesPerTitle;
        if (lines == 0)
          return title + variablesToString(true);
        else
          return variablesToString(true);
      }
      else if (s.startsWith("simulation terminated")) {
        i = s.indexOf(",");
        return "\n" + s.substring(0, i+1) + "\n" +
               s.substring(i+1) + "\n\n";
      }
      else if (s.startsWith("chosen transition="))
        return "";
      else
        return s + "\n";
    }
  }

  // Extract value from named association: "name=value,"
  public static String extract(String s, String pattern) {
    int i = s.indexOf(pattern);
    if (i == -1) return "";
    i = i + pattern.length();
    return s.substring(i, s.indexOf(",", i+1));
  }

  // Extract value from named association: "name={value},"
  public static String extractBraces(String s, String pattern) {
    int i = s.indexOf(pattern);
    int j = s.indexOf('}');
    if (i == -1) return "";
    i = i + pattern.length() + 1;
    if (j == i)
      return "";
    else
      return s.substring(i, j);
  }

  // Extract numeric value from named association: "name=value,"
  public static int extractNum(String s, String pattern) {
    int i = s.indexOf(pattern);
    if (i == -1) return -1;
    i = i + pattern.length();
    String t = s.substring(i, s.indexOf(",", i+1));
    try {
      return Integer.parseInt(t);
    }
    catch(NumberFormatException e) {
      return -1;
    }
  }

	/** 
    Format a variable name or value s into a field of length len
    If item is too long, shorten it
        But, for array variables, shorten the name, not the index
		If item is too short, pad it
  */
	private String formatItem(String s, int len, boolean left) {
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
		else if (left && (s.length() < len))
			return s + blanks.substring(0,len-s.length());
		else if (!left && (s.length() < len))
			return blanks.substring(0,len-s.length()) + s;
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
			s = s + formatItem(it.next(), variableWidth, false) + " ";
		return s;
	}	

  // Store values of variables
  // All the names are given in the variables table
	public void storeVariables(String s) {
    Collection<String> c = variables.keySet();
		String t, num;
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
      t = it.next();
      num = extract(s, t + "=");
      if (num.charAt(0) == '{')
        num = extractBraces(s, t + "=");
      variables.put(t, num);
    }
	}	

  // Return a string with all the values of the variables
	public String variablesToString(boolean processes) {
    Collection<String> c = variables.keySet();
		String t = (processes ? process + " " + statement + " " : "");
		Iterator<String> it = c.iterator();
		while (it.hasNext())
			t = t + formatItem(variables.get(it.next()), variableWidth, false) + " ";
		return t + "\n";
	}

  // Return variables title for use in interactive popup
  public String getTitle() {
    return varTitle;
  }
}