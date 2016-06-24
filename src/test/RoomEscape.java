package test;

import static karelz.Direction.*;
import static karelz.Util.path;
import karelz.*;

public class RoomEscape {

	public static void main(String[] args) {

		World world = new World(path("room.kzw"));

		world.add(new RoomEscapeRobot(7, 10, RIGHT).withLogging());
		world.add(new RoomEscapeRobot(17, 10, RIGHT).withLogging());
		world.add(new RoomEscapeRobot(7, 12, UP).withLogging());

		Window window = new Window(world, 100, true, true);
		window.setVisible(true);

	}
}
