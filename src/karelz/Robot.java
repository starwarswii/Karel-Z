package karelz;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * The {@code Robot} object is the base of all Karel robots.
 * It contains the basic methods for movement, turning, and checking {@code true}/{@code false} conditions,
 * as well as the ability to launch another thread used to run the robots.
 * 
 * @see SuperRobot
 */
public abstract class Robot {
	int x;
	int y;
	Direction direction;
	int beepers;
	RobotImageCollection collection;
	RobotState state;
	World world;
	boolean logging;
	long stepCount;
	
	Thread thread;
	volatile boolean threadIsActive;
	
	/**
	 * Instantiates a new robot with 0 beepers, no colored badge, and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 */
	public Robot(int x, int y, Direction direction) {
		this(x, y, direction, 0, null, null);
	}
	
	/**
	 * Instantiates a new robot with no colored badge and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 */
	public Robot(int x, int y, Direction direction, int beepers) {
		this(x, y, direction, beepers, null, null);
	}
	
	/**
	 * Instantiates a new robot with 0 beepers and no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param color the badge color, which can be null
	 */
	public Robot(int x, int y, Direction direction, Color color) {
		this(x, y, direction, 0, color, null);
	}
	
	/**
	 * Instantiates a new robot with no world.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 * @param color the badge color, which can be null
	 */
	public Robot(int x, int y, Direction direction, int beepers, Color color) {
		this(x, y, direction, beepers, color, null);
	}
	
	/**
	 * Instantiates a new robot.
	 *
	 * @param x the x position in the world
	 * @param y the y position in the world
	 * @param direction the direction it's facing
	 * @param beepers the number of beepers it has
	 * @param color the badge color, which can be null
	 * @param world the world it is in
	 */
	Robot(int x, int y, Direction direction, int beepers, Color color, World world) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.beepers = beepers;
		collection = new RobotImageCollection(color);
		state = RobotState.ON;
		this.world = world;
		logging = false;
		stepCount = 0;
	}
	
	/**
	 * Runs the robot's program. This method will be overwritten in subclasses of {@code Robot} with the program code.
	 *
	 * @throws EndTaskException when the robot task is terminated either by a crash or the program ending
	 */
	public abstract void task() throws EndTaskException;
	
	/**
	 * Launches the robot task thread. The thread will run the first step once {@link #step()} is called.
	 */
	void launchThread() {
		threadIsActive = true;
		
		thread = new Thread(() -> {
			waitForStep();
			try {
				task();
			} catch (EndTaskException e) {}
			threadIsActive = false;
			if (state != RobotState.ERROR) {
				log("finished its task in "+stepCount+(stepCount == 1 ? " step" : " steps"));	
			}
		});
		thread.start();
	}
	
	/**
	 * Steps forward one step in the running robot task thread, then pauses and waits using {@link #waitForStep()}.
	 */
	void step() {
		if (running()) {
			stepCount++;
			log("stepped, facing "+direction.toString().toLowerCase()+" with "+(beepers == Cell.INFINITY ? "infinity" : beepers)+(beepers == 1 ? " beeper" : " beepers"));
			thread.interrupt();
		}
	}
	
	/**
	 * Waits on the robot task thread for {@link #step()} to be called, then continues.
	 */
	void waitForStep() {
		while (threadIsActive) {
			try {
				Thread.sleep(30000);// 30 seconds
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	/**
	 * Prints a message to the console if logging is enabled on this {@code Robot}.
	 * The message is in the format: {@code A <RobotClassName> at (<x>, <y>) has <message>}
	 *
	 * @param message the message to log, starting with a past tense verb (e.g. "crashed into a wall", "stopped moving", "turned left")
	 * 
	 * @see #setLogging(boolean)
	 * @see #withLogging()
	 * @see #getLogging()
	 */
	void log(Object message) {
		if (logging) {
			System.out.println("A "+getClass().getSimpleName()+" at ("+x+", "+y+") has "+message);
		}
	}
	
	/**
	 * Crashes the robot, stops the robot task thread, prints a message to console if logging is enabled, and throws an {@code EndTaskException}.
	 *
	 * @param message the message to log, starting with a capitalized past tense verb
	 * (e.g. "Hit a wall", "Drove off the map", "Tried to do something but failed")
	 * @throws EndTaskException always
	 */
	void crash(Object message) throws EndTaskException {
		state = RobotState.ERROR;
		threadIsActive = false;
		log("crashed: "+message);
		throw new EndTaskException();
	}
	
	/**
	 * Gets the current image used to display this robot this includes the direction, state, and badge color.
	 *
	 * @return the current image
	 */
	BufferedImage getCurrentImage() {
		return collection.getImage(direction, state);
	}
	
	/**
	 * Turns logging on and returns this {@code Robot}.
	 * This is a utility method to allow easy robot construction with logging using: {@code new SomeRobot(...).withLogging()}
	 *
	 * @return this {@code Robot}
	 */
	public Robot withLogging() {
		logging = true;
		return this;
	}
	
	/**
	 * Sets the logging value.
	 *
	 * @param value the new value
	 */
	public void setLogging(boolean value) {
		logging = value;
	}
	
	/**
	 * Gets the logging. value.
	 *
	 * @return the logging value
	 */
	public boolean getLogging() {
		return logging;
	}
	
	/**
	 * Gets the step count.
	 *
	 * @return the step count
	 */
	public long getStepCount() {
		return stepCount;
	}
	
	/**
	 * Returns {@code true} if the space in the front of this robot is clear and it could move forward without hitting a wall.
	 *
	 * @return {@code true} if the space in the front of this robot is clear
	 * @see #frontIsBlocked()
	 */
	public boolean frontIsClear() {
		Cell currentCell = world.get(x, y);
		
		if ((currentCell.containsHorizontalWall() && direction == Direction.DOWN) || (currentCell.containsVerticalWall() && direction == Direction.LEFT)) {
			return false;
		}
		
		int nextX = x;
		int nextY = y;
		
		switch (direction) {
		case UP:
			nextY++;
			break;
		case RIGHT:
			nextX++;
			break;
		case DOWN:
			nextY--;
			break;
		case LEFT:
			nextX--;
		}
		
		if (nextX < 0 || nextY < 0) {
			return false;
		}
		
		Cell nextCell = world.get(nextX, nextY);
		
		if ((nextCell.containsHorizontalWall() && direction == Direction.UP) || (nextCell.containsVerticalWall() && direction == Direction.RIGHT) || nextCell.containsBlockWall()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns {@code true} if this robot has more than 0 beepers.
	 *
	 * @return {@code true} if this robot has more than 0 beepers
	 * @see #doesntHaveBeepers()
	 */
	public boolean hasBeepers() {
		return beepers > 0 || beepers == Cell.INFINITY;
	}
	
	/**
	 * Returns {@code true} if this robot is next to a beeper in its current world.
	 *
	 * @return {@code true} if this robot is next to a beeper in its current world
	 * @see #notNextToABeeper()
	 */
	public boolean nextToABeeper() {
		return world.get(x, y).containsValidBeeperPile();
	}
	
	/**
	 * Returns {@code true} if this robot is next to another robot in its current world.
	 *
	 * @return {@code true} if this robot is next to another robot in its current world
	 * @see Robot#notNextToARobot()
	 */
	public boolean nextToARobot() {//java 8 is awesome
		return world.robots.stream().filter(robot -> x == robot.x && y == robot.y).count() >= 2;
	}
	
	/**
	 * Returns {@code true} if this robot is facing up.
	 *
	 * @return {@code true}, if this robot is facing up
	 * @see #notFacingUp()
	 */
	public boolean facingUp() {
		return direction == Direction.UP;
	}
	
	/**
	 * Returns {@code true} if this robot is facing right.
	 *
	 * @return {@code true}, if this robot is facing right
	 * @see #notFacingRight()
	 */
	public boolean facingRight() {
		return direction == Direction.RIGHT;
	}
	
	/**
	 * Returns {@code true} if this robot is facing down.
	 *
	 * @return {@code true}, if this robot is facing down
	 * @see #notFacingDown()
	 */
	public boolean facingDown() {
		return direction == Direction.DOWN;
	}
	
	/**
	 * Returns {@code true} if this robot is facing left.
	 *
	 * @return {@code true}, if this robot is facing left
	 * @see #notFacingLeft()
	 */
	public boolean facingLeft() {
		return direction == Direction.LEFT;
	}
	
	/**
	 * Returns true if this robot is running, meaning it has the "on" state.
	 * This is always true within {@link #task()}, and can be used as a replacement for {@code while(true)} with {@code while(running()}
	 *
	 * @return {@code true}, if this robot is running
	 */
	public boolean running() {
		return state == RobotState.ON;
	}
	
	/**
	 * Returns {@code !frontIsClear()}
	 *
	 * @return {@code !frontIsClear()}
	 * @see #frontIsClear()
	 */
	public boolean frontIsBlocked() {return !frontIsClear();}
	
	/**
	 * Returns {@code !hasBeeepers()}
	 *
	 * @return {@code !hasBeeepers()}
	 * @see #hasBeepers()
	 */
	public boolean doesntHaveBeepers() {return !hasBeepers();}
	
	/**
	 * Returns {@code !nextToABeeper()}
	 *
	 * @return {@code !nextToABeeper()}
	 * @see #nextToABeeper()
	 */
	public boolean notNextToABeeper() {return !nextToABeeper();}
	
	/**
	 * Returns {@code !nextToARobot()}
	 *
	 * @return {@code !nextToARobot()}
	 * @see #nextToARobot()
	 */
	public boolean notNextToARobot() {return !nextToARobot();}
	
	/**
	 * Returns {@code !facingUp()}
	 *
	 * @return {@code !facingUp()}
	 * @see #facingUp()
	 */
	public boolean notFacingUp() {return !facingUp();}
	
	/**
	 * Returns {@code !facingRight()}
	 *
	 * @return {@code !facingRight()}
	 * @see #facingRight()
	 */
	public boolean notFacingRight() {return !facingRight();}
	
	/**
	 * Returns {@code !facingDown()}
	 *
	 * @return {@code !facingDown()}
	 * @see #facingDown()
	 */
	public boolean notFacingDown() {return !facingDown();}
	
	/**
	 * Returns {@code !facingLeft()}
	 *
	 * @return {@code !facingLeft()}
	 * @see #facingLeft()
	 */
	public boolean notFacingLeft() {return !facingLeft();}
	
	/**
	 * Returns {@code !running()}
	 *
	 * @return {@code !running()}
	 * @see #running()
	 */
	public boolean notRunning() {return !running();}
	
	/**
	 * Runs the code in the given {@code CodeBlock} a given number of times.
	 * This can be used as a replacement for a for loop within {@link #task()}
	 *
	 * @param times the number of times to run the code
	 * @param code the code to run
	 * @throws EndTaskException when the robot task is terminated either by a crash or the program ending
	 */
	public void iterate(int times, CodeBlock code) throws EndTaskException {
		for (int i = 0; i < times && running(); i++) {
			code.execute();
		}
	}
	
	/**
	 * Turns off the robot, stops the robot task thread, prints a message to console if logging is enabled, and throws an {@code EndTaskException}.
	 *
	 * @throws EndTaskException always
	 */
	public void turnOff() throws EndTaskException {
		if (running()) {
			state = RobotState.OFF;
			threadIsActive = false;
			log("turned off");
			throw new EndTaskException();
		}
	}
	
	/**
	 * Sleeps for one step, doing nothing until {@link #step()} is called again.
	 */
	public void sleep() {
		if (running()) {
			waitForStep();
		}
	}
	
	/**
	 * Attempts to move the robot forward one cell.
	 * If the robot would have hit a wall, a crash is caused and an {@code EndTaskException} is thrown.
	 *
	 * @throws EndTaskException if the robot would have crashed into a wall
	 */
	public void move() throws EndTaskException {
		if (running()) {
			if (frontIsClear()) {
				switch (direction) {
				case UP:
					y++;
					break;
				case RIGHT:
					x++;
					break;
				case DOWN:
					y--;
					break;
				case LEFT:
					x--;
				}
			} else {
				crash("Hit a wall");
			}
			waitForStep();
		}
	}
	
	/**
	 * Turns the robot 90 degrees counterclockwise.
	 */
	public void turnLeft() {
		if (running()) {
			direction = direction.getCounterclockwiseDirection();
			waitForStep();
		}
	}
	
	/**
	 * Attempts to put down 1 beeper onto the world cell the robot is currently in.
	 * If the robot has no more beepers, a crash is caused and an {@code EndTaskException} is thrown.
	 *
	 * @throws EndTaskException if the robot has no more beepers
	 */
	public void putBeeper() throws EndTaskException {
		if (running()) {
			if (hasBeepers() && beepers != Cell.INFINITY) {
				beepers--;
				world.add(x, y, Cell.newBeeperPile());
			} else {
				crash("Tried to put beeper when it didn't have one");
			}
			waitForStep();
		}
	}
	
	/**
	 * Attempts to pick up 1 beeper from the world cell the robot is currently in.
	 * If there are no beepers on this cell, a crash is caused and an {@code EndTaskException} is thrown.
	 * 
	 * @throws EndTaskException if there are no beepers on this cell
	 */
	public void pickBeeper() throws EndTaskException {
		if (running()) {
			Cell currentCell = world.get(x, y);
			if (currentCell.containsValidBeeperPile()) {
				if (currentCell.beepers != Cell.INFINITY) {
					currentCell.beepers--;	
				}
				currentCell.clearBeeperPileIfEmpty();
				if (beepers != Cell.INFINITY) {
					beepers++;
				}
			} else {
				crash("Tried to pick beeper when there was none");
			}
			waitForStep();	
		}
	}
}
