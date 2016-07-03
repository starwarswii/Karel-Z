package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import karelz.*;

public class TestRobot extends Robot {

	public TestRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}

	public TestRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public TestRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public TestRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public void task() throws EndTaskException {
		turnLeft();
		move();
		turnLeft();
		move();
		turnLeft();
		move();
		turnLeft();
		move();
		iterate((int)(Math.random()*20), this::turnLeft);
		//iterate(100000, this::turnLeft);//for testing delay 0
		turnOff();
	}
	
	public static void main(String[] args) {
		World world = new World(path("test.kzw"));

		world.add(new TestRobot(5, 6, RIGHT).withLogging());
		world.add(new TestRobot(3, 4, UP).withLogging());

		Window window = new Window(world, 100, true, true, 500);
		window.setVisible(true);
		Util.sleep(5000);
		window.loadWorld(new World().add(new TestRobot(3,3, UP)));
		//window.play();
	}
}
