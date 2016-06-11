package test;

import static karelz.Direction.UP;
import static karelz.Util.path;
import karelz.*;

public class Test {

	public static void main(String[] args) {
		
		World world = new World(path("test-world.kzw"));
		
		world.add(new TestRobot2(3, 3, UP));
		
		Window window = new Window(world, 100, true, true);
		window.setVisible(true);
		//Util.sleep(500);
		//window.play();

	}

}
