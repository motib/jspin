/* Copyright 2003-7 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Editor for Promela programs
 */

package eui;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

class Editor implements ClipboardOwner, DocumentListener {
  File   file;  		       // The file to be edited
  File   lastFile;         // Previous file to be edited
  String fileName; 	       // The name of the file
  String fileRoot; 	       // The file name without its extension
  String extension = "";   // The extension of the source file
  String root; 		         // The path to the file
  String LTLFileName = ""; // LTL file name for this source file
  String OUTFileName = ""; // Spin display output file name
  String EXCFileName = ""; // File for excluded variable names
  String EXSFileName = ""; // File for excluded statements
  String PRPFileName = ""; // Property file name for this source file
  String PRPName;		       // Property file name without path

  private JTextArea   area;  		   // The area for editing
  private JTextField  LTLField; 	 // The area for LTL formulas
  private JTextArea   messageArea; // The area for messages
  private String      findString;  // The string to search for
  private int         findLoc;  	 // The last location where it was found
  private boolean     modified;  	 // Was area modified?
  private boolean     LTLmodified; // Was LTL field modified?
  private Border      border1;  	 // Border - not modified
  private Border      border2;  	 // Border - modified
  private Clipboard   clipboard;
  private LineNumbers lineNumbers;
  private Filter      filter;      // For setting excluded variable names

  private static final Border border = 
    BorderFactory.createMatteBorder(2,0,0,0,Color.gray);

  public Editor(JScrollPane jsp, JTextArea a, JTextArea m, JTextField l,
      javax.swing.event.UndoableEditListener ud, Filter f) {
    area = a;
    area.getDocument().addDocumentListener(this);
    area.getDocument().addUndoableEditListener(ud);
    LTLField = l;
    LTLField.getDocument().addDocumentListener(this);
    messageArea = m;
    PRPName = "";
    clipboard = area.getToolkit().getSystemClipboard();
    lineNumbers = new LineNumbers(area);
    jsp.setRowHeaderView(lineNumbers);
    filter = f;
  }

  void focus(boolean start) {
    area.requestFocusInWindow();
    if (start)
      area.setCaretPosition(0);
  }

  void caretToLine(int line) {
    try {
      area.requestFocusInWindow();
      area.setCaretPosition(area.getLineStartOffset(line-1));
    } catch (Exception e) {
      area.setCaretPosition(0);
    }
  }

  void cut() {
    if (area.getSelectionStart() != area.getSelectionEnd()) {
      String s = area.getSelectedText();
      StringSelection contents = new StringSelection(s);
      clipboard.setContents(contents, this);
      area.replaceRange(
        null, 
        area.getSelectionStart(),
        area.getSelectionEnd());
    }
  }

  void copy() {
    if (area.getSelectionStart() != area.getSelectionEnd()) {
      String s = area.getSelectedText();
      StringSelection contents = new StringSelection(s);
      clipboard.setContents(contents, this);
    }
  }

  void paste() {
    Transferable content = clipboard.getContents(this);
    if (content != null) {
      try {
        String s = (String)content.getTransferData(
          DataFlavor.stringFlavor);
        area.replaceRange(
          s, 
          area.getSelectionStart(),
          area.getSelectionEnd());
      } catch (Exception ex) {
        System.err.println("Can't paste");
      }
    }
  }

  private void showArea(String title) {
    String previousTitle = " / " + 
      ((lastFile == null) ? "-" : lastFile.getName()) + " ";
    findString = null;
    findLoc = 0;
    modified = false;
    border1 = BorderFactory.createTitledBorder(
      border, " " + title + previousTitle,
      TitledBorder.LEFT, TitledBorder.TOP);
    border2 = BorderFactory.createTitledBorder(
      border, " " + title + " * " + previousTitle, 
      TitledBorder.LEFT, TitledBorder.TOP);
    area.setBorder(border1);
    focus(true);
  }

  void newFile() {
    root = "";
    file = null;
    fileName = "";
    fileRoot = "";
    LTLFileName = "";
    OUTFileName = "";
    EXCFileName = "";
    EXSFileName = "";
    PRPFileName = "";
    area.setText("");
    showArea(" ");
  }

  private void setPMLRootAndName() {
    fileName = file.getName();
    if (file.getParentFile() == null)
      root = "";
    else
      root = file.getParentFile().getAbsolutePath();
    if (fileName.lastIndexOf('.') == -1)
      fileRoot = fileName;
    else {
      fileRoot = fileName.substring(0, fileName.lastIndexOf('.'));
      extension = fileName.substring(fileName.lastIndexOf('.'));
    }
    LTLFileName = root + File.separator + fileRoot + ".ltl";
    OUTFileName = root + File.separator + fileRoot + ".out";
    EXCFileName = root + File.separator + fileRoot + ".exc";
    EXSFileName = root + File.separator + fileRoot + ".exs";
    PRPFileName = root + File.separator + fileRoot + ".prp";
    PRPName = "";
    showArea(fileName);
  }

  private void setPRPRootAndName(File fc) {
    String fileName = fc.getName();
		String fileRoot, root;
    if (file.getParentFile() == null)
      root = "";
    else
      root = fc.getParentFile().getAbsolutePath();
    if (fileName.lastIndexOf('.') == -1)
      fileRoot = fileName;
    else
      fileRoot = fileName.substring(0, fileName.lastIndexOf('.'));
    LTLFileName = root + File.separator + fileRoot + ".ltl";
    PRPFileName = root + File.separator + fileRoot + ".prp";
    PRPName = fileRoot + ".prp";
  }

	// Open file: Promela (and excluded) or property file
  void openFile(File fc, boolean pml) {
    String prp = "";
    if (pml) {
      if (!fc.exists()) {
        messageArea.append("File "+fc+" does not exist\n");
        focus(true);
        return;
      }
      file = fc;
      area.setText(readFile(fc));
      setPMLRootAndName();
      prp = readFile(new java.io.File(PRPFileName));
      filter.setExcluded(readFile(new java.io.File(EXCFileName)), true);
      filter.setExcluded(readFile(new java.io.File(EXSFileName)), false);
    }
    else {
      if (fc.exists())
        prp = readFile(fc);
      else
        messageArea.append("Creating new prp file\n");
      setPRPRootAndName(fc);
    }
    LTLField.setText(prp.startsWith("Error") ? "" : prp);
    LTLmodified = false;
  }

  String readFile(File fc) {
    BufferedReader textReader = null;
    try {
      textReader = new BufferedReader(new FileReader(fc));
    } catch (IOException e) {
      focus(true);
      return "Error opening file " + fc;
    }
    StringWriter textWriter = new StringWriter();
    int c = 0;
    try {
      while (true) {
        c = textReader.read();
        if (c == -1)
          break;
        else
          textWriter.write(c);
      }
    } catch (IOException e) {
      focus(true);
      return "Error reading file " + fc;
    }
    try {
      textReader.close();
    } 
    catch (IOException e) {
      focus(true);
      return "Error closing file " + fc;
    }
    return textWriter.toString();
  }

  void saveFile(File fc) {
    if (fc != null) {
      file = fc;
      setPMLRootAndName();
      LTLmodified = true;  // To force saving
      modified = true;
    }
    if (LTLmodified) {
      if (!LTLField.getText().equals(""))
        writeFile(new java.io.File(PRPFileName), LTLField);
      LTLmodified = false;
    }
    if (modified) {
      area.setBorder(border1);
      writeFile(file, area);
      modified = false;
    }
  }

  void writeFile(File fc, javax.swing.text.JTextComponent area) {
    if (fc == null || area == null)
      return;
    BufferedWriter textWriter = null;
    try {
      textWriter = new BufferedWriter(new FileWriter(fc));
    } catch (IOException e) {
      System.err.println("Error opening file " + fc);
      focus(true);
      return;
    }
    BufferedReader textReader = 
      new BufferedReader(new StringReader(area.getText()));
    int c = 0;
    try {
      while (true) {
        c = textReader.read();
        if (c == -1)
          break;
        else
          textWriter.write(c);
      }
    } catch (IOException e) {
      System.err.println("Error writing file " + fc);
      focus(true);
      return;
    }
    try {
      textWriter.flush();
      textWriter.close();
    } catch (IOException e) {
      System.err.println("Error closing file " + fc);
      focus(true);
      return;
    }
    messageArea.append("Saved " + fc.getName() + "\n");
  }

  void find() {
    findString = JOptionPane.showInputDialog(
      area, null, "Find", JOptionPane.PLAIN_MESSAGE);
    if (findString != null) {
      search();
      findLoc = area.getCaretPosition();
    } else
      focus(false);
  }

  void findAgain() {
    if (findString != null)
      search();
    else
      focus(false);
  }

  private void search() {
    int found = area.getText().toLowerCase().indexOf(
      findString.toLowerCase(), findLoc);
    if (found != -1) {
      findLoc = found + 1;
      area.setCaretPosition(found);
      area.moveCaretPosition(found+findString.length());
    }
    focus(false);
  }

  private void setModified(DocumentEvent e) {
    if (e.getDocument() == area.getDocument()) {
      modified = true;
      area.setBorder(border2);
      final int lines = area.getLineCount();
      if (lineNumbers != null) {
        Runnable updateAComponent = new Runnable() {
          public void run() {
            lineNumbers.setHeightByLines(lines);
          }
        };
        SwingUtilities.invokeLater(updateAComponent);
      }
    } 
		else
      LTLmodified = true;
  }

  public void changedUpdate(DocumentEvent e) {
    setModified(e);
  }

  public void insertUpdate(DocumentEvent e)  {
    setModified(e);
  }

  public void removeUpdate(DocumentEvent e)  {
    setModified(e);
  }

  public void lostOwnership(Clipboard clipboard, Transferable content) {}
}
