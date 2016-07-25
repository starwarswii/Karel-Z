package karelz;

/**
 * This class contains {@link Main#main(String[])}, which will run when this compiled jar is double-clicked. It just opens the world editor.
 * 
 * @see #main(String[])
 */
public class Main {
	
	/**
	 * The main method, which will run when this compiled jar is double-clicked. It opens the world editor.
	 * <br>If there is more than one argument, it will attempt to parse the first argument as a path to a world file and open that in the editor.
	 * Otherwise, it will open the editor with a blank world.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Window window = new Window(args.length > 0 ? new World(args[0]) : new World(), true);
		window.setVisible(true);
	}
}
