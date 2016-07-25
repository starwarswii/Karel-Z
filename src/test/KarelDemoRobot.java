package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;

public class KarelDemoRobot extends Robot {
	
	public KarelDemoRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}
	
	public KarelDemoRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}
	
	public KarelDemoRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}
	
	public KarelDemoRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}
	
	public void task() throws EndTaskException {
		move();
		move();
		turnLeft();
		move();
		move();
		pickBeeper();
		turnLeft();
		move();
		turnLeft();
		move();
		move();
		turnLeft();
		move();
		move();
		turnLeft();
		move();
		move();
		move();
		turnLeft();
		move();
		move();
		move();
		pickBeeper();
		turnOff();
	}
	
	public static void main(String[] args) {
		Window.runTests(new World(path("karel-demo.kzw")).add(new KarelDemoRobot(1, 0, RIGHT, 3).withLogging()));
	}
	
}
