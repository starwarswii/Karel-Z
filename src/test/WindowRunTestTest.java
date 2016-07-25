package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import karelz.*;

public class WindowRunTestTest {
	
	public static void main(String[] args) {
		Window.runTests(100, 500, true, true,
			new World().add(new TestRobot(3,3, UP)),
			new World(path("test.kzw")).add(new TestRobot(5,5, UP))
			);
		
		Util.sleep(1000);
		
		Window window = new Window();
		window.setVisible(true);
		Util.sleep(250);
		while(true) {
			window.runTest(new World().add(new TestRobot(Util.random(0,19), Util.random(0,19), Util.randomDirection())));
			Util.sleep(1000);
		}
	}
	
}
