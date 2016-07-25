package test;

import static karelz.Direction.RIGHT;
import static karelz.Util.path;

import java.awt.Color;
import karelz.*;

public class BeeperLayerSortRobot extends Robot {
	
	int columnCount;
	
	public BeeperLayerSortRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public BeeperLayerSortRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public BeeperLayerSortRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public BeeperLayerSortRobot(int x, int y, Direction direction, int beepers, Color color) {
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
		int distance = 1;
		while (true) {
			goToWall();//go to the wall, and start again
			
			turnRight();//move up x levels
			for (int i = 0; i < distance; i++) {
				move();
			}
			turnRight();
			
			distance++;
			
			for (int i = 0; i < columnCount; i++) {//get all the beepers, going to the end of the row
				getBeeper();
				move();
			}
			if (doesntHaveBeepers()) {//hit top of columns
				return;//done
			}
			turnRight();//back down to bottom row
			goToWall();
			turnRight();
			while (hasBeepers()) {//place beepers in layers right to left till run out
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
		Window window = Window.runTests(10, new World(path("beeper-bead-layer-sort.kzw")).add(new BeeperLayerSortRobot(0, 0, RIGHT)));
		
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
		return world.add(new BeeperLayerSortRobot(0, 0, RIGHT));
	}
	
}
