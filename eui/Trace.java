/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Dialog for setting Trace Options 
 */

package eui;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

class Trace extends JFrame implements ActionListener {
  private JTextField stateWidthField, varWidthField;
  private JLabel     stateWidthLabel, varWidthLabel;
  private JLabel     stateTitleLabel, varTitleLabel;
  private JLabel     stateExcLabel,   varExcLabel;
  private JLabel     errorLabel;
  private JButton    okButton, cancelButton;
  private JTextArea  stateTextArea, varTextArea;

  private LineBorder border = new LineBorder(java.awt.Color.BLUE);

  private JPanel topPanel, OKPanel;
  private JPanel buttonPanel, buttonPanel1, buttonPanel2; 
  private JPanel excPanel, excPanel1, excPanel2;
  private JPanel titlePanel, titlePanel1, titlePanel2;
  private JPanel textPanel, textPanel1, textPanel2;

  // Object references
  private Editor editor;
  private Filter filter;
  // Excluded file names
  private String fileNameState, fileNameVar;
  // String read from excluded file
  private String read;

	Trace(Editor e, Filter f, java.awt.Font font) {
    editor = e;
    filter = f;

		okButton = new JButton(Config.OK);
    okButton.setMnemonic(Config.OKMN);
		okButton.addActionListener(this);
		cancelButton = new JButton(Config.Cancel);
		cancelButton.addActionListener(this);
		OKPanel = new JPanel();
    OKPanel.setLayout(new java.awt.GridLayout(1,2));
    OKPanel.add(okButton);
    OKPanel.add(cancelButton);
    OKPanel.setBorder(border);

    errorLabel = new JLabel(" ", JLabel.CENTER);

    stateTitleLabel = new JLabel(Config.Statements, JLabel.CENTER);
    varTitleLabel   = new JLabel(Config.Variables, JLabel.CENTER);
		titlePanel1 = new JPanel();
    titlePanel1.add(stateTitleLabel);
    titlePanel1.setBorder(border);
		titlePanel2 = new JPanel();
    titlePanel2.add(varTitleLabel);
    titlePanel2.setBorder(border);
		titlePanel = new JPanel();
    titlePanel.setLayout(new java.awt.GridLayout(1,2));
    titlePanel.add(titlePanel1);
    titlePanel.add(titlePanel2);

    stateExcLabel = new JLabel(Config.Excluded, JLabel.CENTER);
    varExcLabel   = new JLabel(Config.Excluded, JLabel.CENTER);
		excPanel1 = new JPanel();
    excPanel1.add(stateExcLabel);
    excPanel1.setBorder(border);
		excPanel2 = new JPanel();
    excPanel2.add(varExcLabel);
    excPanel2.setBorder(border);
		excPanel = new JPanel();
    excPanel.setLayout(new java.awt.GridLayout(1,2));
    excPanel.add(excPanel1);
    excPanel.add(excPanel2);

		buttonPanel1 = new JPanel();
    buttonPanel1.setLayout(new java.awt.GridLayout(1,2));
    stateWidthLabel = new JLabel(Config.Width);
    stateWidthField =
      new JTextField(Config.getStringProperty("STATEMENT_WIDTH"), 10);
    buttonPanel1.add(stateWidthLabel);
    buttonPanel1.add(stateWidthField);
    buttonPanel1.setBorder(border);

		buttonPanel2 = new JPanel();
    buttonPanel2.setLayout(new java.awt.GridLayout(1,2));
    varWidthLabel = new JLabel(" Width");
    varWidthField =
      new JTextField(Config.getStringProperty("VARIABLE_WIDTH"), 10);
    buttonPanel2.add(varWidthLabel);
    buttonPanel2.add(varWidthField);
    buttonPanel2.setBorder(border);

		buttonPanel = new JPanel();
    buttonPanel.setLayout(new java.awt.GridLayout(1,2));
    buttonPanel.add(buttonPanel1);
    buttonPanel.add(buttonPanel2);

		topPanel = new JPanel();
    topPanel.setLayout(new java.awt.GridLayout(4,1));
    topPanel.add(errorLabel);
    topPanel.add(titlePanel);
    topPanel.add(buttonPanel);
    topPanel.add(excPanel);

    fileNameState = editor.EXSFileName;
    read = editor.readFile(new java.io.File(fileNameState));
    if (read.startsWith("Error")) read = "";
    stateTextArea = new JTextArea(read, 8, 16);
    stateTextArea.setFont(font);

    fileNameVar = editor.EXCFileName;
    read = editor.readFile(new java.io.File(fileNameVar));
    if (read.startsWith("Error")) read = "";
    varTextArea = new JTextArea(read, 8, 16);
    varTextArea.setFont(font);

		textPanel1 = new JPanel();
    textPanel1.add(new JScrollPane(stateTextArea));
    textPanel1.setBorder(border);
		textPanel2 = new JPanel();
    textPanel2.add(new JScrollPane(varTextArea));
    textPanel2.setBorder(border);
		textPanel = new JPanel();
    textPanel.setLayout(new java.awt.GridLayout(1,2));
    textPanel.add(textPanel1);
    textPanel.add(textPanel2);

    getRootPane().setDefaultButton(okButton);
    getRootPane().registerKeyboardAction(
      this,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);
    getContentPane().add(textPanel, java.awt.BorderLayout.CENTER);
    getContentPane().add(OKPanel, java.awt.BorderLayout.SOUTH);
    setTitle(Config.TraceOptions);
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setSize(Config.getIntProperty("WIDTH")/2,
            3*Config.getIntProperty("HEIGHT")/5);
    setLocationRelativeTo(null);
		validate();
    setVisible(true);
    EUI.append(stateTextArea, null);  // Set caret to end of display
  }

  // Property must be a positive integer; return -1 if not
  private int checkProperty(JTextField f) {
    String s = f.getText();
    try {
      int i = Integer.parseInt(s);
      if (i > 0) return i;
      else throw new NumberFormatException();
    }
    catch (NumberFormatException e) {
      errorLabel.setText("Value must be a positive integer");
      return -1;
    }
  }

  private void setProperty(int i, String c) {
    Config.setIntProperty(c, i);
  }

  public void actionPerformed(ActionEvent e) {
    int i1, i2;
		if (e.getSource() == okButton) {
      i1 = checkProperty(stateWidthField);
      if (i1 == -1) return; 
      i2 = checkProperty(varWidthField);
      if (i2 == -1) return; 
      setProperty(i1, "STATEMENT_WIDTH");
      setProperty(i2, "VARIABLE_WIDTH");

      if (!fileNameState.equals("")) {
        editor.writeFile(new java.io.File(fileNameState), stateTextArea);
        filter.setExcluded(stateTextArea.getText(), false);
		  }
      if (!fileNameVar.equals("")) {
        editor.writeFile(new java.io.File(fileNameVar), varTextArea);
        filter.setExcluded(varTextArea.getText(), true);
		  }
      dispose();
		}
    else
       dispose();
	}
}
