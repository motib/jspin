/* Copyright 2009 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Dialog for setting Common Options 
 */

package jspin;
import javax.swing.*;
import java.awt.event.*;

class Limits extends JFrame implements ActionListener {
  private JTextField stepsField, progressField, stackField, locationField;
  private JLabel stepsLabel, progressLabel, stackLabel, locationLabel;
  private JButton okButton, cancelButton;
  private JLabel errorLabel;
  private JPanel topPanel, buttonPanel, OKPanel;

	Limits() {
    stepsLabel = new JLabel(Config.TotalSteps);
    stepsField = new JTextField(Config.getStringProperty("TOTAL_STEPS"), 10);
    progressLabel = new JLabel(Config.ProgressSteps);
    progressField = new JTextField(Config.getStringProperty("PROGRESS_STEPS"), 10);
    stackLabel = new JLabel(Config.StateStack);
    stackField = new JTextField(Config.getStringProperty("STATE_STACK"), 10);
    locationLabel = new JLabel(Config.LocationStack);
    locationField = new JTextField(Config.getStringProperty("LOCATION_STACK"), 10);

		buttonPanel = new JPanel();
    buttonPanel.setLayout(new java.awt.GridLayout(4,2));
    buttonPanel.add(stepsLabel);
    buttonPanel.add(stepsField);
    buttonPanel.add(progressLabel);
    buttonPanel.add(progressField);
    buttonPanel.add(stackLabel);
    buttonPanel.add(stackField);
    buttonPanel.add(locationLabel);
    buttonPanel.add(locationField);

		okButton = new JButton(Config.OK);
    okButton.setMnemonic(Config.OKMN);
		okButton.addActionListener(this);
		cancelButton = new JButton(Config.Cancel);
		cancelButton.addActionListener(this);
		OKPanel = new JPanel();
    OKPanel.setLayout(new java.awt.GridLayout(1,2));
    OKPanel.add(okButton);
    OKPanel.add(cancelButton);

    getRootPane().setDefaultButton(okButton);
    getRootPane().registerKeyboardAction(this,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    errorLabel = new JLabel(" ");
		topPanel = new JPanel();
        topPanel.setLayout(new java.awt.GridLayout(1,1));
    topPanel.add(errorLabel);
    topPanel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
    buttonPanel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
    OKPanel.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));

    getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);
    getContentPane().add(buttonPanel, java.awt.BorderLayout.CENTER);
    getContentPane().add(OKPanel, java.awt.BorderLayout.SOUTH);
    setTitle(Config.Limits);
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setSize(Config.getIntProperty("WIDTH")/4, 
            Config.getIntProperty("HEIGHT")/3);
    setLocationRelativeTo(null);
		validate();
    setVisible(true);
  }

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
    int i1, i2, i3, i4;
		if (e.getSource() == okButton) {
      i1 = checkProperty(stepsField);    if (i1 == -1) return; 
      i2 = checkProperty(progressField); if (i2 == -1) return; 
      i3 = checkProperty(stackField);    if (i3 == -1) return; 
      i4 = checkProperty(locationField); if (i4 == -1) return; 
      setProperty(i1, "TOTAL_STEPS");
      setProperty(i2, "PROGRESS_STEPS");
      setProperty(i3, "STATE_STACK");
      setProperty(i4, "LOCATION_STACK");
      dispose();
		}
    else
       dispose();
	}
}
