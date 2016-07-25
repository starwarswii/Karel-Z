package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;


public class BeeperBeadSortRobot extends Robot {
	
	int columnCount;
	
	public BeeperBeadSortRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public BeeperBeadSortRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public BeeperBeadSortRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public BeeperBeadSortRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	public void turnRight() {
		turnLeft();
		turnLeft();
		turnLeft();
	}
	
	public void turnAround() {
		turnLeft();
		turnLeft();
	}
	
	public void goToWall() throws EndTaskException {
		while (frontIsClear()) {
			move();
		}
	}
	
	public void getBeeper() throws EndTaskException {//prevents trying to pick beeper when there isn't one
		if (nextToABeeper()) {
			pickBeeper();
		}
	}
	
	
	public void sortBeepers() throws EndTaskException {
		while (nextToABeeper()) {//while there still are beeper piles
			columnCount++;
			
			while (nextToABeeper()) {//while current beeper pile still has beepers
				pickBeeper();
			}
			turnLeft();
			while (hasBeepers()) {//expand column
				putBeeper();
				move();
			}
			turnAround();
			goToWall();
			turnLeft();
			move();
		}
		turnAround();
		while (true) {
			goToWall();//go to the wall, and start again
			
			turnRight();//move up a level
			move();
			turnRight();
			
			for (int i = 0; i < columnCount; i++) {//get all the beepers, going to the end of the row
				getBeeper();
				move();
			}
			if (doesntHaveBeepers()) {//hit top of columns
				turnAround();
				goToWall();
				turnLeft();
				goToWall();
				turnLeft();
				
				while (nextToABeeper()) {//collapsing columns
					turnLeft();
					while (nextToABeeper()) {//collect a column of beepers
						pickBeeper();
						move();
					}
					turnAround();
					goToWall();
					turnLeft();
					while (hasBeepers()) {//place them all on bottom row
						putBeeper();
					}
					move();
				}
				return;//done
				
			}
			turnAround();
			while (hasBeepers()) {//place beepers in line right to left till run out
				move();
				putBeeper();
			}
		}
	}
	
	public void task() throws EndTaskException {
		columnCount = 0;
		sortBeepers();
		turnOff();
	}
	
	public static void main(String[] args) {
		Window window = Window.runTests(10, new World(path("beeper-bead-layer-sort.kzw")).add(new BeeperBeadSortRobot(0, 0, RIGHT)));
		
		window.setDelay(5);
		
		while (true) {
			window.runTest(500, generateWorld());
		}
	}
	
	public static World generateWorld() {
		World world = new World(20, 20, Color.BLACK, Color.GRAY, Color.WHITE, Color.DARK_GRAY, Color.WHITE);
		
		int columns = Util.random(1, 20);
		for (int i = 0; i < columns; i++) {
			world.add(i, 0, Cell.newBeeperPile(Util.random(1, 20)));
		}
		return world.add(new BeeperBeadSortRobot(0, 0, RIGHT));
	}
	
}
