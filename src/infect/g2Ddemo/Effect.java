package infect.g2Ddemo;

import java.awt.image.BufferedImage;

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

    public abstract String getName();

    public abstract void drawEffect();
}
