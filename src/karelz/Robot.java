package karelz;

import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class Robot implements RobotTask {
	int x;
	int y;
	Direction direction;
	int beepers;
	RobotImageCollection collection;
	RobotState state;
	World world;
	boolean logging;

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
	}

	//this contains the robot's program
	public abstract void task();

	void launchThread() {
		threadIsActive = true;

		thread = new Thread(() -> {
			waitForTick();
			task();
			threadIsActive = false;
			if (state != RobotState.ERROR) {
				log("finished its task");	
			}
		});
		thread.start();
	}

	void step() {
		thread.interrupt();
	}

	void waitForTick() {
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
			System.out.println("A robot at ("+x+", "+y+") has "+message);
		}
	}

	void crash(Object message) {
		state = RobotState.ERROR;
		threadIsActive = false;
		log("crashed: "+message);
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

	public boolean frontIsBlocked() {return !frontIsClear();}
	public boolean doesntHaveBeepers() {return !hasBeepers();}
	public boolean notNextToABeeper() {return !nextToABeeper();}
	public boolean notNextToARobot() {return !nextToARobot();}
	public boolean notFacingUp() {return !facingUp();}
	public boolean notFacingRight() {return !facingRight();}
	public boolean notFacingDown() {return !facingDown();}
	public boolean notFacingLeft() {return !facingLeft();}

	public void iterate(int times, CodeBlock code) {
		for (int i = 0; i < times && threadIsActive; i++) {
			code.execute();
		}
	}

	public void turnOn() {
		if (state != RobotState.ERROR) {
			state = RobotState.ON;
			log("turned on");
		}
	}

	public void turnOff() {
		if (state != RobotState.ERROR) {
			state = RobotState.OFF;
			log("turned off");
		}
	}

	public void sleep() {
		if (state != RobotState.ERROR) {
			waitForTick();
		}
	}

	public void move() {
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
			waitForTick();
		}
	}

	public void turnLeft() {
		if (state == RobotState.ON) {
			direction = direction.getCounterclockwiseDirection();
			waitForTick();
		}
	}

	public void putBeeper() {
		if (state == RobotState.ON) {
			if (hasBeepers() && beepers != Cell.INFINITY) {
				beepers--;
				world.add(x, y, Cell.newBeeperPile(1));
			} else {
				crash("Tried to put beeper when it didn't have one");
			}
			waitForTick();
		}
	}

	public void pickBeeper() {
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
			waitForTick();	
		}
	}

}
