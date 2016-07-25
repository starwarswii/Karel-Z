package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;

public class BeeperColumnSortRobot extends Robot {
	
	boolean done;
	int rowCount;
	
	public BeeperColumnSortRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public BeeperColumnSortRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public BeeperColumnSortRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public BeeperColumnSortRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	public void turnRight() {// Turns right
		for (int i = 0; i < 3; i++) {
			turnLeft();
		} // Turns left three times
	}
	
	public void scanBottomRow() throws EndTaskException {// counts the beepers in the bottom row
		while (nextToABeeper()) {// as long as there are beepers under the robot
			rowCount++;// increment up a variable
			move();// move to next space
		}
	}
	
	public void turnAround() {// turn around
		for (int i = 0; i < 2; i++) {
			turnLeft();
		} // turn left twice
	}
	
	public void goToWall() throws EndTaskException {// go to the wall
		while (frontIsClear()) {
			move();
		} // as long as the front is clear, move forward
	}
	
	public void getBeeper() throws EndTaskException {// pick up beepers under the robot
		if (nextToABeeper()) {
			pickBeeper();
		} // if there is a beeper under the robot, pick it up
	}
	
	public void sortBeepers() throws EndTaskException {// go up a level, pick up beepers, and place them from right to left
		// move up a level
		turnRight();
		move();
		turnRight();
		
		for (int i = 0; i < rowCount; i++) {// get all the beepers, going to the end of the row
			getBeeper();
			move();
		}
		if (!hasBeepers()) {
			turnOff();
			done = true;
			return;
		} // if there are no beepers on the row, shut off
		turnAround();
		while (hasBeepers()) {// place beepers on each space until there are no more in the bag
			move();
			putBeeper();
		}
		goToWall();// go to the wall, and start again
	}
	
	public void task() throws EndTaskException {
		done = false;
		rowCount = 0;
		scanBottomRow();// count the bottom row of beepers
		turnAround();// turn around
		goToWall();// keep going forward until there is a wall
		while (!done) {
			sortBeepers();// go up a level, pick up beepers, and place them from right to left
		}
	}
	
	public static void main(String[] args) {
		Window window = Window.runTests(10, new World(path("beeper-column-sort.kzw")).add(new BeeperColumnSortRobot(0, 0, RIGHT)));
		
		window.setDelay(5);
		
		while (true) {
			window.runTest(500, generateWorld());
		}
	}
	
	public static World generateWorld() {
		World world = new World(20, 20, Color.BLACK, Color.GRAY, Color.WHITE, Color.DARK_GRAY, Color.WHITE);
		
		int columns = Util.random(1, 20);
		for (int i = 0; i < columns; i++) {
			int columnHeight = Util.random(1, 20);
			for (int j = 0; j < columnHeight; j++) {
				world.add(i, j, Cell.newBeeperPile());
			}
		}
		return world.add(new BeeperColumnSortRobot(0, 0, RIGHT));
	}
}
