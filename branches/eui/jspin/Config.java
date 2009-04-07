/* Copyright 2003-8 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
package jspin;
import java.io.*;
import java.awt.event.*;
import java.util.*;

public class Config {
  static Properties properties = new Properties();
  // Where to find configuration file
  public static String installationDirectory;
  public static String currentDirectory;
  public static String helpFileName;
  public static String aboutFileName;
  public static final char sep = java.io.File.separatorChar;  // shortcut

	// Strings
    static final String SOFTWARE_NAME    = "EUI - Erigone User Interface, Version 0.1";
    static final String JAVA_VERSION     = "1.5";
    static final String CONFIG_FILE_NAME = "config.cfg";
    static final String SPIDER_TITLE     = "SpinSpider";
    static final String OPTIONS_TITLE    = "Display";
    static final String SELECT  	       = "Select a statement";
    static final String OPEN_FILE 	     = "Open a Promela file\n";
	  static final String processTitle     = "Process ";
    static final String statementTitle   = "Statement ";
    static final String SEPARATOR        = "#";

    static void setDefaultProperties() {
        // Directories and file names
        properties.put("SOURCE_DIRECTORY", "jspin-examples");
        properties.put("ERIGONE",          "erigone");
        properties.put("DOT",              "bin" + sep + "dot.exe");
        properties.put("HELP_FILE_NAME",   "txt" + sep + "help.txt");
        properties.put("ABOUT_FILE_NAME",  "txt" + sep + "copyright.txt");

        // Options for executing Spin
        properties.put("SINGLE_QUOTE", Boolean.toString(false));
        properties.put("COMMON_OPTIONS",      "-d");

        // Settings
        properties.put("MAX_STEPS",      "10");
        properties.put("SEED",           "0");
        properties.put("FAIRNESS",       Boolean.toString(true));
        properties.put("VERIFY_MODE",    Safety);
        properties.put("RAW",            Boolean.toString(false));
        properties.put("PROCESS_WIDTH",  Integer.toString(7));
        properties.put("STATEMENT_WIDTH",Integer.toString(18));
        properties.put("VARIABLE_WIDTH", Integer.toString(10));
        properties.put("LINES_PER_TITLE",Integer.toString(20));

		    // Size of main frame
        properties.put("WIDTH", Integer.toString(1000));
        properties.put("HEIGHT", Integer.toString(700));

		    // Select dialog
        properties.put("SELECT_BUTTON", Integer.toString(220)); 
        properties.put("SELECT_HEIGHT", Integer.toString(70));
        properties.put("SELECT_MENU",   Integer.toString(5));
        properties.put("UNEXECUTABLE",  Boolean.toString(false));

		    // Location of dividers in JSplitPanes: Left-right and top-bottom
        properties.put("LR_DIVIDER", Integer.toString(400));
        properties.put("TB_DIVIDER", Integer.toString(500));
        properties.put("MIN_DIVIDER", Integer.toString(50));

        // Font
        properties.put("FONT_FAMILY", "Lucida Sans Typewriter");
        properties.put("FONT_STYLE", Integer.toString(java.awt.Font.PLAIN));
        properties.put("FONT_SIZE",  Integer.toString(14));
		
        // Tab size in editor
        properties.put("TAB_SIZE", Integer.toString(4));
		
        // Display of Spin output
        properties.put("WRAP", Boolean.toString(true));
        properties.put("MSC",  Boolean.toString(false));
        properties.put("PROCESS_TITLE",   "Process ");
        properties.put("STATEMENT_TITLE", "Statement ");
		
        // Delay while waiting for user input
        properties.put("POLLING_DELAY", Integer.toString(200));
    }

    // Component names and mnemonics
    static final String File 		= "File";
    static final int    FileMN		= KeyEvent.VK_F;
    static final String New			= "New";
    static final int    NewMN       = KeyEvent.VK_N;
    static final String Open        = "Open";
    static final int    OpenMN      = KeyEvent.VK_O;
    static final String Save        = "Save";
    static final int    SaveMN      = KeyEvent.VK_S;
    static final String SaveAs   	= "Save as";
    static final int    SaveAsMN    = KeyEvent.VK_A;
    static final String Switch      = "Switch file";
    static final int    SwitchMN    = KeyEvent.VK_F;
    static final String Exit       	= "Exit";
    static final int    ExitMN      = KeyEvent.VK_X;

    static final String Editor      = "Edit";
    static final int    EditorMN  	= KeyEvent.VK_E;
    static final String Undo       	= "Undo";
    static final int    UndoMN 		= KeyEvent.VK_U;
    static final String Redo       	= "Redo";
    static final int    RedoMN      = KeyEvent.VK_R;
    static final String Copy       	= "Copy";
    static final int    CopyMN      = KeyEvent.VK_C;
    static final String Cut         = "Cut";
    static final int    CutMN       = KeyEvent.VK_T;
    static final String Paste       = "Paste";
    static final int    PasteMN   	= KeyEvent.VK_P;
    static final String Find       	= "Find";
    static final int    FindMN     	= KeyEvent.VK_F;
    static final String FindAgain  	= "Find again";
    static final int    FindAgainMN = KeyEvent.VK_A;

    static final String Spin        = "Run";
    static final int    SpinMN      = KeyEvent.VK_U;
    static final String Check		    = "Compile";
    static final int    CheckMN   	= KeyEvent.VK_C;
    static final String Random    	= "Random";
    static final int    RandomMN   	= KeyEvent.VK_R;
    static final String Inter		    = "Interactive";
    static final int    InterMN		  = KeyEvent.VK_I;
    static final String LTL2BA    	= "LTL2BA";
    static final int    LTL2BAMN  	= KeyEvent.VK_L;
    static final String Safety    	= "Safety";
    static final int    SafetyMN  	= KeyEvent.VK_S;
    static final String Acceptance   = "Accept";
    static final int    AcceptanceMN = KeyEvent.VK_A;
    static final String Fair    	  = "Fairness";
    static final int    FairMN  	  = KeyEvent.VK_N;
    static final String Trail       = "Trail";
    static final int    TrailMN  	  = KeyEvent.VK_T;
    static final String Stop       	= "Stop";
    static final int    StopMN   	  = KeyEvent.VK_P;

    static final String Settings   	= "Settings";
    static final int    SettingsMN  = KeyEvent.VK_G;
    static final String Common      = "Common";
    static final int    CommonMN  	= KeyEvent.VK_C;
    static final String Default   	= "Default";
    static final int    DefaultMN 	= KeyEvent.VK_D;
    static final String SaveInstall = "Save install";
    static final int    SaveInstallMN = KeyEvent.VK_L;
    static final String SaveCurrent   = "Save current";
    static final int    SaveCurrentMN = KeyEvent.VK_S;
    static final String MaxSteps    = "Max steps";
    static final int    MaxStepsMN  = KeyEvent.VK_M;
    static final String Seed        = "Seed";
    static final int    SeedMN      = KeyEvent.VK_S;
    static final String StWidth 	= "Statement width";
    static final int    StWidthMN  = KeyEvent.VK_S;
    static final String VarWidth 	= "Variable width";
    static final int    VarWidthMN  = KeyEvent.VK_V;
    static final String ExcludedV 	= "Exclude variables";
    static final int    ExcludedVMN = KeyEvent.VK_E;
    static final String ExcludedS 	= "Exclude statements";
    static final int    ExcludedSMN = KeyEvent.VK_X;

    static final String Output		= "Display";
    static final int    OutputMN   	= KeyEvent.VK_D;
    static final String SaveSpin    = "Save output";
    static final int    SaveSpinMN  = KeyEvent.VK_V;
    static final String Raw     	  = "Raw output";
    static final int    RawMN  	      = KeyEvent.VK_R;
    static final String DisplayRaw    = "Display raw";
    static final int    DisplayRawMN  = KeyEvent.VK_D;

    static final String Spider		    = "SpinSpider";
    static final int    SpiderMN   	    = KeyEvent.VK_D;
    static final String SpiderDisplay   = "Display debug";
    static final int    SpiderDisplayMN = KeyEvent.VK_L;
    static final String Run  		    = "Run";
    static final int    RunMN   	    = KeyEvent.VK_R;

    static final String Help		= "Help";
    static final int    HelpMN    	= KeyEvent.VK_H;
    static final String About    	= "About";
    static final int    AboutMN    	= KeyEvent.VK_A;

    static final String Max      	= "Maximize";
    static final int    MaxMN    	= KeyEvent.VK_M;

    static final String LTLFormula 		 = " LTL formula  ";
    static final int 	  LTL_COLUMNS    = 50;

    // Common options
  	static final String OK           = "OK";
    static final int    OKMN         = KeyEvent.VK_O;
    static final String Cancel       = "Cancel";
    static final String Statements   = "Statements";
    static final int    StatementsMN = KeyEvent.VK_S;
    static final String Globals      = "Globals";
    static final int    GlobalsMN    = KeyEvent.VK_G;
    static final String Locals       = "Locals";
    static final int    LocalsMN     = KeyEvent.VK_L;
    static final String Sent         = "Sent";
    static final int    SentMN       = KeyEvent.VK_T;
    static final String Received     = "Received";
    static final int    ReceivedMN   = KeyEvent.VK_R;
    static final String ClearAll     = "Clear all";
    static final int    ClearAllMN   = KeyEvent.VK_C;
    static final String SetAll       = "Set all";
    static final int    SetAllMN     = KeyEvent.VK_A;

    // SpinSpider options
    static final String NoTrail      = "No trail";
    static final int    NoTrailMN	 = KeyEvent.VK_T;
    static final String EmphTrail    = "Emphasize trail";
    static final int    EmphTrailMN	 = KeyEvent.VK_E;
    static final String OnlyTrail    = "Only trail";
    static final int    OnlyTrailMN	 = KeyEvent.VK_O;
    static final String Automata     = "Automata";
    static final int    AutomataMN	 = KeyEvent.VK_A;
    static final String Debug        = "Debug";
    static final int    DebugMN		 = KeyEvent.VK_D;
    static final String DotSize      = "Dot size";
    static final String DotSmall     = "Small";
    static final int    DotSmallMN	 = KeyEvent.VK_M;
    static final String DotLarge     = "Large";
    static final int    DotLargeMN	 = KeyEvent.VK_L;
    static final String TrailStyle   = "Trail style";
    static final String TrailColor   = "Color";
    static final int    TrailColorMN = KeyEvent.VK_C;
    static final String TrailBold    = "Bold";
    static final int    TrailBoldMN	 = KeyEvent.VK_B;
    static final String Processes    = "Processes";
    static final int    MAX_PROCESS  = 5;
    static final String Variables    = "Variables";
    static final String Format       = "Format";
    static final String DOT          = "dot";
    static final String PNG          = "png";
    static final String PS           = "ps";
    static final String FSM          = "fsm";

    // Accelerators
    // Select All by default            = "control A"
    static final String SwitchAC        = "control B";
    static final String CopyAC          = "control C";
    // static final String              = "control D";
    // static final String              = "control E";
    static final String FindAC     	    = "control F";
    static final String FindAgainAC     = "control G";
    // Backspace by default             = "control H"
    // static final String              = "control I";
    // static final String              = "control J";
    // static final String              = "control L";
    // static final String              = "control M";
    static final String NewAC           = "control N";
    static final String OpenAC          = "control O";
    static final String ExitAC     	    = "control Q";
    // static final String              = "control R";
    static final String SaveAC          = "control S";
    static final String SaveAsAC	      = "control T";
    // static final String              = "control U";
    static final String PasteAC         = "control V";
    // static final String 	            = "control W";
    static final String CutAC           = "control X";
    static final String RedoAC          = "control Y";
    static final String UndoAC          = "control Z";
    
    // Dummy accelerators
    static String 
        AboutAC, AcceptanceAC, CheckAC, CommonAC, DefaultAC, DisplayRawAC,
        ExcludedSAC, ExcludedVAC, FairAC, HelpAC, InterAC, 
        LTLClearAC, LTLLoadAC, LTLTranslateAC, LTL2BAAC,
        MaxAC, MaxStepsAC, NonProcessAC,
        OptionsCAC, OptionsInterAC, OptionsPanAC, OptionsRandomAC,
        OptionsSaveCurrentAC, OptionsSaveInstallAC, OptionsTrailAC, 
        RandomAC, RawAC,
        SafetyAC, SaveSpinAC, SeedAC, SpiderAC, SpiderDisplayAC,
        StWidthAC, StopAC, TrailAC, VarWidthAC, VerifyAC;

	// Initialize configuration file
    static public void init() {
        setDefaultProperties();
        // Try to open config file in current directory;
        //   if not there, try installation directory;
        //   if not there, write default file
        currentDirectory = System.getProperty("user.dir");
        installationDirectory = System.getProperty("java.class.path");
        int lastSeparator = installationDirectory.lastIndexOf(java.io.File.separator);
        if (lastSeparator == -1) 
          installationDirectory = ".";
        else
          installationDirectory = installationDirectory.substring(0, lastSeparator);
//        System.err.println(currentDirectory + java.io.File.separator + CONFIG_FILE_NAME);
//        System.err.println(installationDirectory + java.io.File.separator + CONFIG_FILE_NAME);
        FileInputStream in = null;
        try {
            in = new FileInputStream(
              currentDirectory + java.io.File.separator + CONFIG_FILE_NAME);
              System.err.println("Read configuration file from current directory");
        } catch (FileNotFoundException e1) {
          try {
            in = new FileInputStream(
              installationDirectory + java.io.File.separator + CONFIG_FILE_NAME);
            System.err.println("Read configuration file from installation directory");
          } catch (FileNotFoundException e4) {
            System.err.println(
				      "Cannot open configuration file, creating new file");
            try {
                saveFile(false);
                in = new FileInputStream(installationDirectory + java.io.File.separator + CONFIG_FILE_NAME);
            } catch (IOException e2) {
                System.err.println("Cannot write configuration file");
                return;
            }
          }
        }
        try {
            properties.load(in);
            in.close();
        } catch (IOException e3) {
            System.err.println("Cannot read configuration file");
        }
        helpFileName = installationDirectory + java.io.File.separator +
          properties.getProperty("HELP_FILE_NAME");
        aboutFileName = installationDirectory + java.io.File.separator +
          properties.getProperty("ABOUT_FILE_NAME");
    }

	// Save configuration file
    static void saveFile(boolean current) {
        try {
            FileOutputStream out = 
              new FileOutputStream(
                (current ? currentDirectory : installationDirectory)
                + java.io.File.separator + CONFIG_FILE_NAME);
            properties.store(out, "jSpin configuration file");
            out.close();
            System.err.println("Saved jSpin configuration file config.cfg");
        } catch (IOException e2) {
            System.err.println("Cannot write configuration file");
        }
    }

	// Interface to get/set propertyies of various types
    public static String getStringProperty(String s) {
      return properties.getProperty(s);
    }

    static void setStringProperty(String s, String newValue) {
        properties.setProperty(s, newValue);
    }

    public static boolean getBooleanProperty(String s) {
        return Boolean.valueOf(properties.getProperty(s)).booleanValue();
    }

    static void setBooleanProperty(String s, boolean newValue) {
        properties.setProperty(s, Boolean.toString(newValue));
    }

    public static int getIntProperty(String s) {
        return Integer.valueOf(properties.getProperty(s)).intValue();
    }

    static void setIntProperty(String s, int newValue) {
        properties.setProperty(s, Integer.toString(newValue));
    }
    
    public static Properties getProperties() {
    	return properties;
    }
}
