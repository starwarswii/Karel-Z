package karelz;

import java.awt.Color;

public class WorldColorCollection {
	Color wallColor;
	Color beeperColor;
	Color beeperLabelColor;
	Color lineColor;
	Color backgroundColor;

	public WorldColorCollection(Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		this.wallColor = wallColor;
		this.beeperColor = beeperColor;
		this.beeperLabelColor = beeperLabelColor;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;
	}

	public static WorldColorCollection getDefaultWorldColorCollection() {//default color values
		return new WorldColorCollection(Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE);
	}
}
