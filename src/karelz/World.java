package karelz;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class World {
	
	HashMap<Point,Cell> map;
	ArrayList<Robot> robots;
	int width;
	int height;
	Color wallColor;
	Color beeperColor;
	Color beeperLabelColor;
	Color lineColor;
	Color backgroundColor;

	public World(int width, int height, Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		map = new HashMap<Point,Cell>();
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
	
	public void add(int x, int y, Cell cell) {
		if (map.containsKey(new Point(x, y))) {
			map.get(new Point(x, y)).add(cell);
		} else {
			map.put(new Point(x, y), cell);
		}
		
	}
	
	public void addRobot(Robot robot) {
		robots.add(robot);
	}
	

}
