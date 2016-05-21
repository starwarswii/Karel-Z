package karelz;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Util {

	static final String RESOURCE_PATH = "resources/";
	
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(Util.class.getClassLoader().getResource(RESOURCE_PATH+filename));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ImageIcon getIcon(String filename) {
		return new ImageIcon(getImage(filename));
	}
	
	public static Cursor createCursor(BufferedImage image, String name) {
		return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth()/2,image.getHeight()/2), name);
	}
	
	public static Cursor createCursor(String filename, String name) {
		return createCursor(getImage(filename), name);
	}
	
	//rotates clockwise
	public static BufferedImage getRotatedImage(BufferedImage image, int degrees) {
		return new AffineTransformOp(AffineTransform.getRotateInstance(Math.toRadians(degrees), (double)(image.getWidth()/2), (double)(image.getHeight()/2)), AffineTransformOp.TYPE_BILINEAR).filter(image, null);
	}
	
	public static Font sizeFontToFit(Graphics2D g, Font font, String string, int width, int height) {
		int minSize = 0;
		int maxSize = 288;
		int curSize = font.getSize();

		while (maxSize - minSize > 2) {
			FontMetrics fm = g.getFontMetrics(new Font(font.getName(), font.getStyle(), curSize));
			int fontWidth = fm.stringWidth(string);
			int fontHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();

			if ((fontWidth > width) || (fontHeight > height)) {
				maxSize = curSize;
				curSize = (maxSize + minSize) / 2;
			} else {
				minSize = curSize;
				curSize = (minSize + maxSize) / 2;
			}
		}

		return new Font(font.getName(), font.getStyle(), curSize);
	}
	
	public static void sleep(long milliseconds) {
		try {Thread.sleep(milliseconds);} catch (Exception e) {}
	}
}
