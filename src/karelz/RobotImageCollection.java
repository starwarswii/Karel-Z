package karelz;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * a {@code RobotImageCollection} is used in conjunction with {@code RobotImage} to pre-load and pre-generate
 * all the possible images of a Karel robot based on the directions and states, as well as the robot's badge color.
 * 
 * @see RobotImage
 */
public class RobotImageCollection {
	
	static final String FILENAME_ON = "karel-on.png";
	static final String FILENAME_OFF = "karel-off.png";
	static final String FILENAME_ERROR = "karel-error.png";
	
	static final int BADGE_X = 101;
	static final int BADGE_Y = 134;
	static final int BADGE_SIZE = 51;
	
	BufferedImage[] images;
	
	/**
	 * Instantiates a new robot image collection with no colored badge.
	 */
	public RobotImageCollection() {
		this(null);
	}
	
	/**
	 * Instantiates a new robot image collection.
	 *
	 * @param color the badge color. Use {@code null} for no badge
	 */
	public RobotImageCollection(Color color) {
		images = new BufferedImage[12];
		
		BufferedImage on = Util.getImage(FILENAME_ON);
		BufferedImage off = Util.getImage(FILENAME_OFF);
		BufferedImage error = Util.getImage(FILENAME_ERROR);
		
		if (color != null) {
			Graphics2D g = on.createGraphics();
			g.setColor(color);
			g.fillRect(BADGE_X, BADGE_Y, BADGE_SIZE, BADGE_SIZE);
			
			g = off.createGraphics();
			g.setColor(color);
			g.fillRect(BADGE_X, BADGE_Y, BADGE_SIZE, BADGE_SIZE);
			
			g = error.createGraphics();
			g.setColor(color);
			g.fillRect(BADGE_X, BADGE_Y, BADGE_SIZE, BADGE_SIZE);
		}
		
		images[0] = on;
		images[1] = Util.getRotatedImage(on, 90);
		images[2] = Util.getRotatedImage(on, 180);
		images[3] = Util.getRotatedImage(on, 270);
		
		images[4] = off;
		images[5] = Util.getRotatedImage(off, 90);
		images[6] = Util.getRotatedImage(off, 180);
		images[7] = Util.getRotatedImage(off, 270);
		
		images[8] = error;
		images[9] = Util.getRotatedImage(error, 90);
		images[10] = Util.getRotatedImage(error, 180);
		images[11] = Util.getRotatedImage(error, 270);
		
	}
	
	/**
	 * Gets the matching image given a {@code RobotImage}.
	 *
	 * @param image the {@code RobotImage} to get the matching image for
	 * @return the matching image
	 */
	public BufferedImage getImage(RobotImage image) {
		return images[image.ordinal()];
	}
	
	/**
	 * Gets the matching image given a direction and a robot state.
	 *
	 * @param direction the direction
	 * @param state the state
	 * @return the matching image
	 */
	public BufferedImage getImage(Direction direction, RobotState state) {
		return getImage(RobotImage.getRobotImage(direction, state));
	}
}
