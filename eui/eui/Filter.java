// Copyright 2004-12 by Mordechai (Moti) Ben-Ari. See copyright.txt.
/**
 Filter Erigone output.
*/

package eui;
import java.util.*;
public class Filter {
  private Properties properties;

  private int processWidth;    // Widths of fields
  private int variableWidth;
  private int statementWidth;
  private int linesPerTitle;   // Lines between each title
  private int lines;           // Line counter to redisplay title

  private String  title;       // Title string
  private String  varTitle;    // Title string, just variables
  private String  process;     // Process string
  private String  statement;   // Statement string

  // Since filter is called separately for each line,
  //   boolean variables are needed to retain state
  private boolean program;     // Program listing ended (for efficiency)
  private boolean acceptance;  // Start of acceptance cycle found
  private boolean transitions; // End of transitions, skip accept list
  private boolean mtype;       // Parsing mtypes
  private boolean newprocess;  // Don't display new processes from "run"

  // Map from variable names to values
  //   to store complete states
  private TreeMap<String, String> variables = new TreeMap<String, String>();

  // Mtype values (names)
  private ArrayList<String> mtypes        = new ArrayList<String>();

  // Excluded variable and statements
  private ArrayList<String> excludedVar   = new ArrayList<String>();
  private ArrayList<String> excludedState = new ArrayList<String>();

	// Initialize variables and local copies of properties
	public void init (Properties properties) {
		variables.clear();
		title       = "";
		lines       = -1;
    program     = true;
    transitions = false;
    acceptance  = false;
    mtype       = false;
    newprocess  = false;
    this.properties = properties;
    processWidth    = Config.getIntProperty("PROCESS_WIDTH");
    variableWidth   = Config.getIntProperty("VARIABLE_WIDTH");
    statementWidth  = Config.getIntProperty("STATEMENT_WIDTH");
    linesPerTitle   = Config.getIntProperty("LINES_PER_TITLE");
    process   = formatItem("", processWidth,   true);
    statement = formatItem("", statementWidth, true);
	}

  // Parse string to initialize excluded processes and variables
  //   exVar is true for variables, false for processes
  public void setExcluded(String s, boolean exVar) {
    ArrayList<String> excluded = exVar ? excludedVar : excludedState;
    // Replace whitespace by separator
    s = s.replaceAll("[\t\n\\x0B\f\r]", Config.SEPARATOR) + Config.SEPARATOR;
    excluded.clear();
    do {
      int nl = s.indexOf(Config.SEPARATOR);
      if (nl == -1) break;
      if (!s.substring(0,nl).equals("")) {
        excluded.add(s.substring(0, nl));
      }
      s = s.substring(nl+1);
    } while (true);
  }

  // Check if name is excluded variable (exVar=true) or statement (exVar=false)
  //   For each excluded string S:
  //     If variable name includes S, do not display,
  //     but if some +T in file is in name, display anyway.
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

  // Filter strings for compilation
  public String filterCompilation(String s) {
    // Copyright and version line
    if (s.startsWith("Erigone"))
      return s + "\n";
    // Execution mode
    else if (s.startsWith("execution mode="))
      return s.substring(0, s.indexOf(",")+1) + "\n\n";
    // Compilation error
    else if (s.startsWith("message="))
      return extract(s, "message=") + "\n";

    // Table of variables
    else if (s.startsWith("variables="))
      return Config.SYMBOL_TITLE + "\n";
    else if (s.startsWith("name=")) {
      // Display a variable: type, name, length
      String type = extract(s, "type=");
      type = type.substring(0, type.indexOf("_"));
      return
        formatItem(type, 7, true) + " " + 
        formatItem(extract(s, "length="), 7, true) + " " +
        formatItem(
          extract(s, "name="), Config.SYMBOL_WIDTH, true) + "\n";
    }

    // Display mtype list
    else if (s.startsWith("mtypes=")) {
      mtype = true;
      mtypes.clear();
      mtypes.add("dummy");
      return "\n" + Config.MTYPE_TITLE + "\n";
    }
    else if (mtype && s.startsWith("offset=")) {
      int offset = extractNum(s, "offset=");
      mtypes.add(offset, extract(s, "value="));
      return "";
    }
    else if (s.startsWith("mtype table end=")) {
      mtype = false;
      String s1 =
        "1=" + mtypes.get(1).substring(1,mtypes.get(1).length()-1);
      for (int i = 2; i < mtypes.size(); i++)
        s1 = s1 + ", " + i + "=" + 
             mtypes.get(i).substring(1,mtypes.get(i).length()-1);
      return s1 + "\n";
    }

    // Table of processes
    else if (s.startsWith("processes="))
      return "\n" + Config.PROCESSES_TITLE1 + "\n" + Config.FLAGS + "\n";
    else if (s.startsWith("process=") && !transitions) {
      // Display number of transitions per process
      return "\n" + s + "\n" + Config.PROCESSES_TITLE2 + "\n";
    }
    else if (s.startsWith("number=")) {
      // Display transition source->target, statement and
      //   flags: A=atomic, e=end, a=accept
      return
        formatItem(
          extract(s, "source=") + "->" + 
          extract(s, "target="), 10, true) + " " +
        formatItem(
          (extract(s, "atomic=").equals("1") ? "A" : "-") +
          (extract(s, "end=").equals("1")    ? "e" : "-") +
          (extract(s, "accept=").equals("1") ? "a" : "-"),
            6, true) + " " +
        formatItem(
          extract(s, "line="), Config.COLS_LINE_NUMBER, false) + ". " +
        formatItem(extractBraces(s, "statement="),
          Config.getIntProperty("STATEMENT_WIDTH"), true) + "\n";
    }
    else if (s.startsWith("transitions end=")) {
      transitions = true;
      return "";
    }
    // Display compilation time
    else if (s.startsWith("times="))
      return "\n" + s + "\n";
    else
      return "";
  }

  // Filter strings from verification
  public String filterVerification(String s) {
    int i;
    // Display version and copyright message
    if (s.startsWith("Erigone"))
      return s + "\n";
    // Display execution mode
    else if (s.startsWith("execution mode=")) {
      i = s.indexOf(",");
      return s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n";
    }
    else if (s.startsWith("exception="))
      return "";

    // Display termination message
    else if (s.startsWith("verification terminated=successfully,")) {
      return "\n" + s + "\n\n";
    }
    else if (s.startsWith("verification terminated=")) {
      boolean counter =
        (s.indexOf("invalid end state")         != -1) ||
        (s.indexOf("never claim terminated")    != -1) ||
        (s.indexOf("acceptance cycle")          != -1) ||
        (s.indexOf("assert statement is false") != -1);
      i = s.indexOf(",");
      return "\n" + s.substring(0, i+1) + "\n" +
             s.substring(i+1) + "\n" +
             (counter ? Config.RUN_GUIDED : "") + "\n";
    }

    else
      return s + "\n";
  }

  // Create title needs to be called also after "run"
  private void createTitle() {
    varTitle = collectionToString(variables.keySet());
    title = "\n" +
      formatItem(
        Config.PROCESS_TITLE, processWidth, true) + " " +
      formatItem(
        Config.STATEMENT_TITLE, statementWidth, true) + " " + 
      varTitle + "\n";
  }  

  // Filter strings from simulation
  // The flag "program" is used so that all options need not be
  //   checked during execution
  // The flag "newprocess" is used when displaying the result
  //   of executing a "run" command
  public String filterSimulation(String s) {
    int i;
    // Display data from program
    if (program) {
      // Display copyright and version message
      if (s.startsWith("Erigone"))
          return s + "\n";
      // Display execution mode
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
      // Store channels that are not excluded
      else if (s.startsWith("index=")) {
        if (extract(s, "buffer_size=").equals("0"))
          return "";
        String varName = Config.CHANNEL_PREFIX + extract(s, "index=");
        if (!checkExcluded(varName, true))
          variables.put(varName, "");
        return "";
      }

      // Current not used because Erigone does not yet use mtypes
      //   in display of simulation states
      // Initialize mtypes
      //   Dummy entry needed since mtypes start with 1
      else if (s.startsWith("mtypes=")) {
        mtype = true;
        mtypes.clear();
        mtypes.add("dummy");
        return "";
      }
      // Store an mtype value
      else if (mtype && s.startsWith("offset=")) {
        int offset = extractNum(s, "offset=");
        mtypes.add(offset, extract(s, "value="));
        return "";
      }
      else if (s.startsWith("mtype table end=")) {
        mtype = false;
        return "";
      }

      // Create title after all variables and channels read
      else if (s.startsWith("data size=")) {
        createTitle();
        return "";
      }
      else if (s.startsWith("transitions end=")) {
        program = false;
        return "";
      }
      else
        return "";
    }

    // Display data from execution
    else {
      // Store state; it is displayed after the transition is chosen
      if (s.startsWith("next state=") ||
          s.startsWith("initial state=")) {
        storeVariables(s);
        return "";
      }
      // Display scenario line with chosen process
      else if (s.startsWith("process=") && !newprocess) {
        String st = extractBraces(s, "statement=");
        if (checkExcluded(st, false)) return "";
        String ln = formatItem(
          extract(s, "line="), Config.COLS_LINE_NUMBER, false);
        process   = formatItem(
          extract(s, "process="), processWidth, true);
        statement = formatItem(
          ln + " " + st, statementWidth, true);

        // Display title if needed
        lines = (lines + 1) % linesPerTitle;
        String prefix = (lines == 0 ? title : "");

        // Display start of acceptance cycle if needed
        String suffix = (acceptance ? "start of acceptance cycle\n" : "");
        acceptance = false;
        return prefix + variablesToString(true) + suffix;
      }
      else if (s.startsWith("start of acceptance cycle=")) {
        acceptance = true;
        return "";
      }

      // New symbols and processes from "run" command
      else if (s.startsWith("new symbol=")) {
        i = s.indexOf("type=");
        String varName = extract(s, "name=");
        // If name is Proc_NN.Var, exclude the formal name Proc.Var
        String varNameFormal =
                  varName.substring(0, varName.indexOf("_")) + 
                  varName.substring(varName.indexOf("."));
        if (variables.containsKey(varNameFormal))
          variables.remove(varNameFormal);
        if (!excludedVar.contains(varName))
          variables.put(varName, "");
        createTitle();
        return s.substring(0, i) + "\n";
      }
      else if (s.startsWith("new copy of process=")) {
        newprocess = true;
        return s + "\n";
      }
      else if (newprocess) {
        if (s.startsWith("process=")) {
          i = s.indexOf("initial=");
          lines = 0;
          return s.substring(0, i) + title;
        }
        else if (s.startsWith("new copy end=")) {
          newprocess = false;
          return "";
        }
        else
          return "";
      }

      // Termination message
      else if (s.startsWith("simulation terminated=")) {
        i = s.indexOf(",");
        return
          variablesToString(false) +
          "\n" + s.substring(0, i+1) + "\n" +
          s.substring(i+1) + "\n\n";
      }
      else if (s.startsWith("chosen transition="))
        return "";
      // Display of printf output
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

  // Format a variable name or value s into a field of length len
  // If an item is too long, shorten it
  // But, for array variables, shorten the name, not the index
  // If an item is too short, pad it
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
			return s + Config.BLANKS.substring(0,len-s.length());
		else if (!left && (s.length() < len))
			return Config.BLANKS.substring(0,len-s.length()) + s;
		else
			return s;
	}

	// Transform a collection of variables to a string, 
  //   calling formatItem for each element
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
      if (num.equals("")) continue;
      if (num.charAt(0) == '{')
        num = extractBraces(s, t + "=");
      variables.put(t, num);
    }
	}

  // Return a string with all the values of the variables
  // If processes is true, write the process and statement
  //   otherwise pad with blanks
	public String variablesToString(boolean processes) {
    Collection<String> c = variables.keySet();
    String t;

    if (processes)
      t = process + " " + statement + " ";
    else {
      char[] ch = new char[processWidth + statementWidth + 2];
      Arrays.fill(ch, ' ');
		  t = new String(ch);
		}
		Iterator<String> it = c.iterator();
		while (it.hasNext())
			t = t + formatItem(
                variables.get(it.next()), variableWidth, false) + " ";
		return t + "\n";
	}

  // Return variables title for use in interactive popup
  public String getTitle() {
    return varTitle;
  }
}
