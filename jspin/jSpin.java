/*
 jSpin - Development environment for Spin
 Copyright 2003-8 by Mordechai (Moti) Ben-Ari.
 
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

package jspin;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class jSpin extends JFrame implements ActionListener {
	// Contained objects. Create only after initializing configuration.
    private Editor editor;
    private RunSpin runSpin;
    private Filter filter;
    private UndoRedo undoredo;
    private SpiderFile sp;
    private java.util.Properties spiderProperties;
    
	// User interface components
    private JTextArea editorArea = new JTextArea();
    private JTextArea trailArea = new JTextArea();
    private JTextArea messageArea = new JTextArea();
    private JTextField LTLField = new JTextField();

    private JFileChooser PMLfileChooser;
    private JFileChooser PRPfileChooser;
    private JFileChooser OUTfileChooser;
    private java.awt.Font font;

    private JScrollPane editorScrollPane = new JScrollPane(editorArea);
    private JScrollPane trailScrollPane = new JScrollPane(trailArea);
    private JScrollPane messageScrollPane = new JScrollPane(messageArea);
    private JSplitPane topSplitPane;
    private JSplitPane mainSplitPane;

    private JMenuBar menuBar = new JMenuBar();

    private JMenu menuFile = new JMenu();
    private JMenuItem menuItemNew = new JMenuItem(Config.New);
    private JMenuItem menuItemOpen = new JMenuItem(Config.Open);
    private JMenuItem menuItemSave = new JMenuItem(Config.Save);
    private JMenuItem menuItemSaveAs = new JMenuItem(Config.SaveAs);
    private JMenuItem menuItemSwitch = new JMenuItem(Config.Switch);
    private JMenuItem menuItemExit = new JMenuItem(Config.Exit);

    private JMenu menuEditor = new JMenu();
    private JMenuItem menuItemCopy = new JMenuItem(Config.Copy);
    private JMenuItem menuItemCut = new JMenuItem(Config.Cut);
    private JMenuItem menuItemPaste = new JMenuItem(Config.Paste);
    private JMenuItem menuItemFind = new JMenuItem(Config.Find);
    private JMenuItem menuItemFindAgain = new JMenuItem(Config.FindAgain);

    private JMenu menuSpin = new JMenu();
    private JMenuItem menuItemCheck = new JMenuItem(Config.Check);
    private JMenuItem menuItemRandom = new JMenuItem(Config.Random);
    private JMenuItem menuItemInter = new JMenuItem(Config.Inter);
    private JMenuItem menuItemTrail = new JMenuItem(Config.Trail);
    private JMenuItem menuItemLTL2BA = new JMenuItem(Config.LTL2BA);
    private JMenuItem menuItemSafety = new JMenuItem(Config.Safety);
    private JMenuItem menuItemAcceptance = new JMenuItem(Config.Acceptance);
    private JMenuItem menuItemFairness = new JMenuItem(Config.Fair);
    private JMenuItem menuItemStop = new JMenuItem(Config.Stop);
    private JMenuItem menuItemOptionsTrail = new JMenuItem(Config.Trail);
    private JMenuItem menuItemDefault = new JMenuItem(Config.Default);
    private JMenuItem menuItemOptionsSaveInstall = new JMenuItem(Config.SaveInstall);
    private JMenuItem menuItemOptionsSaveCurrent = new JMenuItem(Config.SaveCurrent);

    private JMenu menuOptions = new JMenu();
    private JMenuItem menuItemLimits = new JMenuItem(Config.Limits);
    private JMenuItem menuItemSeed = new JMenuItem(Config.Seed);

    private JMenu menuOutput = new JMenu();
    private JMenuItem menuItemMax = new JMenuItem(Config.Max);
    private JMenuItem menuItemVarWidth = new JMenuItem(Config.VarWidth);
    private JMenuItem menuItemStWidth = new JMenuItem(Config.StWidth);
    private JMenuItem menuItemExcludedV = new JMenuItem(Config.ExcludedV);
    private JMenuItem menuItemExcludedS = new JMenuItem(Config.ExcludedS);
    // private JCheckBoxMenuItem menuItemRaw = new JCheckBoxMenuItem(Config.Raw);
    // private JMenuItem menuItemDisplayRaw  = new JMenuItem(Config.DisplayRaw);
    private JMenuItem menuItemSaveSpin = new JMenuItem(Config.SaveSpin);

    private JMenu menuSpider = new JMenu();
    private JMenuItem menuItemSpider  = new JMenuItem(Config.Spider);
    private JMenuItem menuItemSpiderDisplay = new JMenuItem(Config.SpiderDisplay);

    private JMenu menuHelp = new JMenu();
    private JMenuItem menuItemHelp  = new JMenuItem(Config.Help);
    private JMenuItem menuItemAbout = new JMenuItem(Config.About);

    private JToolBar toolBar = new JToolBar();

    private JButton toolOpen   = new JButton(Config.Open);
    private JButton toolSave   = new JButton(Config.Save);
    private JButton toolCheck  = new JButton(Config.Check);
    private JButton toolRandom = new JButton(Config.Random);
    private JButton toolInter  = new JButton(Config.Inter);
    private JButton toolTrail  = new JButton(Config.Trail);
    private JButton toolLTL2BA = new JButton(Config.LTL2BA);
    private JButton toolSafety = new JButton(Config.Safety);
    private JButton toolAcceptance = new JButton(Config.Acceptance);
    private JButton toolFairness   = new JButton(Config.Fair);
    private JButton toolStop   = new JButton(Config.Stop);
    private JButton toolSpider = new JButton(Config.Spider);

    private JButton   toolMax = new JButton(Config.Max);
    private boolean   maxedDivider;
    private int       currentDivider;

    private JLabel LTLLabel = new JLabel(Config.LTLFormula);

    public void actionPerformed(ActionEvent e) {
        // File menu actions
        if (e.getSource() == menuItemNew) {
            editor.lastFile = editor.file;
            clearAreas();
            editor.newFile();
        }
        else if ((e.getSource() == menuItemOpen) || (e.getSource() == toolOpen)) {
            if(PMLfileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                editor.lastFile = editor.file;
                clearAreas();
                editor.openFile(PMLfileChooser.getSelectedFile(), true);
            }
        }
        else if ((e.getSource() == menuItemSave) || (e.getSource() == toolSave)) {
            if (editor.file != null)
                editor.saveFile(null);
            else {
                if(PMLfileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    editor.saveFile(PMLfileChooser.getSelectedFile());
                }
            }
        } 
        else if (e.getSource() == menuItemSaveAs) {
            if(PMLfileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                editor.lastFile = editor.file;
                editor.saveFile(PMLfileChooser.getSelectedFile());
        }
        else if (e.getSource() == menuItemSwitch) {
            if (editor.lastFile != null) {
                java.io.File temp = editor.lastFile;
                if (editor.file != null)
                    editor.saveFile(null);
                else {
                    if(PMLfileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        editor.saveFile(PMLfileChooser.getSelectedFile());
                    }
                }
                editor.lastFile = editor.file;
                editor.openFile(temp, true);
            }
        }
        else if (e.getSource() == menuItemExit) {
            runSpin.killSpin();
            System.exit(0);
        }

        // Editor menu actions
        else if (e.getSource() == menuItemCut)
            editor.cut();
        else if (e.getSource() == menuItemCopy)
            editor.copy();
        else if (e.getSource() == menuItemPaste)
            editor.paste();
        else if (e.getSource() == menuItemFind)
            editor.find();
        else if (e.getSource() == menuItemFindAgain)
            editor.findAgain();

        // Run menu actions
        else if ((e.getSource() == menuItemCheck) || (e.getSource() == toolCheck)) {
            runSpin.runAndWait(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("COMPILE_OPTIONS") + " " +
                editor.fileName);
        }
        else if ((e.getSource() == menuItemRandom) || (e.getSource() == toolRandom)) {
            runSpin.run(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("RANDOM_OPTIONS") + " " +
                (Config.getIntProperty("SEED") != 0 ? 
                   ("-n" + Config.getIntProperty("SEED") + " ") : "") +
                " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemInter) || (e.getSource() == toolInter)) {
            runSpin.run(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("INTERACTIVE_OPTIONS") + " " +
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemTrail) || (e.getSource() == toolTrail)) {
            runSpin.run(trailArea, false,
                Config.getStringProperty("ERIGONE"),  
                Config.getStringProperty("TRAIL_OPTIONS") + " " +
                " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemLTL2BA) || (e.getSource() == toolLTL2BA)) {
            runSpin.runAndWait(trailArea, false,
                Config.getStringProperty("ERIGONE"),  
                Config.getStringProperty("LTL2BA_OPTIONS") + " " +
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemSafety) || (e.getSource() == toolSafety)) {
            runSpin.runAndWait(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("SAFETY_OPTIONS") + " " +
                (!LTLField.getText().equals("") ? ("-t ") : "") +
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemAcceptance) || (e.getSource() == toolAcceptance)) {
            if (LTLField.getText().equals("")) {
              append(messageArea,
                     "Need LTL formula for acceptance verification\n");
              return;
            }
            runSpin.runAndWait(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("ACCEPT_OPTIONS") + " " +
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemFairness) || (e.getSource() == toolFairness)) {
            if (LTLField.getText().equals("")) {
              append(messageArea,
                     "Need LTL formula for fairness verification\n");
              return;
            }
            runSpin.runAndWait(trailArea, false,
                Config.getStringProperty("ERIGONE"),
                Config.getStringProperty("FAIRNESS_OPTIONS") + " " +
                editor.fileName);
            isSpinRunning();
        }
        else if ((e.getSource() == menuItemStop) || (e.getSource() == toolStop))
            runSpin.killSpin();

        // Options menu actions
        else if (e.getSource() == menuItemLimits)
            new Limits();
        else if (e.getSource() == menuItemSeed)
            changeOption("SEED", true);

        else if (e.getSource() == menuItemDefault)
            Config.setDefaultProperties();

        else if ((e.getSource() == menuItemOptionsSaveInstall) ||
                 (e.getSource() == menuItemOptionsSaveCurrent)) {
            // Save current directory and divider locations
            Config.setStringProperty("SOURCE_DIRECTORY",
                PMLfileChooser.getCurrentDirectory().getAbsolutePath());
            Config.setIntProperty("LR_DIVIDER", topSplitPane.getDividerLocation());
            Config.setIntProperty("TB_DIVIDER", mainSplitPane.getDividerLocation());
            boolean currentConfig = e.getSource() == menuItemOptionsSaveCurrent;
            Config.saveFile(currentConfig);
            append(messageArea, 
                   "Saved jSpin configuration file config.cfg in " +
                   (currentConfig ? "current" : "installation") + " directory\n");
        }

        // Output menu actions
        else if ((e.getSource() == menuItemMax) || 
        		 (e.getSource() == toolMax)) {
            if (maxedDivider)
                topSplitPane.setDividerLocation(currentDivider);
            else {
                currentDivider = topSplitPane.getDividerLocation();
                topSplitPane.setDividerLocation(Config.getIntProperty("MIN_DIVIDER"));
            }
            maxedDivider = ! maxedDivider;
        }
        else if (e.getSource() == menuItemExcludedV)
            new Excluded(editor, font, filter, true);
        else if (e.getSource() == menuItemExcludedS)
            new Excluded(editor, font, filter, false);
        else if (e.getSource() == menuItemStWidth)
            changeOption("STATEMENT_WIDTH", true);
        else if (e.getSource() == menuItemVarWidth)
            changeOption("VARIABLE_WIDTH", true);
        else if (e.getSource() == menuItemSaveSpin) {
            java.io.File outFile = new java.io.File(editor.OUTFileName);
            OUTfileChooser.setSelectedFile(outFile);
            if(OUTfileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            	editor.writeFile(OUTfileChooser.getSelectedFile(), trailArea);
        }
        // else if (e.getSource() == menuItemRaw) {
            // boolean raw = !Config.getBooleanProperty("RAW");
            // Config.setBooleanProperty("RAW", raw);
            // menuItemRaw.setSelected(raw);
        // }
        // else if (e.getSource() == menuItemDisplayRaw)
            // displayFile(editor.root + java.io.File.separator + editor.fileRoot + ".raw");

        // Spider menu actions
        else if ((e.getSource() == menuItemSpider) || (e.getSource() == toolSpider)) {
        	if (editor.file == null) {
        		jSpin.append(messageArea, Config.OPEN_FILE);
        		return;
        	}
        	// Read Spider properties from the file for this program
        	sp = new SpiderFile(spiderProperties,
        			editor.root + java.io.File.separator + editor.fileRoot);
        	sp.init();
        	if (new SpiderOptions(this, sp, font).showDialog()) {
        		sp.saveFile();
        		new Thread() {  // Run in thread to display progress
        			public void run() {
        				spinSpider.SpinSpider spd =
        				new spinSpider.SpinSpider(
        					editor.root + java.io.File.separator + editor.fileRoot,
        					editor.extension,
                    sp.getStringProperty("FORMAT"),
        						sp.getIntProperty("PROCESSES"),
        						stringToArray(sp.getStringProperty("VARIABLES")),
        						sp.getBooleanProperty("SPIDER_DEBUG"),
        						sp.getIntProperty("TRAIL_CODE"),
        						sp.getIntProperty("DOT_SIZE"),
        						sp.getIntProperty("TRAIL_STYLE"),
        						messageArea,
        						Config.getProperties());
        				if (!spd.runSpider()) return;
        				// If PNG format, display in a frame
                		if (sp.getStringProperty("FORMAT").equals(Config.PNG)) {
                			// Draw automata if requested
                			if (sp.getIntProperty("TRAIL_CODE") == 3)
               		        	new spinSpider.DrawAutomata(spd).drawAutomata();
               		        new DisplayImage(editor.root + java.io.File.separator + editor.fileRoot +
           		        			spinSpider.Config.names[sp.getIntProperty("TRAIL_CODE")] + ".png");
                		}
        			}
        		}.start();
        	}
        }
        else if (e.getSource() == menuItemSpiderDisplay)
            displayFile(editor.root + java.io.File.separator + editor.fileRoot + ".dbg");

        // Help menu actions
        else if (e.getSource() == menuItemHelp)
        	displayFile(Config.helpFileName);
        else if (e.getSource() == menuItemAbout)
        	displayFile(Config.aboutFileName);
        if (editor != null) editor.focus(false);
    }

    // Display the contents of a file in the trail area
    private void displayFile(String fn) {
    	trailArea.setText(editor.readFile(new java.io.File(fn)));
        trailArea.setCaretPosition(0);
    }
    
    // Convert String to array of Strings
    private static String[] stringToArray(String s) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(s);
        int count = st.countTokens();
        String[] sa = new String[count];
        for (int i = 0; i < count; i++) 
        	sa[i] = st.nextToken();
        return sa;
    }
    
    // Append and move caret to end
    public static void append(JTextArea area, String s) {
        if (s != null) area.append(s);
        int last = area.getText().length();
        try { 
            area.scrollRectToVisible(area.modelToView(last)); 
            area.setCaretPosition(last);
        }
        catch (javax.swing.text.BadLocationException e) {
            System.err.println("Error setting caret position when writing\n" + s + "\n");
        }
    }

    // Clear all areas before opening file or creating new file
    private void clearAreas() {
        messageArea.setText("");
        trailArea.setText("");
        LTLField.setText("");
    }

    // Display an option pane to edit an option string
    private void changeOption(String property, boolean number) {
        String s = Config.getStringProperty(property);
        String answer = JOptionPane.showInputDialog(property, s);
        if (answer == null) return;
		if (!number || (number && checkInt(answer)))
            Config.setStringProperty(property, answer);
		else
			JOptionPane.showMessageDialog(null,  
				answer + " is not an integer", "Format error",
				JOptionPane.ERROR_MESSAGE);
    }

	// Some option panes need to be positive integers
    private boolean checkInt(String property) {
        try {
            Integer.parseInt(property);
			return true;
        } 
        catch (NumberFormatException e) {
			return false;
        }
    }

	// Create a thread to check if Spin is running
	// Enables user interface to continue running and press Stop
    void isSpinRunning() {
        Thread th = new Thread () {
            public void run() {
                while (runSpin.isRunning()) {
                    try {
                        Thread.sleep(Config.getIntProperty("POLLING_DELAY"));
                    } catch (InterruptedException e) {}
                }
                append(messageArea, "done!\n");
            }
        };
        th.start();
    }

    // Initialize menus
    private void initMenuItem(
        JMenu menu, JMenuItem item, int mnemonic, String accelerator) {
        menu.add(item);
        item.addActionListener(this);
        item.setMnemonic(mnemonic);
        if (accelerator != null) 
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
    }

    private void initMenus() {
        menuBar.setBorder(new LineBorder(java.awt.Color.BLUE));

        menuBar.add(menuFile);
        menuFile.setText(Config.File);
        menuFile.setMnemonic(Config.FileMN);
        initMenuItem(menuFile, menuItemNew, Config.NewMN, Config.NewAC);
        initMenuItem(menuFile, menuItemOpen, Config.OpenMN, Config.OpenAC);
        initMenuItem(menuFile, menuItemSave, Config.SaveMN, Config.SaveAC);
        initMenuItem(menuFile, menuItemSaveAs, Config.SaveAsMN, Config.SaveAsAC);
        menuFile.addSeparator();
        initMenuItem(menuFile, menuItemSwitch, Config.SwitchMN, Config.SwitchAC);
        menuFile.addSeparator();
        initMenuItem(menuFile, menuItemExit, Config.ExitMN, Config.ExitAC);

        undoredo = new UndoRedo();
        menuBar.add(menuEditor);
        menuEditor.setText(Config.Editor);
        menuEditor.setMnemonic(Config.EditorMN);
        JMenuItem menuItemUndo = menuEditor.add(undoredo.undoAction);
        JMenuItem menuItemRedo = menuEditor.add(undoredo.redoAction);
        initMenuItem(menuEditor, menuItemUndo, Config.UndoMN, Config.UndoAC);
        initMenuItem(menuEditor, menuItemRedo, Config.RedoMN, Config.RedoAC);
        menuEditor.addSeparator();
        initMenuItem(menuEditor, menuItemCopy, Config.CopyMN, Config.CopyAC);
        initMenuItem(menuEditor, menuItemCut, Config.CutMN, Config.CutAC);
        initMenuItem(menuEditor, menuItemPaste, Config.PasteMN, Config.PasteAC);
        menuEditor.addSeparator();
        initMenuItem(menuEditor, menuItemFind, Config.FindMN, Config.FindAC);
        initMenuItem(menuEditor, menuItemFindAgain, Config.FindAgainMN, Config.FindAgainAC);

        menuBar.add(menuSpin);
        menuSpin.setText(Config.Spin);
        menuSpin.setMnemonic(Config.SpinMN);
        initMenuItem(menuSpin, menuItemCheck, Config.CheckMN, Config.CheckAC);
        menuSpin.addSeparator();
        initMenuItem(menuSpin, menuItemRandom, Config.RandomMN, Config.RandomAC);
        initMenuItem(menuSpin, menuItemInter, Config.InterMN, Config.InterAC);
        initMenuItem(menuSpin, menuItemTrail, Config.TrailMN, Config.TrailAC);
        menuSpin.addSeparator();
        initMenuItem(menuSpin, menuItemLTL2BA, Config.LTL2BAMN, Config.LTL2BAAC);
        menuSpin.addSeparator();
        initMenuItem(menuSpin, menuItemSafety, Config.SafetyMN, Config.SafetyAC);
        initMenuItem(menuSpin, menuItemAcceptance, Config.AcceptanceMN, Config.AcceptanceAC);
        initMenuItem(menuSpin, menuItemFairness, Config.FairMN, Config.FairAC);
        menuSpin.addSeparator();
        initMenuItem(menuSpin, menuItemStop, Config.StopMN, Config.StopAC);

        // menuBar.add(menuOptions);
        // menuOptions.setText(Config.Options);
        // menuOptions.setMnemonic(Config.OptionsMN);
        menuBar.add(menuOptions);
        menuOptions.setText(Config.Options);
        menuOptions.setMnemonic(Config.OptionsMN);
        initMenuItem(menuOptions, menuItemLimits, Config.LimitsMN, Config.LimitsAC);
        // initMenuItem(menuOptions, menuItemMaxDepth, Config.MaxDepthMN, Config.MaxDepthAC);
        menuOptions.addSeparator();
        initMenuItem(menuOptions, menuItemSeed, Config.SeedMN, Config.SeedAC);
        menuOptions.addSeparator();
        // initMenuItem(menuOptions, menuItemOptionsCommon, Config.CommonMN, Config.CommonAC);
        // menuOptions.addSeparator();
        // initMenuItem(menuOptions, menuItemOptionsCheck, Config.CheckMN, Config.CheckAC);
        // initMenuItem(menuOptions, menuItemOptionsRandom, Config.RandomMN, Config.OptionsRandomAC);
        // initMenuItem(menuOptions, menuItemOptionsInter, Config.InterMN, Config.OptionsInterAC);
        // initMenuItem(menuOptions, menuItemOptionsTrail, Config.TrailMN, Config.OptionsTrailAC);
        // menuOptions.addSeparator();
        initMenuItem(menuOptions, menuItemDefault, Config.DefaultMN, Config.DefaultAC);
        initMenuItem(menuOptions, menuItemOptionsSaveInstall, Config.SaveInstallMN, Config.OptionsSaveInstallAC);
        initMenuItem(menuOptions, menuItemOptionsSaveCurrent, Config.SaveCurrentMN, Config.OptionsSaveCurrentAC);


        menuBar.add(menuOutput);
        menuOutput.setText(Config.Output);
        menuOutput.setMnemonic(Config.OutputMN);
        initMenuItem(menuOutput, menuItemMax, Config.MaxMN, Config.MaxAC);
        menuOutput.addSeparator();
        initMenuItem(menuOutput, menuItemExcludedV, Config.ExcludedVMN, Config.ExcludedVAC);
        initMenuItem(menuOutput, menuItemExcludedS, Config.ExcludedSMN, Config.ExcludedSAC);
        menuOutput.addSeparator();
        initMenuItem(menuOutput, menuItemStWidth, Config.StWidthMN, Config.StWidthAC);
        initMenuItem(menuOutput, menuItemVarWidth, Config.VarWidthMN, Config.VarWidthAC);
        menuOutput.addSeparator();
        initMenuItem(menuOutput, menuItemSaveSpin, Config.SaveSpinMN, Config.SaveSpinAC);
        // menuOutput.addSeparator();
        // initMenuItem(menuOutput, menuItemRaw, Config.RawMN, Config.RawAC);
        // initMenuItem(menuOutput, menuItemDisplayRaw, Config.DisplayRawMN, Config.DisplayRawAC);
        // menuItemRaw.setSelected(Config.getBooleanProperty("RAW"));

        // menuBar.add(menuSpider);
        // menuSpider.setText(Config.Spider);
        // menuSpider.setMnemonic(Config.SpiderMN);
        // initMenuItem(menuSpider, menuItemSpider, Config.SpiderMN, Config.SpiderAC);
        // menuSpider.addSeparator();
        // initMenuItem(menuSpider, menuItemSpiderDisplay, Config.SpiderDisplayMN, Config.SpiderDisplayAC);

        menuBar.add(menuHelp);
        menuHelp.setText(Config.Help);
        menuHelp.setMnemonic(Config.HelpMN);
        initMenuItem(menuHelp, menuItemHelp, Config.HelpMN, Config.HelpAC);
        initMenuItem(menuHelp, menuItemAbout, Config.AboutMN, Config.AboutAC);

        menuBar.add(new JLabel("                    "));
        menuBar.add(new JSeparator(SwingConstants.VERTICAL));
        menuBar.add(LTLLabel);
        LTLField.setColumns(Config.LTL_COLUMNS);
        LTLField.setPreferredSize(new java.awt.Dimension(250,30));
        menuBar.add(LTLField);
    }

    private void initToolButton(JButton item, int mnemonic) {
        item.setMaximumSize(new java.awt.Dimension(75,40));
        toolBar.add(item);
        if (mnemonic != -1) item.setMnemonic(mnemonic);
        item.addActionListener(this);
    }
    
    // Initialize toolbar
    private void initToolBar() {
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setFloatable(false);
        toolBar.setBorder(new LineBorder(java.awt.Color.BLUE));

        toolBar.add(toolOpen);
        initToolButton(toolOpen, -1);
        initToolButton(toolSave, -1);
        toolBar.addSeparator();
        initToolButton(toolCheck, Config.CheckMN);
        initToolButton(toolRandom, Config.RandomMN);
        initToolButton(toolInter, Config.InterMN);
        initToolButton(toolTrail, Config.TrailMN);
        toolBar.addSeparator();
        initToolButton(toolLTL2BA, Config.LTL2BAMN);
        initToolButton(toolSafety, Config.SafetyMN);
        initToolButton(toolAcceptance, Config.AcceptanceMN);
        initToolButton(toolFairness, Config.FairMN);
        toolBar.addSeparator();
        initToolButton(toolStop, Config.StopMN);
//        initToolButton(toolSpider, -1);
        initToolButton(toolMax, Config.MaxMN);
    }

    private JFileChooser newChooser(String title, String e1, String e2, String e3) {
        JFileChooser fileChooser = new JFileChooser(Config.getStringProperty("SOURCE_DIRECTORY"));
        fileChooser.setFileFilter(new JSpinFileFilter(title, e1, e2, e3));
        return fileChooser;
    }
    
    // Initialization, optionally with initial source file
    private void init(String file) {
        Config.init();
    	spiderProperties = new java.util.Properties();
		
		// Set properties of text areas
        font = new java.awt.Font(
            Config.getStringProperty("FONT_FAMILY"), 
            Config.getIntProperty("FONT_STYLE"), 
            Config.getIntProperty("FONT_SIZE"));
        javax.swing.UIManager.put("TextField.font", font);
        editorArea.setFont(font);
        editorArea.setTabSize(Config.getIntProperty("TAB_SIZE"));
        trailArea.setFont(font);
        trailArea.setLineWrap(Config.getBooleanProperty("WRAP"));
        trailArea.setWrapStyleWord(true);
        messageArea.setFont(font);
        LTLField.setFont(font);
		
		// Create menus and toolbar
        initMenus();
        initToolBar();
        setJMenuBar(menuBar);

		// Set up frame with panes
        topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, trailScrollPane);
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, messageScrollPane);
        topSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setOneTouchExpandable(true);
        topSplitPane.setDividerLocation(Config.getIntProperty("LR_DIVIDER"));
        currentDivider = Config.getIntProperty("LR_DIVIDER");
        mainSplitPane.setDividerLocation(Config.getIntProperty("TB_DIVIDER"));
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new java.awt.BorderLayout());
        contentPane.add(toolBar, java.awt.BorderLayout.NORTH);
        contentPane.add(mainSplitPane, java.awt.BorderLayout.CENTER);
        setContentPane(contentPane);

		// Create objects
        filter = new Filter();
        editor = new Editor(editorScrollPane, editorArea, messageArea, 
            LTLField, undoredo.myundoable, filter);
        runSpin = new RunSpin(editor, messageArea, filter);
        PMLfileChooser = newChooser("Promela source files", ".PML", ".PROM", ".H");
        PRPfileChooser = newChooser("LTL property files", ".PRP", null, null);
        OUTfileChooser = newChooser("Spin display output files", ".OUT", null, null);

		// Window listener: kill Spin process if window closed
        addWindowListener(new WindowAdapter() {        
            public void windowClosing(WindowEvent e) {
                runSpin.killSpin();
                System.exit(0);
            }
        });
		
		// Configuration JFrame and make visible
        setFont(font);
        setTitle(Config.SOFTWARE_NAME);
        setSize(Config.getIntProperty("WIDTH"), Config.getIntProperty("HEIGHT"));
        setLocationRelativeTo(null); 
        setVisible(true);
		
		// Open file if given as command argument
        if (file != "")
            editor.openFile(new java.io.File(file), true);
        else
            editor.newFile();
    }

    public static void main(java.lang.String[] args) {
		// Check Java version before executing
        String version = System.getProperty("java.version");
        if (version.compareTo(Config.JAVA_VERSION) < 0) {
            JOptionPane.showMessageDialog(null, 
                "Your Java version is " + version + "\n" + 
                "jSpin needs least " + Config.JAVA_VERSION, "Version error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        final String a = (args.length>0) ? args[0] : "";
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    jSpin jspin = new jSpin();
                    jspin.init(a);
                }
            });
    }

}
