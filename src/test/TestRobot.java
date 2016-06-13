package test;

import java.awt.Color;
import karelz.*;

public class TestRobot extends Robot {

	public TestRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}

	public TestRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public TestRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public TestRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public void task() throws EndTaskException {
		turnLeft();
		move();
		turnLeft();
		move();
		turnLeft();
		move();
		turnLeft();
		move();
		iterate(6, this::turnLeft);
		//iterate(100000, this::turnLeft);//for testing delay 0
		turnOff();
	}
}
