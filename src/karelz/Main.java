package karelz;

public class Main {//this class will run if you double click on the compiled jar. It just opens the world editor

	public static void main(String[] args) {
		World world;

		if (args.length > 0) {
			world = new World(args[0]);
		} else {
			world = new World(20, 20);
		}

		Window window = new Window(world, true);
		window.setVisible(true);
	}
}
