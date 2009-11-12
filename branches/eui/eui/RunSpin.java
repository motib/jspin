/* Copyright 2003-9 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
* Fork process for running Spin
* Manage dialog for interactive simulation
*/

package eui;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.util.StringTokenizer;

class RunSpin {
  private Editor editor;               // The editor object
  private Filter filter;               // The filter object
  private RunThread runThread;         // Thread object for running Spin
  private SelectDialog selectDialog;   // Thread object for select dialog

  private JTextArea messageArea;       // The message area
  private JTextArea area;              // Output area display

  private String command, parameters;  // The command being executed
  private EUI.FilterTypes filtering;   // Output filtering mode

  // For interactive mode
  // (Executable) transitions and selections in popup menu
  private static final int MAX_SELECTIONS = 50;
  private String[] selections = new String[MAX_SELECTIONS];
  private int numSelections;
  private int selectedValue;

  RunSpin(Editor e, JTextArea m, Filter f) {
    editor = e;
    messageArea = m;
    filter = f;
  }

  // Called by EUI to run Erigone
  void run(JTextArea area,
           EUI.FilterTypes filtering,
           String command,
           String parameters) {
    this.filtering = filtering;
    this.area = area;
    this.area.setText("");
    this.command = command;
    this.parameters = parameters;
    filter.init(Config.properties);

    // Make sure that a file has been opened
    if (editor.file == null) {
      EUI.append(messageArea, Config.OPEN_FILE);
      return;
    }

    // Save editor buffer and set up display areas
    editor.saveFile(null);
    if (area != messageArea)
      this.area.setText("");
    EUI.append(messageArea, command + " " + parameters + " ... ");

    // Create a thread to run Spin and start it
    runThread = new RunThread();
    runThread.start();
  }

  // Run Spin and wait for it to complete
  void runAndWait(JTextArea area,
                  EUI.FilterTypes filtering,
                  String command,
                  String parameters) {
    run(area, filtering, command, parameters);
    if (runThread == null) return; // If file not open
    try {
      runThread.join();
      EUI.append(messageArea, "done!\n");
    } catch (InterruptedException e) {}
  }

  // Check is Spin is still running
  boolean isRunning() {
    return (runThread != null) && runThread.isAlive();
  }

  // Kill the thread running Spin and the selection dialog
  void killSpin() {
    if (runThread != null) {
      runThread.killSpin();
      runThread.interrupt();
      runThread = null;
    }
    if (selectDialog != null) {
      selectDialog.disposeDialog();
      selectDialog.interrupt();
      selectDialog = null;
    }
    EUI.append(messageArea, "\nErigone process stopped\n");
  }

  // Class RunThread enables Spin to run asynchronously
  private class RunThread extends Thread {
    private Process p;

    public void run() {
      if (filtering == EUI.FilterTypes.SPACE) {
        runDot();
        return;
      }
      try {
        // Use ProcessBuilder to run Spin, redirecting ErrorStream
        String[] sa = stringToArray(command, parameters);
        ProcessBuilder pb = new ProcessBuilder(sa);
        File pf = editor.file.getParentFile();
        if (pf != null) pb.directory(pf.getCanonicalFile());
        pb.redirectErrorStream(true);
        p = pb.start();

        // Connect to I/O streams
        InputStream istream = p.getInputStream();
        BufferedReader input =
          new BufferedReader(new InputStreamReader(istream));
        OutputStream ostream = p.getOutputStream();
        OutputStreamWriter output = new OutputStreamWriter(ostream);

        // Process Spin output line by line
        String s = "";
        boolean running = true;
        // chosenFlag: 0 = in program, 1 = before choosing, 2 = after choosing
        int chosenFlag  = 0;
        // Store current state for interactive display
        String  currentState = "";
        while (running) {
          s = input.readLine();
          // System.out.println(s);  // For debugging
          if (s == null)
            running = false;
          else if (filtering == EUI.FilterTypes.SIMULATION)
            EUI.append(area, filter.filterSimulation(s));
          else if (filtering == EUI.FilterTypes.VERIFICATION)
            EUI.append(area, filter.filterVerification(s));
          else if (filtering == EUI.FilterTypes.COMPILATION)
            EUI.append(area, filter.filterCompilation(s));
          else if (filtering == EUI.FilterTypes.TRANSLATION)
            EUI.append(area, filter.filterVerification(s));
          else if (filtering == EUI.FilterTypes.SPACE) {
            System.out.println(s);
          }
          else if (filtering == EUI.FilterTypes.INTERACTIVE) {
            if (s.startsWith("initial state=") || s.startsWith("next state=")) {
              currentState = s;
              numSelections = 0;
            }
            else if (s.startsWith("executable transitions="))
              // Program has been read; look for choices
              chosenFlag = 1;
            else if (s.startsWith("chosen transition="))
              // Next transition is the one chosen
              chosenFlag = 2;
            else if ((chosenFlag == 1) && s.startsWith("process=")) {
              // Store executable transition for selection
              if (numSelections == MAX_SELECTIONS) {
                EUI.append(messageArea, "Too many selections");
                return;
              }
              selections[numSelections++] =
                Filter.extract(s,       "process=") + " " +
                Filter.extract(s,       "line=")    + " " + 
                Filter.extractBraces(s, "statement=");
            }
            else if ((chosenFlag == 2) && s.startsWith("process=")) {
              // Use current state and selected transition
              //   to update scenario display
              EUI.append(area, filter.filterSimulation(currentState));
              EUI.append(area, filter.filterSimulation(s));
              chosenFlag = 1;
            }
            else if (s.startsWith("choose from=")) {
              // Display the popup menu to choose a transition
              filter.storeVariables(currentState);
              running = select(
                filter.getTitle() + "\n" + filter.variablesToString(false),
                area, p, input, output);
            }
            else
              EUI.append(area, filter.filterSimulation(s));
          }
          else
            EUI.append(area, s + "\n");
        }
        // Wait for Spin process to end
        p.waitFor();
      } catch (InterruptedException e) {
        EUI.append(messageArea, "Interrupted exception");
      } catch (java.io.IOException e) {
        EUI.append(messageArea, "IO exception\n" + e);
      }
    }

    // Run dot and redirect output to PNG file
    public void runDot() {
      try {
        String[] sa = stringToArray(command, parameters);
        ProcessBuilder pb = new ProcessBuilder(sa);
        File pf = editor.file.getParentFile();
        if (pf != null) pb.directory(pf.getCanonicalFile());
        p = pb.start();
      }
      catch (java.io.IOException e) {}
      }

    // String to array of tokens - for ProcessBuilder
    //   Previous versions of EUI used StringTokenizer
    //     which caused problems in Linux
    String[] stringToArray(String command, String s) {
      char quote = Config.getBooleanProperty("SINGLE_QUOTE") ? '\'' : '"';
      String[] sa = new String[50];
      int count = 0, i = 0, start = 0;
      sa[count] = command;
      count++;
      while (i < s.length() && s.charAt(i) == ' ') i++;
      start = i;
      boolean isQuote;
      do {
        if (i == s.length()) break;
        isQuote = s.charAt(i) == quote;
        if (isQuote) i++;
        if (isQuote) {
          while (s.charAt(i) != quote) i++;
          i++;
        }
        else
          while (i < s.length() && s.charAt(i) != ' ') i++;
        sa[count] = s.substring(start, i);
        while (i < s.length() && s.charAt(i) == ' ') i++;
        start = i;
        count++;
      } while (true);
      String[] sb = new String[count];
      System.arraycopy(sa, 0, sb, 0, count);
      return sb;
    }

    // Kill spin process
    private void killSpin() {
      if (p != null) p.destroy();
    }

    // Select the next statement to run in interactive simulation
    private boolean select(
      String state, JTextArea area, Process p, 
      BufferedReader input, OutputStreamWriter output) {
      try {
        // Get selection from dialog
        selectedValue = -1;
        // Display select dialog as buttons or combobox
        selectDialog = 
          new SelectDialog(
            numSelections <= Config.getIntProperty("SELECT_MENU"),
            state);
        selectDialog.start();
        while (selectedValue == -1) {
          try {
            Thread.sleep(Config.getIntProperty("POLLING_DELAY"));
          } catch (InterruptedException e) {}
        }
        // For Macintosh (?) - Angelika's problem (?)
        if (selectDialog != null) { 
          selectDialog.interrupt();
          selectDialog = null;
        }
        // If 0 (escape or close) selected, quit Spin interaction
        if (selectedValue == 0) {
          output.write("q\n");
          output.flush();
          return false;
        }

        // Send selection to Spin
        selectedValue--;
        output.write(selectedValue + "\n");
        output.flush();
        return true;
      } catch (Exception e) {
        System.err.println(e);
        killSpin();
        return false;
      }
    }
  }

  // Class SelectDialog displays the statement select dialog in a thread
  private class SelectDialog extends Thread implements ActionListener {
    // Positive when a button is selected, zero upon escape or close
    private JFrame    dialog;
    private JPanel    panel1, panel2;
    private JTextArea stateField;
    private JButton[] options;
    private JComboBox pulldown;
    private int       width;    // Width of button

    // Constructor - set up frame with number of buttons required
    SelectDialog (boolean buttons, String state) {
      dialog = new JFrame();
      dialog.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            selectedValue = 0;
            dialog.dispose();
          }
        });
      dialog.getRootPane().registerKeyboardAction(
        this,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
      dialog.setTitle(Config.SELECT);

      // Panel for buttons or combobox
      panel1 = new JPanel();
      panel1.setLayout(new java.awt.GridLayout(1,1));
      if (buttons)
        constructButtonsDialog();
      else
        constructMenuDialog();

      // Display current state in a text area
      stateField = new JTextArea(state, 2, 50);
      stateField.setFont(messageArea.getFont());
      stateField.setEditable(false);
      stateField.setFocusable(false);

      // Panel for state area
      panel2 = new JPanel();
      panel2.setLayout(new java.awt.GridLayout(1,1));
      panel2.add(stateField);

      // Setup dialog frame
      dialog.getContentPane().setLayout(new java.awt.GridLayout(2,1));
      dialog.getContentPane().add(panel2);
      dialog.getContentPane().add(panel1);
      dialog.setSize(width, Config.getIntProperty("SELECT_HEIGHT") * 2);
      dialog.setLocationRelativeTo(messageArea);
      dialog.validate();
      dialog.setVisible(true);
      options[0].requestFocusInWindow();
    }

    void constructButtonsDialog() {
      panel1.setLayout(new java.awt.GridLayout(1, numSelections));
      options = new JButton[numSelections];
      JButton button = null;
      for (int i = 1; i <= numSelections; i++) {
        button = new JButton(selections[i-1]);
        button.setFont(messageArea.getFont());
        button.addActionListener(this);
        options[i-1] = button;
        panel1.add(button);
      }
      width = Config.getIntProperty("SELECT_BUTTON") * numSelections;
    }

    void constructMenuDialog() {
      panel1.setLayout(new java.awt.BorderLayout());
      pulldown = new JComboBox();
      pulldown.setFont(messageArea.getFont());
      pulldown.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
      pulldown.setEditable(false);
      for (int i = 0; i < numSelections; i++)
        pulldown.addItem(selections[i]);
      pulldown.setSelectedIndex(-1);
      pulldown.addActionListener(this);
      panel1.add(pulldown, java.awt.BorderLayout.CENTER);
      width = Config.getIntProperty("SELECT_BUTTON"); 
    }

    // Display dialog
    public void run() {
      dialog.setVisible(true);
    }

    // ActionListener
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JButton) {
        JButton selected = (JButton) e.getSource();
        for (int i = 0; i < numSelections; i++)
          if (options[i].equals(selected))
            selectedValue = i+1;
      } 
      else if (e.getSource() instanceof JComboBox) {
        selectedValue = ((JComboBox)e.getSource()).getSelectedIndex()+1;
      } 
      else {
        selectedValue = 0;
        dialog.dispose();
        return;
      }
      dialog.dispose();
      java.awt.Dimension rv = ((JComponent)e.getSource()).getSize(null);
      Config.setIntProperty("SELECT_BUTTON", rv.width);
    }
    
    // Dispose of dialog frme
    private void disposeDialog() {
      if (dialog != null) dialog.dispose();
    }
  }
}
