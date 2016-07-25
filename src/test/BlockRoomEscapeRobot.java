package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;

public class BlockRoomEscapeRobot extends SuperRobot {
	
	public BlockRoomEscapeRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public BlockRoomEscapeRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public BlockRoomEscapeRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public BlockRoomEscapeRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	public void task() throws EndTaskException {
		while (frontIsClear()) {//find a wall or the exit
			turnLeft();
			if (frontIsBlocked()) {
				turnRight();
				turnRight();
				if (frontIsBlocked()) {
					turnLeft();
					move();
					turnOff();
				}
			} else {
				turnRight();
			}
			move();
		}
		turnLeft();
		while (running()) {
			while (frontIsClear()) {
				turnRight();
				if (frontIsClear()) {
					move();
					move();
					turnOff();
				} else {
					turnLeft();
				}
				move();
			}
			turnLeft();
		}
	}
	
	public static void main(String[] args) {
		
		World world = new World(path("block-room.kzw"));
		
		world.add(new BlockRoomEscapeRobot(7, 10, RIGHT).withLogging());
		world.add(new BlockRoomEscapeRobot(17, 10, RIGHT).withLogging());
		world.add(new BlockRoomEscapeRobot(7, 12, UP).withLogging());
		
		Window window = new Window(world, 100, false, true, 500);
		window.setVisible(true);
	}
}
