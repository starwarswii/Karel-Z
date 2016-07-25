package karelz;

import java.awt.Color;

/**
 * The {@code SuperRobot} object is an improvement on {@code Robot}, with a few more advanced  and convenience methods.
 * These include turning right, turning around, teleporting to any spot, and giving access to the {@code World} object the robot is currently in.
 * 
 * @see Robot
 */
public abstract class SuperRobot extends Robot {
	
	/**
	 * Instantiates a new super robot with 0 beepers, no colored badge, and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 */
	public SuperRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	/**
	 * Instantiates a new super robot with no colored badge and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 */
	public SuperRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	/**
	 * Instantiates a new super robot with 0 beepers and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param color the badge color, which can be null
	 */
	public SuperRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	/**
	 * Instantiates a new super robot with no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 * @param color the badge color, which can be null
	 */
	public SuperRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	/**
	 * Instantiates a new super robot.
	 *
	 * @param x the x position in the world
	 * @param y the y position in the world
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 * @param color the badge color, which can be null
	 * @param world the world it is in
	 */
	SuperRobot(int x, int y, Direction direction, int beepers, Color color, World world) {
		super(x, y, direction, beepers, color, world);
	}
	
	/**
	 * Turns the robot 90 degrees clockwise.
	 */
	public void turnRight() {
		if (running()) {
			direction = direction.getClockwiseDirection();
			waitForStep();
		}
	}
	
	/**
	 * Turns the robot 180 degrees.
	 */
	public void turnAround() {
		if (running()) {
			direction = direction.getOppositeDirection();
			waitForStep();
		}
	}
	
	/**
	 * Attempts to teleport to a given x and y position in the world.
	 * If that position contains a wall or is out of bounds, a crash is caused and an {@code EndTaskException} is thrown.
	 *
	 * @param x the x position to teleport to
	 * @param y the y position to teleport to
	 * @throws EndTaskException when the teleport destination is out of bounds or contains a wall
	 */
	public void teleportTo(int x, int y) throws EndTaskException {
		if (running()) {
			if (x < 0 || y < 0) {
				crash("Tried to teleport out of bounds to ("+x+", "+y+")");
			}
			if (world.get(x, y).containsBlockWall()) {
				crash("Tried to teleport into a wall at ("+x+", "+y+")");
			}
			this.x = x;
			this.y = y;
			waitForStep();
		}
	}
	
	/**
	 * Crashes the robot, stops the robot task thread, prints a message to console if logging is enabled, and throws an {@code EndTaskException}.
	 * This is useful when a user wants a robot to terminate with an error state because something went wrong.
	 *
	 * @param message the message to log, starting with a capitalized past tense verb
	 * (e.g. "Hit a wall", "Drove off the map", "Tried to do something but failed")
	 * @throws EndTaskException always
	 */
	public void forceCrash(Object message) throws EndTaskException {
		state = RobotState.ERROR;
		threadIsActive = false;
		log("force crashed: "+message);
		throw new EndTaskException();
	}
	
	/**
	 * Gets the {@code World} object this robot is currently in.
	 *
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}
}
