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
	static final String WORLD_PATH = "src/worlds/";

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
		int currentSize = font.getSize();

		while (maxSize-minSize > 2) {
			FontMetrics metrics = g.getFontMetrics(new Font(font.getName(), font.getStyle(), currentSize));
			int fontWidth = metrics.stringWidth(string);
			int fontHeight = metrics.getLeading()+metrics.getMaxAscent()+metrics.getMaxDescent();

			if ((fontWidth > width) || (fontHeight > height)) {
				maxSize = currentSize;
				currentSize = (maxSize+minSize)/2;
			} else {
				minSize = currentSize;
				currentSize = (minSize+maxSize)/2;
			}
		}

		return new Font(font.getName(), font.getStyle(), currentSize);
	}

	public static void sleep(long milliseconds) {
		try {Thread.sleep(milliseconds);} catch (Exception e) {}
	}

	public static String path(String name) {
		return WORLD_PATH+name;
	}
	
	public static int random(int min, int max) {
		return min+(int)(Math.random()*((max-min)+1));
	}
	
	public static Direction randomDirection() {
		return Direction.values()[(int)(Math.random()*4)];
	}
}
