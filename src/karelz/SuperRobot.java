package karelz;

import java.awt.Color;

public abstract class SuperRobot extends Robot {

	protected SuperRobot(int x, int y, Direction direction) {super(x, y, direction);}
	
	protected SuperRobot(int x, int y, Direction direction, int beepers) {super(x, y, direction, beepers);}
	
	protected SuperRobot(int x, int y, Direction direction, Color color) {super(x, y, direction, color);}
	
	protected SuperRobot(int x, int y, Direction direction, int beepers, Color color) {super(x, y, direction, beepers, color);}

	SuperRobot(int x, int y, Direction direction, int beepers, Color color, World world) {super(x, y, direction, beepers, color, world);}
	
	protected void turnRight() {
		if (state == RobotState.ON) {
			direction = direction.getClockwiseDirection();
			waitForTick();
		}
	}
	
}
