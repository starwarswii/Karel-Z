package karelz;

//mostly for testing. the stuff run in this main would mostly be run inside whatever bot project you were doing.
public class Main {

	public static void main(String[] args) {
		
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
		
		world.add(13, 6, Cell.newBlockWall());
		world.add(13, 5, Cell.newBeeperPile(1337));
		
		world.add(12, 3, Cell.newBeeperPile(2));
		world.add(13, 3, Cell.newBeeperPile(12));
		world.add(14, 3, Cell.newBeeperPile(123));
		world.add(15, 3, Cell.newBeeperPile(1234));
		world.add(16, 3, Cell.newBeeperPile(12345));
		world.add(17, 3, Cell.newBeeperPile(1234567890));
		
		Window window = new Window(world);
		window.setVisible(true);
		
		//then start world
	}

}
