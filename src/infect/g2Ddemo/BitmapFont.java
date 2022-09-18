package infect.g2Ddemo;

import java.awt.Graphics;

public interface BitmapFont {

    /** draw a single character into the specified graphics at the specified coordinates **/
    public void drawString(Graphics g, char c,     int x, int y);

    /** draw a string of characters into the specified graphics at the specified coordinates **/
    public void drawString(Graphics g, String str, int x, int y);
}
