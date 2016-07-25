package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;


public class RoomEscapeRobot extends Robot {
	
	boolean frontBlocked;
	boolean rightBlocked;
	boolean done;
	
	public RoomEscapeRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public RoomEscapeRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public RoomEscapeRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public RoomEscapeRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	public void turnRight() throws EndTaskException {// Turns right
		iterate(3, () -> {
			turnLeft();
		}); // Turns left three times
	}
	
	public void faceFirstWall() throws EndTaskException {// Check for initial surrounding walls
		iterate(4, () -> {// Turn left until facing a wall or done a full circle
			if (frontIsClear()) {// If there isn't a wall, turn left
				turnLeft();
			} else {
				return;
			} // If there is, stop turning left and exit the method
		});
	}
	
	public void checkSides() throws EndTaskException {
		frontBlocked = frontIsBlocked();
		turnRight();
		rightBlocked = frontIsBlocked();
		turnLeft();
	}
	
	public void followWall() throws EndTaskException {// Follows along a wall until it finds a door, and then exits it.
		checkSides();
		if (!rightBlocked) {// If the wall you are following suddenly disappears, then there is a door there
			// Turn and go through the door, then shut off
			turnRight();
			move();
			turnOff();
		}
		if (!frontBlocked) {
			move();// If there isn't a wall in front, go forward
		} else {
			turnLeft();// If there is a wall in front, turn left
		}
	}
	
	public void checkOutsideRoom() throws EndTaskException {// Checks to see if it has passed through a hole in a wall, indicating a door
		// Going into position to check if outside
		turnLeft();
		move();
		if (frontIsBlocked()) {// If there is a wall here (in the middle of the check), stop checking and face it
			frontBlocked = true;
			return;
		}
		turnLeft();
		if (frontIsBlocked()) {
			turnOff();
		}// If there is a wall here, shut off because the robot is outside the room
		// No wall, returning to starting point
		turnLeft();
		move();
		turnLeft();
		move();
		// Reset variables
		frontBlocked = false;
		rightBlocked = false;
	}
	
	public void findAndFollowWall() throws EndTaskException {// Finds a wall, or the door, and then follows/exits it
		checkSides();
		if (!frontBlocked) {// Keep checking if the robot is outside each step until it hits a wall
			checkOutsideRoom();
		} else {
			// Follows wall to exit
			turnLeft();
			while (running()) {// Repeat until the robot is off
				followWall();
			}
		}
	}
	
	public void task() throws EndTaskException {
		frontBlocked = false;
		rightBlocked = false;
		faceFirstWall();
		while (running()) {
			findAndFollowWall();
		}
	}
	
	public static void main(String[] args) {
		Window window = Window.runTests(50,
			new World(path("room.kzw")).add(new RoomEscapeRobot(9, 2, UP)),
			new World(path("room-corner-up.kzw")).add(new RoomEscapeRobot(9, 2, UP)),
			new World(path("room-corner-down.kzw")).add(new RoomEscapeRobot(9, 2, UP)),
			new World(path("room.kzw")).add(new RoomEscapeRobot(10, 13, RIGHT)),
			new World(path("room.kzw")).add(new RoomEscapeRobot(8, 8, RIGHT)),
			new World(path("room-small.kzw")).add(new RoomEscapeRobot(10, 8, RIGHT)),
			new World(path("room-hall.kzw")).add(new RoomEscapeRobot(10, 7, LEFT))
			);
		
		window.setDelay(10);
		
		while (true) {
			window.runTest(500, new World(path("room.kzw")).add(new RoomEscapeRobot(Util.random(3, 15), Util.random(2, 14), Util.randomDirection())));
		}
	}
}
