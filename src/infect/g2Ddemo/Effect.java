package infect.g2Ddemo;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public abstract class Effect {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final double ASPECT_RATIO = (double)WIDTH / (double)HEIGHT;

    protected final BufferedImage buf;
    protected final long frameDurationNs; // nano seconds

    public Effect(long frameDurationNs) {
        this.buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.frameDurationNs = frameDurationNs;
    }

    public BufferedImage getBuffer() {
        return buf;
    }

    public BufferedImage loadImageFile(String fileName) {
        ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource(fileName));
        Image tmpImage = icon.getImage();
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();
        return image;
    }

    public abstract String getName();

    public abstract void drawEffect();
}
