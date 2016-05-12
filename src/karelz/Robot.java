package karelz;

import java.awt.Color;
import java.awt.image.BufferedImage;

//TODO improve image of crashed robot, make X more noticeable
public abstract class Robot implements RobotTask {
	int x;
	int y;
	Direction direction;
	int beepers;
	RobotImageCollection collection;
	RobotState state;
	World world;

	Thread thread;
	volatile boolean threadIsActive;

	protected Robot(int x, int y, Direction direction) {
		this(x, y, direction, 0, null, null);
	}

	protected Robot(int x, int y, Direction direction, int beepers) {
		this(x, y, direction, beepers, null, null);
	}

	protected Robot(int x, int y, Direction direction, Color color) {
		this(x, y, direction, 0, color, null);
	}

	protected Robot(int x, int y, Direction direction, int beepers, Color color) {
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
	}


	//this contains the robot's program
	public abstract void task();

	void launchThread() {
		threadIsActive = true;

		thread = new Thread(() -> {
			waitForTick();
			task();
			threadIsActive = false;
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

	void crash(String message) {
		state = RobotState.ERROR;
		threadIsActive = false;
		System.out.println("A robot crashed at ("+x+", "+y+"): "+message);
	}

	BufferedImage getCurrentImage() {
		return collection.getImage(direction, state);
	}

	
	protected boolean frontIsClear() {
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

	protected boolean hasBeepers() {
		return beepers > 0 || beepers == Cell.INFINITY;
	}
	
	protected boolean nextToABeeper() {
		return world.get(x, y).containsValidBeeperPile();
	}
	
	protected boolean nextToARobot() {//java 8 is awesome
		return world.robots.stream().filter(a -> a.x == x && a.y == y).count() >= 2;
	}
	
	protected boolean facingUp() {
		return direction == Direction.UP;
	}
	
	protected boolean facingRight() {
		return direction == Direction.RIGHT;
	}
	
	protected boolean facingDown() {
		return direction == Direction.DOWN;
	}
	
	protected boolean facingLeft() {
		return direction == Direction.LEFT;
	}
	
	protected boolean frontIsBlocked() {return !frontIsClear();}
	protected boolean doesntHaveBeepers() {return !hasBeepers();}
	protected boolean notNextToABeeper() {return !nextToABeeper();}
	protected boolean notNextToARobot() {return !nextToARobot();}
	protected boolean notFacingUp() {return !facingUp();}
	protected boolean notFacingRight() {return !facingRight();}
	protected boolean notFacingDown() {return !facingDown();}
	protected boolean notFacingLeft() {return !facingLeft();}
	
	protected void iterate(CodeBlock code, int times) {
		for (int i = 0; i < times && threadIsActive; i++) {
			code.execute();
		}
	}

	protected void turnOn() {
		if (state != RobotState.ERROR) {
			state = RobotState.ON;
		}
	}

	protected void turnOff() {
		if (state != RobotState.ERROR) {
			state = RobotState.OFF;
		}
	}
	
	protected void sleep() {
		if (state != RobotState.ERROR) {
			waitForTick();
		}
	}

	protected void move() {
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

	protected void turnLeft() {
		if (state == RobotState.ON) {
			direction = direction.getCounterclockwiseDirection();
			waitForTick();
		}
	}

	protected void putBeeper() {
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
	
	protected void pickBeeper() {
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
