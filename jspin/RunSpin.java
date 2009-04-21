/* Copyright 2003-4 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
* Fork process for running Spin
* Manage dialog for interactive simulation
*/

package jspin;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.util.StringTokenizer;

class RunSpin {
  private Editor editor;              // The editor object
  private Filter filter;              // The filter object
  private RunThread runThread;        // Thread object for running Spin
  private SelectDialog selectDialog;  // Thread object for select dialog

  private JTextArea messageArea;      // The message area
  private JTextArea area;             // Output area display
  // private PrintWriter rawWriter;      // To write raw spin output

  private String command, parameters; // The command being executed
  private jSpin.FilterTypes filtering;      // Output filtering mode
  private static final int MAX_SELECTIONS = 50;
  private String[] selections = new String[MAX_SELECTIONS];
                                      // Statements selected from

  RunSpin(Editor e, JTextArea m, Filter f) {
    editor = e;
    messageArea = m;
    filter = f;
  }

  // Called by jSpin to run Spin
  void run(JTextArea area, jSpin.FilterTypes filtering, String command, String parameters) {
    this.filtering = filtering;
    this.area = area;
    this.area.setText("");
    this.command = command;
    this.parameters = parameters;
    filter.init(Config.properties);
    // Make sure that a file has been opened
    if (editor.file == null) {
        jSpin.append(messageArea, Config.OPEN_FILE);
        return;
    }
    // Save editor buffer and set up display areas
    editor.saveFile(null);
    if (area != messageArea)
        this.area.setText("");
    jSpin.append(messageArea, command + " " + parameters + " ... ");
    // Create a thread to run Spin and start it
    runThread = new RunThread();
    runThread.start();
  }

  // Run Spin and wait for it to complete
  void runAndWait(JTextArea area, jSpin.FilterTypes filtering, String command, String parameters) {
    run(area, filtering, command, parameters);
    if (runThread == null) return; // If file not open
    try {
        runThread.join();
        jSpin.append(messageArea, "done!\n");
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
    jSpin.append(messageArea, "\nSpin process stopped\n");
  }

  // Class RunThread enables Spin to run asynchronously
  private class RunThread extends Thread {
    private Process p;

    public void run() {
      try {
        // if (Config.getBooleanProperty("RAW"))
          // rawFile();
        // Use ProcessBuilder to run Spin, redirecting ErrorStream
        String[] sa = stringToArray(command, parameters);
// for (int i = 0; i < sa.length; i++) System.out.println(sa[i]);
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
        while (running) {
          s = input.readLine();
          // if (Config.getBooleanProperty("RAW"))
            // rawWriter.println(s);
          if (s == null)
            running = false;
          else if (s.startsWith("choose from="))
            running = select(s, area, p, input, output);
          // else if (s.startsWith("spin: type return")) {
            // output.write("\n");
            // output.flush();
          // } 
          else if (filtering == jSpin.FilterTypes.SIMULATION)
            jSpin.append(area, filter.filterSimulation(s));
          else if (filtering == jSpin.FilterTypes.VERIFICATION)
            jSpin.append(area, filter.filterVerification(s));
          else
            jSpin.append(area, s + "\n");
        }
        // Wait for Spin process to end
        p.waitFor();
        // if (Config.getBooleanProperty("RAW"))
          // rawWriter.close();
        // If syntax error, set cursor to line in editor
        if (area.getText().indexOf("Error: syntax error") != -1) {
          s = area.getText();
          try {
            int line = Integer.parseInt(
              s.substring(
                s.indexOf("line")+4, s.indexOf('"')).trim());
              editor.caretToLine(line);
          } catch (NumberFormatException e1) {
            editor.caretToLine(0);
          }
        }
      } catch (InterruptedException e) {
        jSpin.append(messageArea, "Interrupted exception");
      }
      catch (java.io.IOException e) {
        jSpin.append(messageArea, "IO exception\n" + e);
      }
    }

    // String to array of tokens - for ProcessBuilder
    //   Previous versions of jSpin used StringTokenizer
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

    // Open file for raw Spin output
    // private void rawFile() {
      // String s = editor.root + File.separator + editor.fileRoot + ".raw";
      // try {
        // rawWriter = new PrintWriter(new FileWriter(s));
        // jSpin.append(messageArea, "\nOpened " + s + "\n");
      // } catch (IOException e) {
        // jSpin.append(messageArea, "\nCannot open " + s + "\n");
      // }
    // }

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

    // Select the next statement to run in interactive simulation
    private boolean select(
        String s,
        JTextArea area, Process p, 
        BufferedReader input, OutputStreamWriter output) {
      int numOptions = extractNum(s, "to=") + 1;
      if (numOptions >= MAX_SELECTIONS) return false;
      try {
        for (int i=0; i < numOptions; i++)
          selections[i] = i + "";
        // Get selection from dialog
        int selectedValue = -1;
        selectDialog = 
          new SelectDialog(
            numOptions,
            numOptions <= Config.getIntProperty("SELECT_MENU"),
            selections);
        selectDialog.start();
        while (selectedValue == -1) {
          try {
            Thread.sleep(Config.getIntProperty("POLLING_DELAY"));
          } 
          catch (InterruptedException e) {}
          if (selectDialog == null) break;
          else selectedValue = selectDialog.getValue();
        }
System.out.println("selected1=" + selectedValue);
        // For Macintosh (?) - Angelika's problem (?)
        if (selectDialog != null) { 
          selectDialog.interrupt();
          selectDialog = null;
        }
        // if (selectDialog != null) selectDialog.interrupt();
        // If 0 (escape or close) selected, quit Spin interaction
        if (selectedValue == 0) {
            output.write("q\n");
            output.flush();
            return false;
        }
        // selectedValue = Integer.valueOf(
          // selections[selectedValue-1].substring(
            // 0, selections[selectedValue-1].indexOf(':')));
// System.out.println("selected2=" + selectedValue);
        // Send selection to Spin
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
    private int selectedValue = -1;
      // Positive when a button is selected, zero upon escape or close
    private int numOptions;     // Number of process buttons
    private String[] selections;// The selections

    private JFrame    dialog;      // The frame
    private JPanel    panel;
    private JButton[] options;  // Array of process buttons
    private JComboBox pulldown = new JComboBox();

    // Constructor - set up frame with number of buttons required
    SelectDialog (int numOptions, boolean buttons, String[] selections) {
      this.numOptions = numOptions;
      this.selections = selections;
      dialog = new JFrame();
      dialog.addWindowListener(
        new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          selectedValue = 0;
          dialog.dispose();
        }
      });
      dialog.getRootPane().registerKeyboardAction(this,
              KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
              JComponent.WHEN_IN_FOCUSED_WINDOW);
      dialog.setTitle(Config.SELECT);
      panel = new JPanel();
      if (buttons) constructButtonsDialog(); else constructMenuDialog();
      dialog.getContentPane().setLayout(new java.awt.BorderLayout());
      dialog.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
      dialog.setLocationRelativeTo(messageArea);
      dialog.validate();
      dialog.setVisible(true);
    }
    
    void constructButtonsDialog() {
      panel.setLayout(new java.awt.GridLayout(1, numOptions));
      options = new JButton[numOptions];
      JButton button = null;
      for (int i = 1; i <= numOptions; i++) {
        button = new JButton(selections[i-1]);
        button.setFont(messageArea.getFont());
        button.addActionListener(this);
        options[i-1] = button;
        panel.add(button);
      }
      dialog.setSize(
        Config.getIntProperty("SELECT_BUTTON") * numOptions,
        Config.getIntProperty("SELECT_HEIGHT"));
    }

    void constructMenuDialog() {
      panel.setLayout(new java.awt.BorderLayout());
      pulldown = new JComboBox();
      pulldown.setFont(messageArea.getFont());
      pulldown.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
      pulldown.setEditable(false);
      for (int i = 0; i < numOptions; i++)
        pulldown.addItem(selections[i]);
      pulldown.setSelectedIndex(-1);
      pulldown.addActionListener(this);
      panel.add(pulldown, java.awt.BorderLayout.CENTER);
      dialog.setSize(
        Config.getIntProperty("SELECT_BUTTON"), 
        Config.getIntProperty("SELECT_HEIGHT"));
    }

    // Display dialog
    public void run() {
      dialog.setVisible(true);
    }

    // ActionListener
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JButton) {
        JButton selected = (JButton) e.getSource();
        for (int i = 0; i < numOptions; i++)
          if (options[i].equals(selected))
            selectedValue = i+1;
      } 
      else if (e.getSource() instanceof JComboBox) {
        selectedValue = ((JComboBox)e.getSource()).getSelectedIndex()+1;
      } 
      else selectedValue = 0;
      dialog.dispose();
      java.awt.Dimension rv = ((JComponent)e.getSource()).getSize(null);
      Config.setIntProperty("SELECT_BUTTON", rv.width);
    }
    
    // Dispose of dialog frme
    private void disposeDialog() {
      if (dialog != null) dialog.dispose();
    }
    
    // Get value
    private int getValue() {
      return selectedValue;
    }
  }
}
