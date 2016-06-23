package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import karelz.*;

public class Test {

	public static void main(String[] args) {
		World world = new World(path("test.kzw"));

		world.add(new TestRobot(5, 6, RIGHT).withLogging());
		world.add(new TestRobot(3, 4, UP).withLogging());

		Window window = new Window(world, 100, true, true, 500);
		window.setVisible(true);
	}
}
