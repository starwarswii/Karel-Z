package karelz;

import java.awt.Color;
import java.awt.image.BufferedImage;

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

	//this contains the robot's program
	public abstract void task() throws EndTaskException;

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

	void step() {
		stepCount++;
		log("stepped, facing "+direction.toString().toLowerCase()+" with "+(beepers == Cell.INFINITY ? "infinity" : beepers)+(beepers == 1 ? " beeper" : " beepers"));
		thread.interrupt();
	}

	void waitForStep() {
		while (threadIsActive) {
			try {
				Thread.sleep(30000);// 30 seconds
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	void log(Object message) {
		if (logging) {
			System.out.println("A "+getClass().getSimpleName()+" at ("+x+", "+y+") has "+message);
		}
	}

	void crash(Object message) throws EndTaskException {
		state = RobotState.ERROR;
		threadIsActive = false;
		log("crashed: "+message);
		throw new EndTaskException();
	}

	BufferedImage getCurrentImage() {
		return collection.getImage(direction, state);
	}

	//a utility method to allow "new SomeRobot(whatever).withLogging()"
	public Robot withLogging() {
		logging = true;
		return this;
	}

	public void setLogging(boolean value) {
		logging = value;
	}

	public boolean getLogging() {
		return logging;
	}

	public long getStepCount() {
		return stepCount;
	}

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

	public boolean hasBeepers() {
		return beepers > 0 || beepers == Cell.INFINITY;
	}

	public boolean nextToABeeper() {
		return world.get(x, y).containsValidBeeperPile();
	}

	public boolean nextToARobot() {//java 8 is awesome
		return world.robots.stream().filter(robot -> x == robot.x && y == robot.y).count() >= 2;
	}

	public boolean facingUp() {
		return direction == Direction.UP;
	}

	public boolean facingRight() {
		return direction == Direction.RIGHT;
	}

	public boolean facingDown() {
		return direction == Direction.DOWN;
	}

	public boolean facingLeft() {
		return direction == Direction.LEFT;
	}

	public boolean running() {//used for "while (running())" instead of "while (true)"
		return state == RobotState.ON;
	}

	public boolean frontIsBlocked() {return !frontIsClear();}
	public boolean doesntHaveBeepers() {return !hasBeepers();}
	public boolean notNextToABeeper() {return !nextToABeeper();}
	public boolean notNextToARobot() {return !nextToARobot();}
	public boolean notFacingUp() {return !facingUp();}
	public boolean notFacingRight() {return !facingRight();}
	public boolean notFacingDown() {return !facingDown();}
	public boolean notFacingLeft() {return !facingLeft();}
	public boolean notRunning() {return !running();}

	public void iterate(int times, CodeBlock code) throws EndTaskException {
		for (int i = 0; i < times && threadIsActive; i++) {
			code.execute();
		}
	}

	public void turnOff() throws EndTaskException {
		if (state == RobotState.ON) {
			state = RobotState.OFF;
			threadIsActive = false;
			log("turned off");
			throw new EndTaskException();
		}
	}

	public void sleep() {
		if (state == RobotState.ON) {
			waitForStep();
		}
	}

	public void move() throws EndTaskException {
		if (state == RobotState.ON) {
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

	public void turnLeft() {
		if (state == RobotState.ON) {
			direction = direction.getCounterclockwiseDirection();
			waitForStep();
		}
	}

	public void putBeeper() throws EndTaskException {
		if (state == RobotState.ON) {
			if (hasBeepers() && beepers != Cell.INFINITY) {
				beepers--;
				world.add(x, y, Cell.newBeeperPile());
			} else {
				crash("Tried to put beeper when it didn't have one");
			}
			waitForStep();
		}
	}

	public void pickBeeper() throws EndTaskException {
		if (state == RobotState.ON) {
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
