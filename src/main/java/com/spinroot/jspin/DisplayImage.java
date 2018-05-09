/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
// Class DisplayFile for Help, About, Display raw
package com.spinroot.jspin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
        setSize((int) (0.8 * Config.getIntProperty("WIDTH")),
                (int) (0.9 * Config.getIntProperty("HEIGHT")));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        dispose();
    }
}

