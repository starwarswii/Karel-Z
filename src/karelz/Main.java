package karelz;

//mostly for testing. the stuff run in this main would mostly be run inside whatever bot project you were doing.
public class Main {

	public static void main(String[] args) {
		
		World world = new World(20, 20);
		world.addObject(new BeeperPile(2), 3, 3);
		world.addObject(new BeeperPile(BeeperPile.ININITY), 8, 8);
		world.addObject(new Wall(Walls.BLOCK), 0, 9);
		
		Window window = new Window(world);
		window.setVisible(true);
		
		//then start world
	}

}
