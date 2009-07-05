/*
 EUI - Development environment for Erigone
 Copyright 2003-9 by Mordechai (Moti) Ben-Ari.
 
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

package eui;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class EUI extends JFrame implements ActionListener {
  public enum FilterTypes {
    COMPILATION, TRANSLATION, SIMULATION, INTERACTIVE, VERIFICATION};

	// Contained objects. Create only after initializing configuration.
  private Editor     editor;
  private RunSpin    runSpin;
  private Filter     filter;
  private UndoRedo   undoredo;
  private boolean    maxedDivider;
  private int        currentDivider;

	// User interface components
  private JTextArea  editorArea = new JTextArea();
  private JTextArea  trailArea = new JTextArea();
  private JTextArea  messageArea = new JTextArea();
  private JTextField LTLField = new JTextField();

  private JFileChooser  PMLfileChooser;
  private JFileChooser  PRPfileChooser;
  private JFileChooser  OUTfileChooser;
  private java.awt.Font font;

  private JScrollPane editorScrollPane = new JScrollPane(editorArea);
  private JScrollPane trailScrollPane = new JScrollPane(trailArea);
  private JScrollPane messageScrollPane = new JScrollPane(messageArea);
  private JSplitPane  topSplitPane;
  private JSplitPane  mainSplitPane;
  private JLabel LTLLabel = new JLabel(Config.LTL_FORMULA);

  private JMenuBar  menuBar = new JMenuBar();

  private JMenu     menuFile = new JMenu();
  private JMenuItem menuItemNew = new JMenuItem(Config.New);
  private JMenuItem menuItemOpen = new JMenuItem(Config.Open);
  private JMenuItem menuItemSave = new JMenuItem(Config.Save);
  private JMenuItem menuItemSaveAs = new JMenuItem(Config.SaveAs);
  private JMenuItem menuItemSwitch = new JMenuItem(Config.Switch);
  private JMenuItem menuItemExit = new JMenuItem(Config.Exit);

  private JMenu     menuEditor = new JMenu();
  private JMenuItem menuItemCopy = new JMenuItem(Config.Copy);
  private JMenuItem menuItemCut = new JMenuItem(Config.Cut);
  private JMenuItem menuItemPaste = new JMenuItem(Config.Paste);
  private JMenuItem menuItemFind = new JMenuItem(Config.Find);
  private JMenuItem menuItemFindAgain = new JMenuItem(Config.FindAgain);

  private JMenu     menuSpin = new JMenu();
  private JMenuItem menuItemCheck = new JMenuItem(Config.Check);
  private JMenuItem menuItemRandom = new JMenuItem(Config.Random);
  private JMenuItem menuItemInter = new JMenuItem(Config.Inter);
  private JMenuItem menuItemTrail = new JMenuItem(Config.Trail);
  private JMenuItem menuItemLTL2BA = new JMenuItem(Config.LTL2BA);
  private JMenuItem menuItemSafety = new JMenuItem(Config.Safety);
  private JMenuItem menuItemAcceptance = new JMenuItem(Config.Acceptance);
  private JMenuItem menuItemFairness = new JMenuItem(Config.Fair);
  private JMenuItem menuItemStop = new JMenuItem(Config.Stop);

  private JMenu     menuOptions = new JMenu();
  private JMenuItem menuItemLimits = new JMenuItem(Config.Limits);
  private JMenuItem menuItemSeed = new JMenuItem(Config.Seed);
  private JMenuItem menuItemDefault = new JMenuItem(Config.Default);
  private JMenuItem menuItemOptionsSaveInstall = new JMenuItem(Config.SaveInstall);
  private JMenuItem menuItemOptionsSaveCurrent = new JMenuItem(Config.SaveCurrent);

  private JMenu     menuDisplay = new JMenu();
  private JMenuItem menuItemMax = new JMenuItem(Config.Max);
  private JMenuItem menuItemTraceOptions = new JMenuItem(Config.TraceOptions);
  private JMenuItem menuItemSaveSpin = new JMenuItem(Config.SaveSpin);

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
  private JButton   toolMax = new JButton(Config.Max);

  public void actionPerformed(ActionEvent e) {
    // File menu actions
    if (e.getSource() == menuItemNew) {
      editor.lastFile = editor.file;
      clearAreas();
      editor.newFile();
    }
    else if ((e.getSource() == menuItemOpen) ||
             (e.getSource() == toolOpen)) {
      if(PMLfileChooser.showOpenDialog(this) ==
         JFileChooser.APPROVE_OPTION) {
        editor.lastFile = editor.file;
        clearAreas();
        editor.openFile(PMLfileChooser.getSelectedFile(), true);
      }
    }
    else if ((e.getSource() == menuItemSave) ||
             (e.getSource() == toolSave)) {
      if (editor.file != null)
        editor.saveFile(null);
      else {
        if(PMLfileChooser.showSaveDialog(this) ==
           JFileChooser.APPROVE_OPTION) {
          editor.saveFile(PMLfileChooser.getSelectedFile());
        }
      }
    } 
    else if (e.getSource() == menuItemSaveAs) {
      if(PMLfileChooser.showSaveDialog(this) ==
         JFileChooser.APPROVE_OPTION)
        editor.lastFile = editor.file;
        editor.saveFile(PMLfileChooser.getSelectedFile());
    }
    else if (e.getSource() == menuItemSwitch) {
      if (editor.lastFile != null) {
        java.io.File temp = editor.lastFile;
        // Save current file; if textarea not saved, as for a file name
        if (editor.file != null)
          editor.saveFile(null);
        else {
          if(PMLfileChooser.showSaveDialog(this) ==
             JFileChooser.APPROVE_OPTION) {
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
    // For quick actions like compilation, call runAndWait
    // For others call run and create thread to check isSpinRunning
    else if ((e.getSource() == menuItemCheck) ||
             (e.getSource() == toolCheck)) {
      runSpin.runAndWait(trailArea, FilterTypes.COMPILATION,
        Config.getStringProperty("ERIGONE"),
        Config.getStringProperty("COMPILE_OPTIONS") + " " +
        editor.fileName);
    }
    else if ((e.getSource() == menuItemRandom) ||
             (e.getSource() == toolRandom)) {
      runSpin.run(trailArea, FilterTypes.SIMULATION,
        Config.getStringProperty("ERIGONE"),
        Config.getStringProperty("RANDOM_OPTIONS") + " " +
        (Config.getIntProperty("SEED") != 0 ? 
          ("-n" + Config.getIntProperty("SEED") + " ") : "") +
          " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
          editor.fileName);
      isSpinRunning();
    }
    else if ((e.getSource() == menuItemInter) ||
             (e.getSource() == toolInter)) {
      runSpin.run(trailArea, FilterTypes.INTERACTIVE,
        Config.getStringProperty("ERIGONE"),
        Config.getStringProperty("INTERACTIVE_OPTIONS") + " " +
        editor.fileName);
      isSpinRunning();
    }
    else if ((e.getSource() == menuItemTrail) ||
             (e.getSource() == toolTrail)) {
      runSpin.run(trailArea, FilterTypes.SIMULATION,
        Config.getStringProperty("ERIGONE"),  
        Config.getStringProperty("TRAIL_OPTIONS") + " " +
        " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
        editor.fileName);
      isSpinRunning();
    }
    else if ((e.getSource() == menuItemLTL2BA) ||
             (e.getSource() == toolLTL2BA)) {
      runSpin.runAndWait(trailArea, FilterTypes.TRANSLATION,
        Config.getStringProperty("ERIGONE"),  
        Config.getStringProperty("LTL2BA_OPTIONS") + " " +
        editor.fileName);
    }
    else if ((e.getSource() == menuItemSafety) ||
             (e.getSource() == toolSafety)) {
      runSpin.run(trailArea, FilterTypes.VERIFICATION,
        Config.getStringProperty("ERIGONE"),
        Config.getStringProperty("SAFETY_OPTIONS") + " " +
        " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
        (!LTLField.getText().equals("") ? ("-t ") : "") +
        editor.fileName);
      isSpinRunning();
    }
    else if ((e.getSource() == menuItemAcceptance) ||
             (e.getSource() == toolAcceptance)     ||
             (e.getSource() == menuItemFairness)   ||
             (e.getSource() == toolFairness)) {
      if (LTLField.getText().equals("")) {
        append(messageArea,
               "Need LTL formula for acceptance verification\n");
        return;
      }
      runSpin.run(trailArea, FilterTypes.VERIFICATION,
        Config.getStringProperty("ERIGONE"),
        Config.getStringProperty(
          ((e.getSource() == menuItemFairness) ||
           (e.getSource() == toolFairness) ?  
           "FAIRNESS_OPTIONS" : "ACCEPT_OPTIONS")) + " " +
        " -e" + Config.getStringProperty("TOTAL_STEPS") + " " + 
        editor.fileName);
      isSpinRunning();
    }
    else if ((e.getSource() == menuItemStop) || (e.getSource() == toolStop))
      runSpin.killSpin();

    // Options menu actions
    else if (e.getSource() == menuItemLimits)
        new Limits();
    else if (e.getSource() == menuItemSeed)
        changeOption("SEED");
    else if (e.getSource() == menuItemDefault)
        Config.setDefaultProperties();
    else if ((e.getSource() == menuItemOptionsSaveInstall) ||
             (e.getSource() == menuItemOptionsSaveCurrent)) {
      // Save current directory and divider locations
      Config.setStringProperty("SOURCE_DIRECTORY",
        PMLfileChooser.getCurrentDirectory().getAbsolutePath());
      Config.setIntProperty(
        "LR_DIVIDER", topSplitPane.getDividerLocation());
      Config.setIntProperty(
        "TB_DIVIDER", mainSplitPane.getDividerLocation());
      // Save in current or installation directory
      boolean currentConfig = e.getSource() == menuItemOptionsSaveCurrent;
      Config.saveFile(currentConfig);
      append(
        messageArea, 
        "Saved configuration file config.cfg in " +
        (currentConfig ? "current" : "installation") + " directory\n");
    }

    // Display menu actions
    else if ((e.getSource() == menuItemMax) || 
             (e.getSource() == toolMax)) {
      if (maxedDivider)
        topSplitPane.setDividerLocation(currentDivider);
      else {
        currentDivider = topSplitPane.getDividerLocation();
        topSplitPane.setDividerLocation(
          Config.getIntProperty("MIN_DIVIDER"));
      }
      maxedDivider = ! maxedDivider;
    }
    else if (e.getSource() == menuItemTraceOptions)
      new Trace(editor, filter, font);
    else if (e.getSource() == menuItemSaveSpin) {
      java.io.File outFile = new java.io.File(editor.OUTFileName);
      OUTfileChooser.setSelectedFile(outFile);
      if(OUTfileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        editor.writeFile(OUTfileChooser.getSelectedFile(), trailArea);
    }

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
    
  // Append string to text area and move the caret to the end
  public static void append(JTextArea area, String s) {
    if (s != null) area.append(s);
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

  // Clear all areas before opening file or creating new file
  private void clearAreas() {
    messageArea.setText("");
    trailArea.setText("");
    LTLField.setText("");
  }

  // Display an option pane to edit a numerical option
  private void changeOption(String property) {
    String s = Config.getStringProperty(property);
    while (true) {
      String answer = JOptionPane.showInputDialog(property, s);
      if (answer == null) return;
      try {
        Integer.parseInt(answer);
        Config.setStringProperty(property, answer);
        return;
      } 
      catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null,  
          answer + " is not an integer", "Format error",
          JOptionPane.ERROR_MESSAGE);
      }
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

  // Initialize one menu item; add mnemonic and accelerator (if any)
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
    initMenuItem(
      menuFile, menuItemNew, Config.NewMN, Config.NewAC);
    initMenuItem(
      menuFile, menuItemOpen, Config.OpenMN, Config.OpenAC);
    initMenuItem(
      menuFile, menuItemSave, Config.SaveMN, Config.SaveAC);
    initMenuItem(
      menuFile, menuItemSaveAs, Config.SaveAsMN, Config.SaveAsAC);
    menuFile.addSeparator();
    initMenuItem(
      menuFile, menuItemSwitch, Config.SwitchMN, Config.SwitchAC);
    menuFile.addSeparator();
    initMenuItem(
      menuFile, menuItemExit, Config.ExitMN, Config.ExitAC);

    undoredo = new UndoRedo();
    menuBar.add(menuEditor);
    menuEditor.setText(Config.Editor);
    menuEditor.setMnemonic(Config.EditorMN);
    JMenuItem menuItemUndo = menuEditor.add(undoredo.undoAction);
    JMenuItem menuItemRedo = menuEditor.add(undoredo.redoAction);
    initMenuItem(
      menuEditor, menuItemUndo, Config.UndoMN, Config.UndoAC);
    initMenuItem(
      menuEditor, menuItemRedo, Config.RedoMN, Config.RedoAC);
    menuEditor.addSeparator();
    initMenuItem(
      menuEditor, menuItemCopy, Config.CopyMN, Config.CopyAC);
    initMenuItem(
      menuEditor, menuItemCut, Config.CutMN, Config.CutAC);
    initMenuItem(
      menuEditor, menuItemPaste, Config.PasteMN, Config.PasteAC);
    menuEditor.addSeparator();
    initMenuItem(
      menuEditor, menuItemFind, Config.FindMN, Config.FindAC);
    initMenuItem(
      menuEditor, menuItemFindAgain,
      Config.FindAgainMN, Config.FindAgainAC);

    menuBar.add(menuSpin);
    menuSpin.setText(Config.Spin);
    menuSpin.setMnemonic(Config.SpinMN);
    initMenuItem(
      menuSpin, menuItemCheck, Config.CheckMN, Config.CheckAC);
    menuSpin.addSeparator();
    initMenuItem(
      menuSpin, menuItemRandom, Config.RandomMN, Config.RandomAC);
    initMenuItem(
      menuSpin, menuItemInter, Config.InterMN, Config.InterAC);
    initMenuItem(
      menuSpin, menuItemTrail, Config.TrailMN, Config.TrailAC);
    menuSpin.addSeparator();
    initMenuItem(
      menuSpin, menuItemLTL2BA, Config.LTL2BAMN, Config.LTL2BAAC);
    menuSpin.addSeparator();
    initMenuItem(
      menuSpin, menuItemSafety, Config.SafetyMN, Config.SafetyAC);
    initMenuItem(
      menuSpin, menuItemAcceptance,
      Config.AcceptanceMN, Config.AcceptanceAC);
    initMenuItem(
      menuSpin, menuItemFairness, Config.FairMN, Config.FairAC);
    menuSpin.addSeparator();
    initMenuItem(
      menuSpin, menuItemStop, Config.StopMN, Config.StopAC);

    menuBar.add(menuOptions);
    menuOptions.setText(Config.Options);
    menuOptions.setMnemonic(Config.OptionsMN);
    initMenuItem(menuOptions, menuItemLimits,
      Config.LimitsMN, Config.LimitsAC);
    menuOptions.addSeparator();
    initMenuItem(
      menuOptions, menuItemSeed, Config.SeedMN, Config.SeedAC);
    menuOptions.addSeparator();
    initMenuItem(
      menuOptions, menuItemDefault, Config.DefaultMN, Config.DefaultAC);
    initMenuItem(
      menuOptions, menuItemOptionsSaveInstall,
      Config.SaveInstallMN, Config.OptionsSaveInstallAC);
    initMenuItem(
      menuOptions, menuItemOptionsSaveCurrent,
      Config.SaveCurrentMN, Config.OptionsSaveCurrentAC);

    menuBar.add(menuDisplay);
    menuDisplay.setText(Config.Display);
    menuDisplay.setMnemonic(Config.DisplayMN);
    initMenuItem(
      menuDisplay, menuItemTraceOptions,
      Config.TraceOptionsMN, Config.TraceOptionsAC);
    menuDisplay.addSeparator();
    initMenuItem(
      menuDisplay, menuItemMax, Config.MaxMN, Config.MaxAC);
    menuDisplay.addSeparator();
    initMenuItem(
      menuDisplay, menuItemSaveSpin,
      Config.SaveSpinMN, Config.SaveSpinAC);

    menuBar.add(menuHelp);
    menuHelp.setText(Config.Help);
    menuHelp.setMnemonic(Config.HelpMN);
    initMenuItem(
      menuHelp, menuItemHelp, Config.HelpMN, Config.HelpAC);
    initMenuItem(
      menuHelp, menuItemAbout, Config.AboutMN, Config.AboutAC);

    menuBar.add(new JLabel("                    "));
    menuBar.add(new JSeparator(SwingConstants.VERTICAL));
    menuBar.add(LTLLabel);
    LTLField.setColumns(Config.LTL_COLUMNS);
    LTLField.setPreferredSize(new java.awt.Dimension(250,30));
    menuBar.add(LTLField);
  }

  // Initialize one tool button
  // Set mnemonic (-1 if none)
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
    initToolButton(toolMax, Config.MaxMN);
  }

  private JFileChooser newChooser(
      String title, String e1, String e2, String e3) {
    JFileChooser fileChooser =
      new JFileChooser(Config.getStringProperty("SOURCE_DIRECTORY"));
    fileChooser.setFileFilter(new EUIFileFilter(title, e1, e2, e3));
    return fileChooser;
  }
    
  // Initialization, optionally with initial source file
  private void init(String file) {
    Config.init();

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
    topSplitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, trailScrollPane);
    mainSplitPane = new JSplitPane(
      JSplitPane.VERTICAL_SPLIT, topSplitPane, messageScrollPane);
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
    editor = new Editor(
      editorScrollPane, editorArea, messageArea, 
      LTLField, undoredo.myundoable, filter);
    runSpin = new RunSpin(editor, messageArea, filter);
    PMLfileChooser = newChooser(Config.PML_FILES, ".PML", ".PROM", ".H");
    PRPfileChooser = newChooser(Config.PRP_FILES, ".PRP", null, null);
    OUTfileChooser = newChooser(Config.OUT_FILES, ".OUT", null, null);

    // Window listener: kill Spin process if window closed
    addWindowListener(new WindowAdapter() {        
      public void windowClosing(WindowEvent e) {
        runSpin.killSpin();
        System.exit(0);
      }
    });

    // Configurate JFrame and make visible
    setFont(font);
    setTitle(Config.SOFTWARE_NAME);
    setSize(Config.getIntProperty("WIDTH"), 
            Config.getIntProperty("HEIGHT"));
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
        "Use at least version " + Config.JAVA_VERSION, "Version error", 
        JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    final String a = (args.length>0) ? args[0] : "";
      javax.swing.SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            (new EUI()).init(a);
          }
        });
  }
}
