package karelz;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class World {
	
	HashMap<Point,WorldObject> map;
	ArrayList<Robot> robots;
	int width;
	int height;
	Color wallColor;
	Color beeperColor;
	Color beeperLabelColor;
	Color lineColor;
	Color backgroundColor;

	public World(int width, int height, Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		map = new HashMap<Point,WorldObject>();
		robots = new ArrayList<Robot>();
		this.width = width;
		this.height = height;
		this.wallColor = wallColor;
		this.beeperColor = beeperColor;
		this.beeperLabelColor = beeperLabelColor;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;
	}
	
	public World(int width, int height) {
		this(width, height, Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE);//default color values
	}
	
	public void addObject(WorldObject object, int x, int y) {
		map.put(new Point(x, y), object);
		
	}
	
	public WorldObject getObjectAt(int x, int y) {
		return map.get(new Point(x, y));
	}
	
	public void addRobot(Robot robot) {
		robots.add(robot);
	}
	

}
