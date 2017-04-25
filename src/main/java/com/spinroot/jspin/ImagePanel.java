/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Display images from dot file
 */
package com.spinroot.jspin;

import javax.swing.*;
import java.awt.*;

class ImagePanel extends JPanel {
    private Image image;

    ImagePanel(String fileName) {
        image = Toolkit.getDefaultToolkit().createImage(fileName);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
