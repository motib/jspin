/* Copyright 2003-7 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Editor for Promela programs
 */

package com.spinroot.jspin;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class Editor implements ClipboardOwner, DocumentListener {
    private static final Border border = BorderFactory.createMatteBorder(2, 0, 0, 0, Color.gray);
    /**
     * The file to be edited
     */
    private File file;
    /**
     * Previous file to be edited
     */
    private File lastFile;
    /**
     * The name of the file
     */
    private String fileName;
    /**
     * The file name without its extension
     */
    private String fileRoot;
    /**
     * The extension of the source file
     */
    private String extension = "";
    /**
     * The path to the file
     */
    private String root;
    /**
     * LTL file name for this source file
     */
    private String LTLFileName = "";
    /**
     * Spin display output file name
     */
    private String OUTFileName = "";
    /**
     * File for excluded variable names
     */
    private String EXCFileName = "";
    /**
     * File for excluded statements
     */
    private String EXSFileName = "";
    /**
     * Property file name for this source file
     */
    private String PRPFileName = "";
    /**
     * Property file name without path
     */
    private String PRPName;
    /**
     * The area for editing
     */
    private JTextArea area;
    /**
     * The area for LTL formulas
     */
    private JTextField LTLField;
    /**
     * The area for messages
     */
    private JTextArea messageArea;
    /**
     * The string to search for
     */
    private String findString;
    /**
     * The last location where it was found
     */
    private int findLoc;
    /**
     * Was area modified?
     */
    private boolean modified;
    /**
     * Was LTL field modified?
     */
    private boolean LTLmodified;
    /**
     * Border - not modified
     */
    private Border border1;
    /**
     * Border - modified
     */
    private Border border2;
    private Clipboard clipboard;
    private LineNumbers lineNumbers;
    /**
     * For setting excluded variable names
     */
    private com.spinroot.filterSpin.Filter filter;

    public Editor(JScrollPane jsp, JTextArea area, JTextArea m, JTextField l,
                  javax.swing.event.UndoableEditListener ud, com.spinroot.filterSpin.Filter filter) {
        this.area = area;
        LTLField = l;
        LTLField.getDocument().addDocumentListener(this);
        messageArea = m;
        PRPName = "";
        clipboard = this.area.getToolkit().getSystemClipboard();
        lineNumbers = new LineNumbers(this.area);
        jsp.setRowHeaderView(lineNumbers);
        this.filter = filter;
    }

    public File getFile() {
        return file;
    }

    public File getLastFile() {
        return lastFile;
    }

    public void setLastFile(File lastFile) {
        this.lastFile = lastFile;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }

    public String getExtension() {
        return extension;
    }

    public String getRoot() {
        return root;
    }

    public String getLTLFileName() {
        return LTLFileName;
    }

    public String getOUTFileName() {
        return OUTFileName;
    }

    public String getEXCFileName() {
        return EXCFileName;
    }

    public String getEXSFileName() {
        return EXSFileName;
    }

    void focus(boolean start) {
        area.requestFocusInWindow();
        if (start)
            area.setCaretPosition(0);
    }

    void caretToLine(int line) {
        try {
            area.requestFocusInWindow();
            area.setCaretPosition(area.getLineStartOffset(line - 1));
        } catch (Exception e) {
            area.setCaretPosition(0);
        }
    }

    void cut() {
        if (area.getSelectionStart() != area.getSelectionEnd()) {
            String s = area.getSelectedText();
            StringSelection contents = new StringSelection(s);
            clipboard.setContents(contents, this);
            area.replaceRange(null,
                    area.getSelectionStart(), area.getSelectionEnd());
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
                String s = (String) content.getTransferData(
                        DataFlavor.stringFlavor);
                area.replaceRange(s,
                        area.getSelectionStart(), area.getSelectionEnd());
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
                messageArea.append("File " + fc + " does not exist\n");
                focus(true);
                return;
            }
            file = fc;
            area.setText(readFile(fc));
            setPMLRootAndName();
            prp = readFile(new java.io.File(PRPFileName));
            filter.setExcluded(readFile(new java.io.File(EXCFileName)), true);
            filter.setExcluded(readFile(new java.io.File(EXSFileName)), false);
        } else {
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
        try {
            return FileUtils.readFileToString(fc, StandardCharsets.UTF_8);
        } catch (IOException e) {
            focus(true);
            return "IO error with file " + fc;
        }
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
        if (fc == null || area == null) {
            return;
        }
        try {
            FileUtils.writeStringToFile(fc, area.getText(), StandardCharsets.UTF_8);
            messageArea.append("Saved " + fc.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            area.moveCaretPosition(found + findString.length());
        }
        focus(false);
    }

    private void setModified(DocumentEvent e) {
        modified = true;
        area.setBorder(border2);
        final int lines = area.getLineCount();
        if (lineNumbers != null) {
            Runnable updateAComponent = () -> lineNumbers.setHeightByLines(lines);
            SwingUtilities.invokeLater(updateAComponent);
        }
    }

    public void modifiedLTL() {
        LTLmodified = true;
    }

    public void changedUpdate(DocumentEvent e) {
        setModified(e);
    }

    public void insertUpdate(DocumentEvent e) {
        setModified(e);
    }

    public void removeUpdate(DocumentEvent e) {
        setModified(e);
    }

    public void lostOwnership(Clipboard clipboard, Transferable content) {
    }
}
