/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Edit excluded variables
 */

package com.spinroot.jspin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

class Excluded extends JFrame implements ActionListener {
    private JTextArea names;
    private JButton okButton = new JButton(Config.OK);
    private JButton cancelButton = new JButton(Config.Cancel);
    private JPanel textPanel = new JPanel();
    private JPanel OKPanel = new JPanel();
    private Editor editor;
    private com.spinroot.filterSpin.Filter filter;
    private boolean exVar;
    private String fileName;

    Excluded(Editor editor, java.awt.Font font, com.spinroot.filterSpin.Filter filter, boolean v) {
        this.editor = editor;
        this.filter = filter;
        exVar = v;
        fileName = exVar ? this.editor.getEXCFileName() : this.editor.getEXSFileName();
        String read = this.editor.readFile(new java.io.File(fileName));
        if (read.startsWith("Error")) read = "";
        names = new JTextArea(read, 9, 32);
        names.setFont(font);
        textPanel.add(new JScrollPane(names));
        OKPanel.add(okButton);
        OKPanel.add(cancelButton);
        okButton.setMnemonic(Config.OKMN);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);
        getRootPane().registerKeyboardAction(this,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getContentPane().add(textPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(OKPanel, java.awt.BorderLayout.SOUTH);
        setTitle(exVar ? Config.ExcludedV : Config.ExcludedS);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(Config.getIntProperty("WIDTH") / 3,
                Config.getIntProperty("HEIGHT") / 3);
        setLocationRelativeTo(null);
        validate();
        setVisible(true);
        jSpin.append(names, null);  // Set caret to end of display
    }

    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == okButton) && !fileName.equals("")) {
            editor.writeFile(new java.io.File(fileName), names);
            filter.setExcluded(names.getText(), exVar);
        }
        dispose();
    }
}
