package karelz;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Robot {
	int x;
	int y;
	Direction direction;
	int beepers;
	RobotImageCollection collection;
	RobotState state;
	World world;
	
	public Robot(int x, int y, Direction direction) {
		this(x, y, direction, 0, null, null);
	}
	
	public Robot(int x, int y, Direction direction, int beepers) {
		this(x, y, direction, beepers, null, null);
	}
	
	public Robot(int x, int y, Direction direction, Color color) {
		this(x, y, direction, 0, color, null);
	}
	
	public Robot(int x, int y, Direction direction, int beepers, Color color) {
		this(x, y, direction, beepers, color, null);
	}
	
	public Robot(int x, int y, Direction direction, int beepers, Color color, World world) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.beepers = beepers;
		collection = new RobotImageCollection(color);
		state = RobotState.ON;
		this.world = world;
	}
	
	
	public BufferedImage getCurrentImage() {
		return collection.getImage(direction, state);
	}
	
	public void iterate(CodeBlock code, int times) {
		for (int i = 0; i < times; i++) {
			code.execute();
		}
	}
	
	//public boolean 
	
	public void turnLeft() {
		
	}

}
