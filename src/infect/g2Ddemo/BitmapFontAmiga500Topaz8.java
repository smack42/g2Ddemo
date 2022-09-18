package infect.g2Ddemo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class BitmapFontAmiga500Topaz8 implements BitmapFont {

    public static final String RESOURCE_LOCATION = "resource/Font_Amiga500_Topaz8_white.png";

    public final boolean isDoubleWidth, isDoubleHeight;
    public final int charWidth, charHeight;
    public final Color color;
    private final BufferedImage[] images;


    /** create the Amiga 500 font Topaz 8, in original size 8x8 pixels per character and in white color. **/
    public BitmapFontAmiga500Topaz8() {
        this(false, false, Color.WHITE);
    }


    /** create the Amiga 500 font Topaz 8
     * @param isDoubleWidth false = original width 8 pixels per character, true = double width 16 pixels.
     * @param isDoubleHeight false = original height 8 pixels per character, true = double height 16 pixels.
     * @param color of the characters
    **/
    public BitmapFontAmiga500Topaz8(boolean isDoubleWidth, boolean isDoubleHeight, Color color) {
        this.isDoubleWidth = isDoubleWidth;
        this.charWidth = isDoubleWidth ? 16 : 8;
        this.isDoubleHeight = isDoubleHeight;
        this.charHeight = isDoubleHeight ? 16 : 8;
        this.color = color;
        this.images = createImages();
    }


    private BufferedImage[] createImages() {
        // image contains 16 columns and 6 rows, each char is 8*8 pixels
        BufferedImage imgFontAmigaTopaz8White = Util.loadImageFile(RESOURCE_LOCATION);
        BufferedImage[] result = new BufferedImage[16 * 6];
        for (int i = 0;  i < result.length;  ++i) {
            Image img1 = imgFontAmigaTopaz8White.getSubimage(
                    (i & (16 - 1)) * 8,
                    (i / 16) * 8,
                    8, 8 );
            FilteredImageSource filterColor = new FilteredImageSource(img1.getSource(), new RGBImageFilter() {
                @Override
                public int filterRGB(int x, int y, int rgb) {
                    return rgb == Color.WHITE.getRGB()  ?  color.getRGB()  :  rgb;
                }
            });
            Image img2 = Toolkit.getDefaultToolkit().createImage(filterColor);
            BufferedImage bi = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img2, 0, 0, charWidth, charHeight, null);
            g.dispose();
            result[i] = bi;
        }
        return result;
    }


    @Override
    public void drawString(Graphics g, char c, int x, int y) {
        int index = Math.max(c - ' ', 0);
        if (index >= images.length) {
            index = '?' - ' ';
        }
        g.drawImage(images[index], x, y, null);
    }


    @Override
    public void drawString(Graphics g, String str, int x, int y) {
        for (int i = 0;  i < str.length();  ++i) {
            drawString(g, str.charAt(i), x, y);
            x += charWidth;
        }
    }

}
