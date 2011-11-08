/* Copyright 2003-4 by Niko Myller and Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * 	Display line numbers in editor
 */

package eui;
import java.awt.*;
import javax.swing.*;

public class LineNumbers extends JComponent {
  private int size;   	// Width of line numbers
  private Font font;   	// Font of editor window
  private int ascent;  	// Data for computation of line height
  private int increment;
  private int top;

  LineNumbers(JTextArea area) {
    font = area.getFont();
    FontMetrics fm = getFontMetrics(font);
    size = fm.stringWidth("000") + 6;
    ascent = fm.getAscent();
    increment = ascent + fm.getDescent();
    // Extra line for file name in border
    top = fm.getHeight() + fm.getDescent() + 3; 
    setPreferredSize(new Dimension(size, area.getSize().height));
    revalidate();
  }

  void setHeightByLines(int lines) {
    int height = top + ascent + (lines * increment);
    setPreferredSize(new Dimension(size, height));
    revalidate();
  }

  public void paintComponent(Graphics g) {
    Rectangle drawHere = g.getClipBounds();
    //Background painting
    g.setColor(new Color(204, 204, 204));
    g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
    // Ruler labels in black color
    g.setFont(font);
    g.setColor(Color.black);
    int end = 0; 	//This keeps the position for the end of the view
    int start = 0; 	//This keeps the position for the beginning of the view
    start = (drawHere.y / increment) * increment;
    end = (((drawHere.y + drawHere.height) / increment) + 1) * increment;
    int lineNumber = (int) Math.floor(drawHere.y / increment) + 1;
    start += top + ascent;
    end += top + ascent;
    //labels are painted here
    for (int i = start; i < end; i += increment) {
        g.drawString(Integer.toString(lineNumber), 3, i);
        lineNumber++;
    }
  }
}
