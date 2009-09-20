/* Copyright 2005-9 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
// Display png image of state space
package eui;
import javax.swing.*;
import java.awt.event.*;
//import java.awt.*;
class DisplayImage extends JFrame implements ActionListener {
  private class ImagePanel extends JPanel {
    private java.awt.Image image;
    ImagePanel(String fileName) {
      image = java.awt.Toolkit.getDefaultToolkit().createImage(fileName);
      java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
      tracker.addImage(image, 0);
      try { tracker.waitForID(0); }
      catch (InterruptedException e) {}
      setPreferredSize(new java.awt.Dimension(image.getWidth(this), image.getHeight(this)));
    }
    public void paintComponent(java.awt.Graphics g) {
      super.paintComponent(g);
      g.drawImage(image, 0, 0, null);
    }
  }

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
