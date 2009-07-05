/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
// Class DisplayFile for Help, About, Display raw
package eui;
import javax.swing.*;
import java.awt.event.*;
class DisplayImage extends JFrame implements ActionListener { 
  DisplayImage(String file) {
    ImagePanel image = new ImagePanel(file);
    getContentPane().add(new JScrollPane(image));
    setDefaultCloseOperation(
      javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    getRootPane().registerKeyboardAction(this,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    setTitle(file);
    setSize((int) (0.8*Config.getIntProperty("WIDTH")), 
            (int) (0.9*Config.getIntProperty("HEIGHT")));
    setLocationRelativeTo(null); 
    setVisible(true);
  }

    public void actionPerformed(ActionEvent e) {
		dispose();
	}
}
