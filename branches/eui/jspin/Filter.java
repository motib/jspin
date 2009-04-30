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

    // Properties and copies of the data from the properties
    private        Properties properties;

    private static int processWidth;
    private static int variableWidth;
    private static int statementWidth;
    private static int linesPerTitle;

    private String  title;     // Title string
    private String  varTitle;  // Title string, just variables
    private String  process;   // Process string
    private String  statement; // Statement string
    private int     lines;		 // Line counter to redisplay title
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

  public String filterTranslation(String s) {
    int w = Config.SYMBOL_WIDTH;
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

  public String filterSimulation(String s) {
    int i;
    // Flag for program message so don't have to check all options
    //   during execution
    if (program) {
      if (s.startsWith("Erigone"))
          return s + "\n";
      else if (s.startsWith("execution mode=")) {
        i = s.indexOf(",");
        return s.substring(0, i+1) + "\n" +
               s.substring(i+1) + "\n";
      }
      else if (s.startsWith("type=")) {
        String varName = extract(s, "name=");
        if (!checkExcluded(varName, true))
          variables.put(varName, "");
        return "";
      }
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
      if (s.startsWith("next state=") || s.startsWith("initial state=")) {
        storeVariables(s);
        return "";
      }
      else if (s.startsWith("process=")) {
        String st = extractBraces(s, "statement=");
        if (checkExcluded(st, false)) return "";
        String ln = formatItem(extract(s, "line="), COLS_LINE_NUMBER, false);
        process   = formatItem(extract(s, "process="), processWidth, true);
        statement = formatItem(ln + " " + st, statementWidth, true); 
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

  public String filterCompilation(String s) {
    int w = Config.SYMBOL_WIDTH;
    if (s.startsWith("Erigone"))
        return s + "\n";
    else if (s.startsWith("execution mode="))
      return s.substring(0, s.indexOf(",")+1) + "\n\n";
    else if (s.startsWith("message="))
      return "Compilation error\n" + extract(s, "message=") + "\n";
    else if (s.startsWith("variables="))
      return Config.SYMBOL_TITLE + "\n";
    else if (s.startsWith("type=")) {
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
      return
        formatItem(extract(s, "process="),     w, true) + " " + 
        formatItem(extract(s, "transitions="), w, true) + "\n";
    }
    else if (s.startsWith("times="))
      return "\n" + s + "\n";
    else
      return "";
  }

  public static String extract(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
    return s.substring(i, s.indexOf(",", i+1));
  }

  public static String extractBraces(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
    int j = s.indexOf('}');
    if (j == i+1) return ""; else return s.substring(i+1, j);
  }

  public static int extractNum(String s, String pattern) {
    int i = s.indexOf(pattern) + pattern.length();
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

  public String getTitle() {
    return varTitle;
  }

	public String variablesToString(boolean processes) {
    Collection<String> c = variables.keySet();
		String t = (processes ? process + " " + statement + " " : "");
		Iterator<String> it = c.iterator();
		while (it.hasNext())
			t = t + formatItem(variables.get(it.next()), variableWidth, false) + " ";
		return t + "\n";
	}
}
