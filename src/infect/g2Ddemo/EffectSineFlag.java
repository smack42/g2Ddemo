package infect.g2Ddemo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class EffectSineFlag extends Effect {

    public EffectSineFlag(long frameDurationNs) {
        super(frameDurationNs);
        timerIncr = frameDurationNs / 290_000_000.0;
        atMirror = new AffineTransform();
        atMirror.scale(1, -0.25); // flip vertically and reduce height
        atMirror.translate(0, -HEIGHT * 4);
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
    private final AffineTransform atMirror;
    private final AlphaComposite acMirror = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    private final AlphaComposite acMirrorGradient = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
    private final double timerIncr;
    private double timer = 0;


    @Override
    public String getName() {
        return "Sine Flag";
    }


    @Override
    public void drawEffect() {
        Graphics2D g = buf.createGraphics();
        g.setColor(colBack);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // gradient at top of screen
        g.setColor(colGrd1);
        g.fillRect(0, 0, WIDTH, 8);
        g.setColor(colGrd2);
        g.fillRect(0, 8, WIDTH, 8);
        g.setColor(colGrd3);
        g.fillRect(0, 16, WIDTH, 8);

        // text in animated sine-wave flag
        g.setColor(colText);
        g.setFont(theFont);
        for (int i = 0;  i < theText.length();  ++i) {
            final char c = theText.charAt(i);
            if (c != ' ') {
                final float sin = (float)(32 * Math.sin(timer + i / 5.25));
                String txt = String.valueOf(c);
                g.drawString(txt,
                        86  + (i % 32) * 15 + sin * 1.5f,
                        100 + (i / 32) * 15 + sin);
            }
        }
        timer += timerIncr; //0.05;

        // mirror at bottom of screen
        g.setComposite(acMirror);
        g.drawImage(buf, atMirror, null);
        g.setPaint(new GradientPaint(0, 380, Color.DARK_GRAY, 0, HEIGHT, Color.WHITE));
        g.setComposite(acMirrorGradient);
        g.fillRect(0, 380, WIDTH, 100);

        g.dispose();
    }
}
