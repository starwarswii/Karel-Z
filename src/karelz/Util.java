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

/**
 * This class serves as an utility class, with miscellaneous useful functions that are used by the other classes.
 */
public class Util {
	
	static final String RESOURCE_PATH = "resources/";
	static final String WORLD_PATH = "src/worlds/";
	
	/**
	 * Reads and returns a {@code BufferedImage} from the resource folder given the filename.
	 *
	 * @param filename the filename of the image to load
	 * @return the {@code BufferedImage}
	 */
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(Util.class.getClassLoader().getResource(RESOURCE_PATH+filename));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads and returns an {@code ImageIcon} from the resource folder given the filename.
	 *
	 * @param filename the filename of the image to load
	 * @return the {@code ImageIcon}
	 */
	public static ImageIcon getIcon(String filename) {
		return new ImageIcon(getImage(filename));
	}
	
	/**
	 * Creates and returns a custom cursor given a {@code BufferedImage} and a cursor name.
	 *
	 * @param image the image
	 * @param name the name of the cursor
	 * @return the cursor
	 */
	public static Cursor createCursor(BufferedImage image, String name) {
		return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth()/2,image.getHeight()/2), name);
	}
	
	/**
	 * Creates and returns a custom cursor given the filename of a {@code BufferedImage} in the resource folder and a cursor name.
	 *
	 * @param filename of the cursor image
	 * @param name the name of the cursor
	 * @return the cursor
	 */
	public static Cursor createCursor(String filename, String name) {
		return createCursor(getImage(filename), name);
	}
	
	/**
	 * Returns a copy of the given image rotated a number of degrees clockwise.
	 *
	 * @param image the image to create a rotated copy of
	 * @param degrees the number of degrees to rotate clockwise
	 * @return the rotated copy of the image
	 */
	public static BufferedImage getRotatedImage(BufferedImage image, int degrees) {
		return new AffineTransformOp(AffineTransform.getRotateInstance(Math.toRadians(degrees), (double)(image.getWidth()/2), (double)(image.getHeight()/2)), AffineTransformOp.TYPE_BILINEAR).filter(image, null);
	}
	
	/**
	 * Determines the maximum size a given string rendered in a given font can be
	 * while fitting within given dimensions using a binary search,
	 * then returns that new sized font.
	 *
	 * @param g the graphics object the font will be drawn with
	 * @param font the font to size
	 * @param string the string to be rendered in the font
	 * @param width the maximum width in pixels the sized font must fit within
	 * @param height the maximum height in pixels the sized font must fit within
	 * @return the new sized font
	 */
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
	
	/**
	 * Sleeps for a given number of milliseconds. This method is provided as an exception/try-catch free sleep method, and is the same as
	 * <pre><code>
	 * try {
	 * 	Thread.sleep(milliseconds);
	 * } catch (Exception e) {
	 * 	//do nothing
	 * }
	 * </code></pre>
	 *
	 * @param milliseconds the number of milliseconds to sleep
	 */
	public static void sleep(long milliseconds) {
		try {Thread.sleep(milliseconds);} catch (Exception e) {}
	}
	
	/**
	 * Returns the path to a world file in the default world folder given the name of the file.
	 *
	 * @param name the name of the file
	 * @return the path to the file
	 */
	public static String path(String name) {
		return WORLD_PATH+name;
	}
	
	/**
	 * Returns a random int from {@code min} to {@code max}, inclusive.
	 *
	 * @param min the minimum random value
	 * @param max the maximum random value
	 * @return a random int from {@code min} to {@code max}, inclusive.
	 */
	public static int random(int min, int max) {
		return min+(int)(Math.random()*((max-min)+1));
	}
	
	/**
	 * Returns a random {@code Direction}.
	 *
	 * @return a random {@code Direction}
	 */
	public static Direction randomDirection() {
		return Direction.values()[(int)(Math.random()*4)];
	}
}
