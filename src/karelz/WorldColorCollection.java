package karelz;

import java.awt.Color;

/**
 * {@code WorldColorCollections} act as container objects to hold the 5 {@code Color} objects that color different parts of a world.
 * These 5 colors are:
 * <ul>
 * <li>{@code wallColor}, which colors horizontal, vertical, and block walls
 * <li>{@code beeperColor}, which colors the actual circle of the beeper
 * <li>{@code beeperLabelColor}, which colors the numbers displayed on the beepers
 * <li>{@code lineColor}, which colors the horizontal and vertical world grid lines
 * <li>{@code backgroundColor}, which colors the background of the world
 * </ul>
 * These colors can be individually set within a {@code WorldColorCollection} and then that object can be used to construct a world.
 */
public class WorldColorCollection {
	Color wallColor;
	Color beeperColor;
	Color beeperLabelColor;
	Color lineColor;
	Color backgroundColor;
	
	/**
	 * Instantiates a new world color collection.
	 *
	 * @param wallColor the wall color
	 * @param beeperColor the beeper color
	 * @param beeperLabelColor the beeper label color
	 * @param lineColor the line color
	 * @param backgroundColor the background color
	 * @see WorldColorCollection
	 */
	public WorldColorCollection(Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		this.wallColor = wallColor;
		this.beeperColor = beeperColor;
		this.beeperLabelColor = beeperLabelColor;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * Constructs and returns a new {@code WorldColorCollection} with the default color values.
	 * Because this is a new object, the colors in it can be modified without worry of changing the default color values.
	 * <p>The default color values are below.
	 * <table border=1>
	 * <tr><th>Color Name				</th><th>Default Value</th></tr>
	 * <tr><td>{@code wallColor}		</td><td>{@link Color#BLACK}</td></tr>
	 * <tr><td>{@code beeperColor}		</td><td>{@link Color#BLACK}</td></tr>
	 * <tr><td>{@code beeperLabelColor}	</td><td>{@link Color#WHITE}</td></tr>
	 * <tr><td>{@code lineColor}		</td><td>{@link Color#BLACK}</td></tr>
	 * <tr><td>{@code backgroundColor}	</td><td>{@link Color#WHITE}</td></tr>
	 * </table>
	 *
	 * @return a new {@code WorldColorCollection} with the default color values
	 */
	public static WorldColorCollection getDefaultWorldColorCollection() {//default color values
		return new WorldColorCollection(Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE);
	}
}
