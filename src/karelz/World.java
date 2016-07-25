package karelz;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@code World} object represents a standard Karel world, containing features of the world, its size, colors,
 * and {@code Robot} objects within it.
 * 
 * @see WorldColorCollection
 * @see Cell
 * @see Robot
 */
public class World {
	
	ConcurrentHashMap<Point,Cell> map;
	ArrayList<Robot> robots;
	int width;
	int height;
	WorldColorCollection colorCollection;
	
	/**
	 * Instantiates a new world.
	 *
	 * @param width the width of the world
	 * @param height the height of the world
	 * @param colorCollection the world colors
	 */
	public World(int width, int height, WorldColorCollection colorCollection) {
		map = new ConcurrentHashMap<Point,Cell>();
		robots = new ArrayList<Robot>();
		this.width = width;
		this.height = height;
		this.colorCollection = colorCollection;
	}
	
	/**
	 * Instantiates a new world with default colors and a default width and height of 20.
	 */
	public World() {
		this(20, 20);
	}
	
	/**
	 * Instantiates a new world with default colors.
	 *
	 * @param width the width of the world
	 * @param height the height of the world
	 */
	public World(int width, int height) {
		this(width, height, WorldColorCollection.getDefaultWorldColorCollection());
	}
	
	/**
	 * Instantiates a new world. This constructor allows setting specific colors without using a {@code WorldColorCollection}.
	 *
	 * @param width the width of the world
	 * @param height the height of the world
	 * @param wallColor the wall color
	 * @param beeperColor the beeper color
	 * @param beeperLabelColor the beeper label color
	 * @param lineColor the line color
	 * @param backgroundColor the background color
	 * @see WorldColorCollection
	 */
	public World(int width, int height, Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		this(width, height, new WorldColorCollection(wallColor, beeperColor, beeperLabelColor, lineColor, backgroundColor));
	}
	
	/**
	 * Instantiates a new world from an existing world object.
	 *
	 * @param world the world to load
	 */
	public World(World world) {
		loadWorld(world);
	}
	
	/**
	 * Instantiates a new world from a file.
	 *
	 * @param path the path to the file
	 */
	public World(String path) {
		loadWorld(path);
	}
	
	/**
	 * Gets the width of the world.
	 *
	 * @return the width of the world
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of the world.
	 *
	 * @return the height of the world
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Saves the world to a file.
	 *
	 * @param path the path to the file to write
	 */
	public void saveWorld(String path) {
		try {
			Files.write(Paths.get(path), getWorldAsStringList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the world to a file.
	 *
	 * @param file the file to write
	 */
	public void saveWorld(File file) {
		try {
			Files.write(Paths.get(file.toURI()), getWorldAsStringList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads an existing world into this one. This will override this world with the new one.
	 *
	 * @param world the world to load
	 */
	public void loadWorld(World world) {
		map = world.map;
		robots = world.robots;
		width = world.width;
		height = world.height;
		colorCollection = world.colorCollection;
	}
	
	/**
	 * Loads a world from a file.
	 *
	 * @param path the path to the world file to load
	 */
	public void loadWorld(String path) {
		try {
			loadWorldAsStringList(Files.readAllLines(Paths.get(path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a world from a file.
	 *
	 * @param file the world file to load
	 */
	public void loadWorld(File file) {
		try {
			loadWorldAsStringList(Files.readAllLines(Paths.get(file.toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a world from a string in the standard world format. This string is loaded as if it was a file.
	 *
	 * @param worldString the world as a string to load
	 */
	public void loadWorldString(String worldString) {
		loadWorldAsStringList(Arrays.asList(worldString.split("\n")));
	}
	
	/**
	 * Load world as a string list in the standard world format. This list is loaded as if it was a file.
	 *
	 * @param list the world as a list of strings to load
	 */
	public void loadWorldAsStringList(List<String> list) {
		//TODO try catch, if any parse-ints fail, reset the world and popup a msgbox that say "invalid world file, some parts may not have loaded"
		//set a flag and if set after loading, have the Window realize that and pop up a msgbox
		
		//completely reset this world. like calling the constructor again
		map = new ConcurrentHashMap<Point,Cell>();
		robots = new ArrayList<Robot>();
		width = 0;
		height = 0;
		colorCollection = WorldColorCollection.getDefaultWorldColorCollection();
		
		if (list.get(0).toLowerCase().startsWith("karelworld")) {//load the old Karel J Robot format. this is only supported in loading, and saving the file will overwrite it in the new format
			list.forEach(line -> {
				//note all positions in the old format are 1-indexed and are listed y,x (called streets, avenues). walls are also on the right and top of cells, instead of the left and bottom
				String[] tokens = line.split(" ");
				switch (tokens[0].toLowerCase()) {
				case "streets":
					height = Integer.parseInt(tokens[1]);
					break;
				case "avenues":
					width = Integer.parseInt(tokens[1]);
					break;
				case "beepers":
					add(Integer.parseInt(tokens[2])-1, Integer.parseInt(tokens[1])-1, Cell.newBeeperPile(Integer.parseInt(tokens[3])));
					break;
				case "eastwestwalls":
					int northOfStreet = Integer.parseInt(tokens[1]);
					int fromAvenue = Integer.parseInt(tokens[2]);
					int toAvenue = Integer.parseInt(tokens[3]);
					for (int i = 0; i < (toAvenue-fromAvenue)+1; i++) {
						add(fromAvenue-1+i, northOfStreet, Cell.newHorizontalWall());
					}
					break;
				case "northsouthwalls":
					int eastOfAvenue = Integer.parseInt(tokens[1]);
					int fromStreet = Integer.parseInt(tokens[2]);
					int toStreet = Integer.parseInt(tokens[3]);
					for (int i = 0; i < (toStreet-fromStreet)+1; i++) {
						add(eastOfAvenue, fromStreet-1+i, Cell.newVerticalWall());
					}
				}
			});
		} else {//load normal world format
			list.forEach(line -> {//TODO this forEach thing might not work for multiline Robot serialized bytecode
				String[] tokens = line.split(" ");
				switch (tokens[0].toLowerCase()) {
				case "world-size":
					width = Integer.parseInt(tokens[1]);
					height = Integer.parseInt(tokens[2]);
					break;
				case "wall-color":
					colorCollection.wallColor = new Color(Integer.parseInt(tokens[1]));
					break;
				case "beeper-color":
					colorCollection.beeperColor = new Color(Integer.parseInt(tokens[1]));
					break;
				case "beeper-label-color":
					colorCollection.beeperLabelColor = new Color(Integer.parseInt(tokens[1]));
					break;
				case "line-color":
					colorCollection.lineColor = new Color(Integer.parseInt(tokens[1]));
					break;
				case "background-color":
					colorCollection.backgroundColor = new Color(Integer.parseInt(tokens[1]));
					break;
				case "beeper-pile":
					add(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Cell.newBeeperPile(Integer.parseInt(tokens[3])));
					break;
				case "horizontal-wall":
					add(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Cell.newHorizontalWall());
					break;
				case "vertical-wall":
					add(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Cell.newVerticalWall());
					break;
				case "block-wall":
					add(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Cell.newBlockWall());
				}//TODO add bot support?
			});
		}
	}
	
	/**
	 * Get the world as a list of strings. This is the same format that is written to files when saving.
	 *
	 * @return the world as a list of strings
	 */
	public List<String> getWorldAsStringList() {
		List<String> lines = new ArrayList<String>();
		
		lines.add("world-size "+width+" "+height);
		lines.add("wall-color "+Integer.toString(colorCollection.wallColor.getRGB()));
		lines.add("beeper-color "+Integer.toString(colorCollection.beeperColor.getRGB()));
		lines.add("beeper-label-color "+Integer.toString(colorCollection.beeperLabelColor.getRGB()));
		lines.add("line-color "+Integer.toString(colorCollection.lineColor.getRGB()));
		lines.add("background-color "+Integer.toString(colorCollection.backgroundColor.getRGB()));
		
		map.forEach((point, cell) -> {
			if (cell.containsValidBeeperPile()) {
				lines.add("beeper-pile "+point.x+" "+point.y+" "+cell.beepers);
			}
			
			if (cell.containsHorizontalWall()) {
				lines.add("horizontal-wall "+point.x+" "+point.y);
			}
			
			if (cell.containsVerticalWall()) {
				lines.add("vertical-wall "+point.x+" "+point.y);
			}
			
			if (cell.containsBlockWall()) {
				lines.add("block-wall "+point.x+" "+point.y);
			}
		});
		
		return lines;
	}
	
	/**
	 * Prints the world as a string. This is the same as {@code getWorldAsStringList().forEach(System.out::println);}
	 * 
	 * @see #getWorldAsStringList()
	 */
	public void printWorld() {
		getWorldAsStringList().forEach(System.out::println);
	}
	
	/**
	 * Adds a cell to the world at a given point.
	 *
	 * @param point the point
	 * @param cell the cell to add
	 * @return this {@code World}
	 */
	public World add(Point point, Cell cell) {
		if (map.containsKey(point)) {
			map.get(point).add(cell);
		} else {
			map.put(point, cell);
		}
		return this;
	}
	
	/**
	 * Adds a cell to the world at a given point.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param cell the cell to add
	 * @return this {@code World}
	 */
	public World add(int x, int y, Cell cell) {
		return add(new Point(x, y), cell);
	}
	
	/**
	 * Removes everything in a cell at a given point.
	 *
	 * @param point the point
	 * @return this {@code World}
	 */
	public World removeAll(Point point) {
		get(point).clear();
		return this;
	}
	
	/**
	 * Removes everything in a cell at a given point.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @return this {@code World}
	 */
	public World removeAll(int x, int y) {
		return removeAll(new Point(x, y));
	}
	
	/**
	 * Removes a specific part of the cell at a given point.
	 *
	 * @param point the point
	 * @param cell a cell containing the elements to remove
	 * @return this {@code World}
	 */
	public World remove(Point point, Cell cell) {
		get(point).remove(cell);
		return this;
	}
	
	/**
	 * Removes a specific part of the cell at a given point.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @param cell a cell containing the elements to remove
	 * @return this {@code World}
	 */
	public World remove(int x, int y, Cell cell) {
		return remove(new Point(x, y), cell);
	}
	
	/**
	 * Gets the cell at a given point.
	 *
	 * @param point the point
	 * @return the cell
	 */
	public Cell get(Point point) {
		return map.getOrDefault(point, new Cell());
	}
	
	/**
	 * Gets the cell at a given point.
	 *
	 * @param x the x position
	 * @param y the y position
	 * @return the cell
	 */
	public Cell get(int x, int y) {
		return get(new Point(x, y));
	}
	
	/**
	 * Adds a robot to the world.
	 *
	 * @param robot the robot to add
	 * @return this {@code World}
	 */
	public World add(Robot robot) {
		robots.add(robot);
		robot.world = this;
		return this;
	}
}
