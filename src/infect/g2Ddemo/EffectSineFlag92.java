package infect.g2Ddemo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

/**
 * this is a fairly accurate remake of Marley's 1992 bootblock.<p>
 * featuring<br>
 * - font "Topaz 8" from Amiga 500 Kickstart 1.3<br>
 * - authentic color palette<br>
 * - authentic wave motion pattern<p>
 * based on the disassembled code of the original program.
 */
public class EffectSineFlag92 extends Effect {

    public EffectSineFlag92(long frameDurationNs) {
        super(frameDurationNs);
        textBuf = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
        atMain = new AffineTransform();
        atMain.scale(2.0, 2.0); // increase size
        atMain.translate(2.0, 14.0);
        atMirror = new AffineTransform();
        atMirror.scale(2.0, -0.5); // flip vertically and reduce height
        atMirror.translate(2.0, HEIGHT * -2.0);
        precalcValues = precalc();
        imgFontAmigaTopaz8Glyphs = makeFont();
    }

    private final String theText = // must be 16 rows of 32 chars each
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
    private final Color colGrd1 = new Color(0x44, 0x11, 0x44); // gradient dark pink -> dark blue
    private final Color colGrd2 = new Color(0x33, 0x11, 0x44); // gradient dark pink -> dark blue
    private final Color colGrd3 = new Color(0x22, 0x11, 0x44); // gradient dark pink -> dark blue
    private final Color colGrd4 = new Color(0x11, 0x11, 0x44); // gradient dark pink -> dark blue
    private final Color colBack = new Color(0x00, 0x11, 0x44); // dark blue
    private final Color colText = new Color(0xcc, 0x22, 0x44); // red
    private final Color colMirrorBack = new Color(0x11, 0x22, 0x66); // blue
    private final Color colMirrorText = new Color(0x66, 0x22, 0x66); // pink
    private final int precalcValuesPerFrame = 16 * 32 * 2; // rows * columns * 2
    private final short[] precalcValues;
    private final BufferedImage[] imgFontAmigaTopaz8Glyphs;
    private final AffineTransform atMain;
    private final AffineTransform atMirror;
    private final BufferedImage textBuf;
    private int animFrame = 0; // 0...63

    @Override
    public String getName() {
        return "Sine Flag 92";
    }


    @Override
    public void drawEffect() {
        Graphics2D g = buf.createGraphics();

        // gradient at top of screen
        g.setColor(colGrd1); g.fillRect(0,  0, WIDTH, 8);
        g.setColor(colGrd2); g.fillRect(0,  8, WIDTH, 8);
        g.setColor(colGrd3); g.fillRect(0, 16, WIDTH, 8);
        g.setColor(colGrd4); g.fillRect(0, 24, WIDTH, 8);
        g.setColor(colBack); g.fillRect(0, 32, WIDTH, 384 - 32);
        g.setColor(colMirrorBack); g.fillRect(0, 384, WIDTH, HEIGHT - 384);

        // text in animated sine-wave flag
        Graphics2D gText = textBuf.createGraphics();
        gText.setComposite(AlphaComposite.Clear);
        gText.fillRect(0, 0, textBuf.getWidth(), textBuf.getHeight());
        gText.setComposite(AlphaComposite.SrcOver);
        final int centerPixelOffset = 100 * 512 + 160;
        for (int in = animFrame * precalcValuesPerFrame, inEnd = in + precalcValuesPerFrame, ch = 0;  in < inEnd;  ) {
            int d0 = precalcValues[in++];  // address offset, from center of bitplane
            int d1 = precalcValues[in++];  // BLTCON0 = highest 4 bits are horizontal pixel shift
            final char c = theText.charAt(ch++);
            if (c != ' ') {
                int pixelOffset = (d0 & -2) * 8;
                pixelOffset += ((d1 >> 12) & 0xf);
                pixelOffset += centerPixelOffset;
                int x = (pixelOffset & (512 - 1));
                int y = (pixelOffset / 512);
                gText.drawImage(getGlyph(c), x, y, null);
            }
        }
        g.drawImage(textBuf, atMain, null);
        animFrame = ((animFrame + 1) & 0x3f); // 0...63

        // mirror at bottom of screen
        g.drawImage(filterImageRGB(textBuf, colText, colMirrorText), atMirror, null);

        g.dispose();
    }


    /** directly based on the disassembled code of Marley's 1992 bootblock "infect3" */
    private short[] precalc() {
        //precalc1
        final short[] vOne = new short[512]; // 64 * 8
        for (int i = 63, offset = 0;  i >= 0;  --i, offset += 2) {
            int d0 = (i ^ 63);
            d0 *= d0;
            int d1 = d0 * 195;
            d1 /= 0xc000;
            d1 -= 79;
            d1 *= d0;
            d1 /= 0x0800;
            d1 += 128;
            short s = (short)d1;
            vOne[offset] = s;
            vOne[offset + 0x102/2] = s;
            vOne[(512 - offset) & 511] = s; // prevent ArrayIndexOutOfBoundsException
            vOne[512 - offset - 0x2fe/2] = s;
            s = (short)(-s);
            vOne[offset + 0x200/2] = s;
            vOne[offset + 0x302/2] = s;
            vOne[512 - offset - 0x200/2] = s;
            vOne[512 - offset - 0x0fe/2] = s;
        }
        
        //precalc2
        final short[] vTwo = new short[64 * precalcValuesPerFrame];
        for (int frame = 0, out = 0;  frame < 64;  ++frame) {
            int d1 = -80;
            for (int row = 0;  row < 16;  ++row) {
                int d0 = -128;
                for (int column = 0;  column < 32;  ++column) {
                    int d3 = column * 24 + row * 32 + frame * 16;
                    d3 &= 0x03fc;
                    d3 = vOne[d3 >> 1];
                    d3 += 512;
                    int d4 = d0 * d3;
                    d4 <<= 7;
                    d4 >>= 16;
                    d3 *= d1;
                    d3 >>= 3;
                    d3 &= 0xffc0;
                    d3 += d4 >> 3;
                    vTwo[out++] = (short)d3;
                    d4 &= 0xf;
                    d4 <<= 12;
                    d4 |= 0x0dfc;
                    vTwo[out++] = (short)d4;
                    d0 += 8;
                }
                d1 += 9;
            }
        }
        
        //debug output
//        final short[] values = vOne;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PrintStream ps = new PrintStream(baos);
//        for (int i = 0;  i < values.length;  ) {
//            ps.printf("%04X ", Short.valueOf(values[i++]));
//            if ((i & (16 - 1)) == 0) {
//                ps.println();
//            }
//        }
//        System.out.print(baos.toString());
//        try (FileOutputStream fos = new FileOutputStream("dump.bin")) {
//            for (short s : values) {
//                fos.write(0xff & (s >> 8));
//                fos.write(0xff & s);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        return vTwo;
    }


    private BufferedImage[] makeFont() {
        // image contains 16 columns and 6 rows, each char is 8*8 pixels
        BufferedImage imgFontAmigaTopaz8White = loadImageFile("resource/Font_Amiga500_Topaz8_white.png");
        BufferedImage[] result = new BufferedImage[16 * 6];
        for (int i = 0;  i < result.length;  ++i) {
            Image img1 = imgFontAmigaTopaz8White.getSubimage(
                    (i & (16 - 1)) * 8,
                    (i / 16) * 8,
                    8, 8 );
            FilteredImageSource filterColor = new FilteredImageSource(img1.getSource(), new RGBImageFilter() {
                @Override
                public int filterRGB(int x, int y, int rgb) {
                    return rgb == Color.WHITE.getRGB()  ?  colText.getRGB()  :  rgb;
                }
            });
            Image img2 = Toolkit.getDefaultToolkit().createImage(filterColor);
            BufferedImage bi = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(img2, 0, 0, null);
            g.dispose();
            result[i] = bi;
        }
        return result;
    }


    private Image getGlyph(char c) {
        int index = c - ' ';
        index = Math.max(index, 0);
        index = Math.min(index, imgFontAmigaTopaz8Glyphs.length - 1);
        return imgFontAmigaTopaz8Glyphs[index];
    }


    private Image filterImageRGB(Image srcImage, final Color oldColor, final Color newColor) {
        FilteredImageSource src = new FilteredImageSource(srcImage.getSource(), new RGBImageFilter() {
            final int oldRGB = oldColor.getRGB();
            final int newRGB = newColor.getRGB();
            @Override
            public int filterRGB(int x, int y, int rgb) {
                return rgb == oldRGB  ?  newRGB  :  rgb;
            }
        });
        return Toolkit.getDefaultToolkit().createImage(src);
    }
}
