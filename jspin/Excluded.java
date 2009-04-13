/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Edit excluded variables
 */

package jspin;
import javax.swing.*;
import java.awt.event.*;

class Excluded extends JFrame implements ActionListener {
    private JTextArea names;
    private JButton okButton = new JButton(Config.OK);
    private JButton cancelButton = new JButton(Config.Cancel);
    private JPanel textPanel  = new JPanel();
    private JPanel OKPanel = new JPanel();
    private Editor editor;
    private Filter filter;
    private boolean exVar;
    private String fileName;

	Excluded(Editor e, java.awt.Font font, Filter f, boolean v) {
        editor = e;
        filter = f;
        exVar = v;
        fileName = exVar ? editor.EXCFileName : editor.EXSFileName;
        String read = editor.readFile(new java.io.File(fileName));
        if (read.startsWith("Error")) read = "";
        names =  new JTextArea(read, 8, 20);
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
        setSize(Config.getIntProperty("WIDTH")/3, 
                Config.getIntProperty("HEIGHT")/3);
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
