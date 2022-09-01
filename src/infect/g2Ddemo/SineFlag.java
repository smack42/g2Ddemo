package infect.g2Ddemo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class SineFlag extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private final BufferedImage buf;
    private final long delayNs; // nano seconds

    public SineFlag(int displayHz) {
        super(false); // isDoubleBuffered = false
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        delayNs = 1_000_000_000L / displayHz;
        timerIncr = delayNs / 290_000_000.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawEffect((Graphics2D)g);
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Thread animator = new Thread() {
            @Override
            public void run() {
                while (true) {
                    final long tsBefore = System.nanoTime();
                    repaint();
                    final long tsDiff = System.nanoTime() - tsBefore;
                    final long sleep = delayNs > tsDiff  ?  delayNs - tsDiff  :  2_000_000; // 2 ms
                    try {
                        Thread.sleep(sleep / 1_000_000, (int)(sleep % 1_000_000));
                    }
                    catch (InterruptedException e) {
                        // ignored
                    }
                }
            }
        };
        animator.start();
    }


    private final String theText =
            "** **   ** **** ****  **** *****"+
            "** **   ** **** **** ****  *****"+
            "** **   ** **   **   **      ** "+
            "** **   ** **   **   **      ** "+
            "** **   ** **   **   **      ** "+
            "** ***  ** **   **   **      ** "+
            "** **** ** **** **** **      ** "+
            "** ** **** **** **** **      ** "+
            "** **  *** **   **   **      ** "+
            "** **   ** **   **   **      ** "+
            "** **   ** **   **   **      ** "+
            "** **   ** **   **   **      ** "+
            "** **   ** **   **** ****    ** "+
            "** **   ** **   ****  ****   ** "+
            "                                "+
            "  THERE IS NO MEDICINE AGAINST  ";
    private final Font theFont = new Font("Monospaced", Font.BOLD, 24);
    private final Color colGrd1 = new Color(0x40, 0x10, 0x40); // gradient dark pink -> dark blue
    private final Color colGrd2 = new Color(0x30, 0x10, 0x40); // gradient dark pink -> dark blue
    private final Color colGrd3 = new Color(0x20, 0x10, 0x40); // gradient dark pink -> dark blue
    private final Color colBack = new Color(0x10, 0x10, 0x40); // dark blue
    private final Color colText = new Color(0xe0, 0x20, 0x20); // red
    private final double timerIncr;
    private double timer = 0;

    private void drawEffect(Graphics2D g2) {
        // draw to offscreen buffer
        Graphics2D bufg2 = buf.createGraphics();
        
        // clear
        bufg2.setColor(colBack);
        bufg2.fillRect(0, 0, WIDTH, HEIGHT);
        
        // gradient at top of screen
        bufg2.setColor(colGrd1);
        bufg2.fillRect(0, 0, WIDTH, 8);
        bufg2.setColor(colGrd2);
        bufg2.fillRect(0, 8, WIDTH, 8);
        bufg2.setColor(colGrd3);
        bufg2.fillRect(0, 16, WIDTH, 8);
        
        // text in animated sine-wave flag
        bufg2.setColor(colText);
        bufg2.setFont(theFont);
        for (int i = 0;  i < theText.length();  ++i) {
            final char c = theText.charAt(i);
            if (!Character.isWhitespace(c)) {
                final float sin = (float)(32 * Math.sin(timer + i / 5.25));
                String txt = String.valueOf(c);
                bufg2.drawString(txt,
                        86  + (i % 32) * 15 + sin,
                        100 + (i / 32) * 15 + sin);
            }
        }
        timer += timerIncr; //0.05;
        
        // mirror at bottom of screen
        AffineTransform at = new AffineTransform();
        at.scale(1, -0.25); // flip vertically and reduce height
        at.translate(0, -HEIGHT * 4);
        bufg2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        bufg2.drawImage(buf, at, this);
        
        bufg2.setPaint(new GradientPaint(0, 380, Color.DARK_GRAY, 0, HEIGHT, Color.WHITE));
        bufg2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        bufg2.fillRect(0, 380, WIDTH, 100);
        
        // finished, send buffer to screen
        bufg2.dispose();
        g2.drawImage(buf, 0, 0, this);
    }


}
