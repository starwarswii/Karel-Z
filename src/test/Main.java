package test;

import java.awt.Color;
import karelz.*;

public class Main {

	//static final String worldPath = "worlds/";

	public static void main(String[] args) {
		//System.out.println(Direction.UP.getClockwiseDirection().getClockwiseDirection().getClockwiseDirection().getClockwiseDirection());
		//System.out.println(Direction.UP.getCounterclockwiseDirection().getCounterclockwiseDirection().getCounterclockwiseDirection().getCounterclockwiseDirection());
		//System.out.println(RobotImage.getRobotImage(Direction.UP, RobotState.ON));
		//System.out.println(RobotImage.getRobotImage(Direction.LEFT, RobotState.ERROR));

		World world = new World(20, 20);
		world.add(3, 3, Cell.newBeeperPile(2));
		world.add(8, 8, Cell.newBeeperPile(Cell.INFINITY));

		world.add(0, 9, Cell.newBlockWall());
		world.add(1, 10, Cell.newHorizontalWall());
		world.add(2, 11, Cell.newVerticalWall());

		world.add(12, 12, Cell.newHorizontalWall().add(Cell.newVerticalWall()));
		world.add(12, 13, Cell.newHorizontalWall());
		world.add(13, 12, Cell.newVerticalWall());

		world.add(12, 5, Cell.newBeeperPile(20).add(Cell.newBeeperPile(8).add(Cell.newVerticalWall())));
		world.add(12, 5, Cell.newBeeperPile(20));
		world.add(12, 5, Cell.newBeeperPile(8));
		world.add(12, 5, Cell.newVerticalWall());

		world.add(13, 6, Cell.newBlockWall());
		world.add(13, 5, Cell.newBeeperPile(1337));

		world.add(12, 3, Cell.newBeeperPile(2));
		world.add(13, 3, Cell.newBeeperPile(12));
		world.add(14, 3, Cell.newBeeperPile(123));
		world.add(15, 3, Cell.newBeeperPile(1234));
		world.add(16, 3, Cell.newBeeperPile(12345));
		world.add(17, 3, Cell.newBeeperPile(1234567890));

		//world.add(new TestRobot(1, 1, Direction.UP));
		//world.add(new TestRobot(2, 1, Direction.RIGHT));
		//world.add(new TestRobot(3, 1, Direction.DOWN));
		world.add(new TestRobot(4, 1, Direction.LEFT));

		//world.add(new TestRobot(1, 0, Direction.UP, Color.RED));
		world.add(new TestRobot(2, 0, Direction.RIGHT, Color.ORANGE));
		//world.add(new TestRobot(3, 0, Direction.DOWN, Color.GREEN));
		//world.add(new TestRobot(4, 0, Direction.LEFT, Color.BLUE));

		world.add(new TestRobot2(3, 3, Direction.UP));

		//world.printWorld();

		//World world = new World(10, 10);
		//world = World.loadSaveFile(null);

		//world.add(new TestRobot(1, 1, Direction.UP, 0).withLogging());
		
		Window window = new Window(world, 100, true);
		window.setVisible(true);
		//Util.sleep(500);
		//window.start();

	}

}
