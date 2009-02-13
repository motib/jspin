/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Dialog for setting Common Options 
 */

package jspin;
import javax.swing.*;
import java.awt.event.*;

class Options extends JFrame implements ActionListener, ItemListener {
	static final String 
        ST = "-p", LOC = "-l", GLO = "-g", SENT = "-s", REC = "-r";
    private JCheckBox stBox, locBox, gloBox, sentBox, recBox;
    private JButton okButton, cancelButton, clearAllButton, setAllButton;
    private JPanel buttonPanel, OKPanel;
	private boolean st, loc, glo, sent, rec;

    JCheckBox create(String s, boolean set, int mn) {
        JCheckBox cb = new JCheckBox(s, set);
        cb.setMnemonic(mn);
        cb.addItemListener(this);
        buttonPanel.add(cb);
        return cb;
    }

	Options() {
		String s = Config.getStringProperty("COMMON_OPTIONS");
		st = s.indexOf(ST) != -1;
		loc = s.indexOf(LOC) != -1;
		glo = s.indexOf(GLO) != -1;
		sent = s.indexOf(SENT) != -1;
		rec = s.indexOf(REC) != -1;
		buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.GridLayout(5,1));
		stBox   = create(Config.Statements, st, Config.StatementsMN);
		locBox  = create(Config.Locals, loc, Config.LocalsMN);
		gloBox  = create(Config.Globals, glo, Config.GlobalsMN);
		sentBox = create(Config.Sent, sent, Config.SentMN);
		recBox  = create(Config.Received, rec, Config.ReceivedMN);
		OKPanel = new JPanel();
        OKPanel.setLayout(new java.awt.GridLayout(2,2));
		okButton = new JButton(Config.OK);
		cancelButton = new JButton(Config.Cancel);
		clearAllButton = new JButton(Config.ClearAll);
		setAllButton = new JButton(Config.SetAll);
        okButton.setMnemonic(Config.OKMN);
        clearAllButton.setMnemonic(Config.ClearAllMN);
        setAllButton.setMnemonic(Config.SetAllMN);
        OKPanel.add(okButton);
        OKPanel.add(cancelButton);
        OKPanel.add(setAllButton);
        OKPanel.add(clearAllButton);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		setAllButton.addActionListener(this);
		clearAllButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);
        getRootPane().registerKeyboardAction(this,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        getContentPane().add(buttonPanel, java.awt.BorderLayout.CENTER);
        getContentPane().add(OKPanel, java.awt.BorderLayout.SOUTH);
        setTitle(Config.OPTIONS_TITLE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(200, 250); 
        setLocationRelativeTo(null);
		validate();
        setVisible(true);
    }

    private void setClear(boolean b) {
        st = b; loc = b; glo = b; sent = b; rec = b;
        stBox.setSelected(b);
        locBox.setSelected(b);
        gloBox.setSelected(b);
        sentBox.setSelected(b);
        recBox.setSelected(b);
    }
    
	public void actionPerformed(ActionEvent e) {
        String options = "";
		if (e.getSource() == okButton) {
			options = options + (st ? ST + " " : "");
			options = options + (loc ? LOC + " " : "");
			options = options + (glo ? GLO + " " : "");
			options = options + (sent ? SENT + " " : "");
			options = options + (rec ? REC + " " : "");
            Config.setStringProperty("COMMON_OPTIONS", options);
            dispose();
		}
        else if (e.getSource() == setAllButton) {
            setClear(true);
        }
        else if (e.getSource() == clearAllButton) {
            setClear(false);
        }
        else
            dispose();
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getItemSelectable() == stBox)
            st = e.getStateChange() == ItemEvent.SELECTED;
		else if (e.getItemSelectable() == locBox)
            loc = e.getStateChange() == ItemEvent.SELECTED;
		else if (e.getItemSelectable() == gloBox)
            glo = e.getStateChange() == ItemEvent.SELECTED;
		else if (e.getItemSelectable() == sentBox)
            sent = e.getStateChange() == ItemEvent.SELECTED;
		else if (e.getItemSelectable() == recBox)
            rec = e.getStateChange() == ItemEvent.SELECTED;
    }
}
