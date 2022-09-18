package infect.g2Ddemo;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Util {

    /** load image from the specified resource **/
    public static BufferedImage loadImageFile(String fileName) {
        ImageIcon icon = new ImageIcon(Util.class.getClassLoader().getResource(fileName));
        Image tmpImage = icon.getImage();
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();
        return image;
    }

}
