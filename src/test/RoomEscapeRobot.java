package test;

import java.awt.Color;
import karelz.Direction;
import karelz.EndTaskException;
import karelz.SuperRobot;

public class RoomEscapeRobot extends SuperRobot {

	public RoomEscapeRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public RoomEscapeRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public RoomEscapeRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public RoomEscapeRobot(int x, int y, Direction direction, int beepers, Color color) {
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
		while (true) {
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
}
