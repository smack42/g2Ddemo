package infect.g2Ddemo;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Launcher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static volatile boolean doDelay = true;
    private static volatile boolean keepAspectRatio = true;
    private static volatile boolean printInfo = false;
    private static volatile boolean pause = false;

    protected static void createAndShowGUI() {
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice dev = env.getDefaultScreenDevice();
        final int displayHz = dev.getDisplayMode().getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN  ?  60  :  dev.getDisplayMode().getRefreshRate();
        final long delayNs = 1_000_000_000L / displayHz;

        final JFrame jf = new JFrame();
        jf.setTitle("INFECT g2Ddemo");
        jf.setResizable(true);
        jf.setIgnoreRepaint(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                final int kc = e.getKeyCode();
                switch (kc) {
                case KeyEvent.VK_Q: //quit
                case KeyEvent.VK_ESCAPE:
                {
                    System.exit(0);
                    break;
                }
                case KeyEvent.VK_1: //1-to-1 pixel
                case KeyEvent.VK_O: //original size
                {
                    if (dev.getFullScreenWindow() == null) {
                        jf.pack(); // only if not in fullscreen mode
                    }
                    break;
                }
                case KeyEvent.VK_F: //full screen
                case KeyEvent.VK_F11:
                case KeyEvent.VK_ENTER: //with "alt" modifier
                {
                    int mod = e.getModifiersEx();
                    if (kc != KeyEvent.VK_ENTER  ||  (mod & KeyEvent.ALT_DOWN_MASK) != 0) {
                        dev.setFullScreenWindow(dev.getFullScreenWindow() == null  ?  jf  :  null);
                        jf.validate();
                    }
                    break;
                }
                case KeyEvent.VK_D: //delay
                {
                    doDelay = !doDelay;
                    break;
                }
                case KeyEvent.VK_I: //info
                case KeyEvent.VK_F1:
                {
                    printInfo = !printInfo;
                    break;
                }
                case KeyEvent.VK_A: //aspect
                {
                    keepAspectRatio = !keepAspectRatio;
                    break;
                }
                case KeyEvent.VK_P: //pause
                case KeyEvent.VK_SPACE:
                {
                    pause = !pause;
                    break;
                }
                default: // do nothing
                }
            }
        });

        final Canvas canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(Effect.WIDTH, Effect.HEIGHT));
        canvas.setFocusable(false);
        canvas.setIgnoreRepaint(true);
        jf.add(canvas);

        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        //dev.setFullScreenWindow(jf);

        canvas.createBufferStrategy(2); // double buffering
        final BufferStrategy strategy = canvas.getBufferStrategy();

        final Effect effect = new EffectSineFlag(delayNs);

        Thread mainThread = new Thread() {
            private final Font theFont = new Font("Monospaced", Font.PLAIN, 16);
            private int frame = 0, fps = 0, fpsPrevFrame = 0, delayLateFrames = 0;
            private long fpsPrevNano = System.nanoTime(), sleepMillis = 0, sleepNanos = 0;
            @Override
            public void run() {
                // Main loop (from Javadoc of BufferStrategy)
                while (true) {
                    while (pause) {
                        try {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException ignored) { }
                    }

                    final long tsBefore = System.nanoTime();

                    // Prepare for rendering the next frame
                    effect.drawEffect();

                    // Render single frame
                    do {
                        // The following loop ensures that the contents of the drawing buffer
                        // are consistent in case the underlying surface was recreated
                        do {
                            // Get a new graphics context every time through the loop
                            // to make sure the strategy is validated
                            Graphics graphics = strategy.getDrawGraphics();

                            // Render to graphics
                            int x = 0, y = 0, width = canvas.getWidth(), height = canvas.getHeight();
                            final double canvasAspectRatio = (double)width / (double)height;
                            if (keepAspectRatio) {
                                if (canvasAspectRatio < Effect.ASPECT_RATIO) {
                                    int newHeight = (int)(width / Effect.ASPECT_RATIO);
                                    y = (height - newHeight) / 2;
                                    height = newHeight;
                                } else {
                                    int newWidth = (int)(height * Effect.ASPECT_RATIO);
                                    x = (width - newWidth) / 2;
                                    width = newWidth;
                                }
                            }
                            if (x != 0 || y != 0) {
                                graphics.setColor(Color.BLACK);
                                graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                            }
                            graphics.drawImage(effect.getBuffer(), x, y, width, height, null);
                            if (printInfo) {
                                graphics.setColor(Color.WHITE);
                                graphics.setFont(theFont);
                                graphics.drawString("effect:         " + effect.getName(), 16, 0x10);
                                graphics.drawString("frame:          " + frame, 16, 0x20);
                                graphics.drawString("display fps:    " + displayHz + "   " + fps, 16, 0x30);
                                graphics.drawString("delay timer:    " + doDelay + "   " + delayNs + "   " + sleepNanos + "   " + sleepMillis, 16, 0x40);
                                graphics.drawString("aspect ratio:   " + keepAspectRatio + "   " + (float)Effect.ASPECT_RATIO + "   " + (float)canvasAspectRatio, 16, 0x50);
                                graphics.drawString("screen pixels:  " + canvas.getWidth() + " x " + canvas.getHeight() + "   " + width + " x " + height, 16, 0x60);
                                graphics.drawString("Q,Esc : quit", 16, 0x80);
                                graphics.drawString("F,F11 : full screen / window", 16, 0x90);
                                graphics.drawString("P,Space : pause / continue", 16, 0xa0);
                                graphics.drawString("A : aspect ratio", 16, 0xb0);
                                graphics.drawString("O : original size", 16, 0xc0);
                                graphics.drawString("D : delay timer / Vsync", 16, 0xd0);
                                graphics.drawString("I,F1 : this info", 16, 0xe0);
                            }

                            // Dispose the graphics
                            graphics.dispose();

                            // Repeat the rendering if the drawing buffer contents
                            // were restored
                        } while (strategy.contentsRestored());

                        // Display the buffer
                        strategy.show();

                        // Repeat the rendering if the drawing buffer was lost
                    } while (strategy.contentsLost());

                    if (doDelay) {
                        final long tsDiff = System.nanoTime() - tsBefore;
                        sleepNanos = delayNs - tsDiff;
                        sleepMillis = sleepNanos / 1_000_000;
                        if (printInfo) {
                            System.out.println("sleepMillis=" + sleepMillis + "   sleepNanos=" + sleepNanos + "   frame=" + frame);
                        }
                        // try to recognise if page flipping is working correctly
                        // (that means that strategy.show() has already waited for next VSYNC)
                        if (sleepMillis >= 1) {
                            delayLateFrames = 0;
                            try {
                                Thread.sleep(sleepMillis, (int)(sleepNanos % 1_000_000));
                            }
                            catch (InterruptedException ignored) { }
                        } else {
                            if (++delayLateFrames > displayHz) {
                                delayLateFrames = 0;
                                doDelay = false; // switch off after one second
                                if (printInfo) {
                                    System.out.println("deactivated delay timer at frame=" + frame);
                                }
                            }
                        }
                    } else {
                        sleepNanos = sleepMillis = -1;
                        // is it running too fast without Thread.sleep ?
                        if (fps > displayHz * 3) {
                            doDelay = true; // switch on after one second
                            if (printInfo) {
                                System.out.println("activated delay timer at frame=" + frame);
                            }
                        }
                    }
                    ++frame;
                    final long now = System.nanoTime();
                    if (now - fpsPrevNano > 1_000_000_000L) {
                        fps = frame - fpsPrevFrame;
                        fpsPrevNano = now;
                        fpsPrevFrame = frame;
                    }
                }
            }
        };
        mainThread.start();
    }
}
