package karelz;

import java.awt.Color;

public abstract class SuperRobot extends Robot {

	public SuperRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public SuperRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public SuperRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public SuperRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}

	SuperRobot(int x, int y, Direction direction, int beepers, Color color, World world) {
		super(x, y, direction, beepers, color, world);
	}

	public void turnRight() {
		if (state == RobotState.ON) {
			direction = direction.getClockwiseDirection();
			waitForStep();
		}
	}

	public void teleportTo(int x, int y) throws EndTaskException {
		if (state == RobotState.ON) {
			if (x < 0 || y < 0) {
				crash("Tried to teleport out of bounds");
			}
			if (world.get(x, y).containsBlockWall()) {
				crash("Tried to teleport into a wall at ("+x+", "+y+")");
			}
			this.x = x;
			this.y = y;
			waitForStep();
		}
	}
}
