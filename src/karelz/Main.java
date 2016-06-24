package karelz;

public class Main {//this class will run if you double click on the compiled jar. It just opens the world editor

	public static void main(String[] args) {
		Window window = new Window(args.length > 0 ? new World(args[0]) : new World(), true);
		window.setVisible(true);
	}
}
