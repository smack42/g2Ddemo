package infect.g2Ddemo;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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

    protected static void createAndShowGUI() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice dev = env.getDefaultScreenDevice();
        int displayHz = dev.getDisplayMode().getRefreshRate();
        if (displayHz == DisplayMode.REFRESH_RATE_UNKNOWN) {
            displayHz = 60;
        }

        JFrame jf = new JFrame();
        jf.setTitle("INFECT g2Ddemo");
        jf.setResizable(false);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jf.add(new SineFlag(displayHz));

        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

}
