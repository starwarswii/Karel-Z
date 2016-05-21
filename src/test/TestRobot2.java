package test;

import java.awt.Color;
import karelz.*;

public class TestRobot2 extends Robot {

	public TestRobot2(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}

	public TestRobot2(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public TestRobot2(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public TestRobot2(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public void task() {
		while (nextToABeeper()) {
			pickBeeper();
		}
		move();
		while (hasBeepers()) {
			putBeeper();
			move();
			turnLeft();
		}
		turnOff();
	}
}
