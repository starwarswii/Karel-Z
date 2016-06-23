package karelz;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class World {

	HashMap<Point,Cell> map;
	ArrayList<Robot> robots;
	int width;
	int height;
	WorldColorCollection colorCollection;

	public World(int width, int height, WorldColorCollection colorCollection) {
		map = new HashMap<Point,Cell>();
		robots = new ArrayList<Robot>();
		this.width = width;
		this.height = height;
		this.colorCollection = colorCollection;
	}

	public World(int width, int height) {
		this(width, height, WorldColorCollection.getDefaultWorldColorCollection());
	}

	public World(int width, int height, Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		this(width, height, new WorldColorCollection(wallColor, beeperColor, beeperLabelColor, lineColor, backgroundColor));
	}

	public World(World world) {
		loadWorld(world);
	}

	public World(String path) {
		loadWorld(path);
	}

	public void saveWorld(String path) {
		try {
			Files.write(Paths.get(path), getWorldAsStringList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveWorld(File file) {
		try {
			Files.write(Paths.get(file.toURI()), getWorldAsStringList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadWorld(World world) {
		map = world.map;
		robots = world.robots;
		width = world.width;
		height = world.height;
		colorCollection = world.colorCollection;
	}

	public void loadWorld(String path) {
		try {
			loadWorldAsStringList(Files.readAllLines(Paths.get(path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadWorld(File file) {
		try {
			loadWorldAsStringList(Files.readAllLines(Paths.get(file.toURI())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadKarelJRobotWorld(String path) {
		//TODO complete translation of old world form to new
	}

	public void loadWorldString(String worldString) {
		loadWorldAsStringList(Arrays.asList(worldString.split("\n")));
	}

	public void loadWorldAsStringList(List<String> lines) {//TODO this forEach thing might not work for multiline Robot serialized bytecode
		//TODO try catch, if any parse-ints fail, reset the world and popup a msgbox that say "invalid world file" or somthin
		//completely reset this world. like calling the constructor again
		map = new HashMap<Point,Cell>();
		robots = new ArrayList<Robot>();
		width = 0;
		height = 0;
		colorCollection = WorldColorCollection.getDefaultWorldColorCollection();

		lines.forEach(line -> {
			String[] tokens = line.split(" ");
			switch (tokens[0]) {
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

	public void printWorld() {
		getWorldAsStringList().forEach(System.out::println);
	}

	public void add(Point point, Cell cell) {
		if (map.containsKey(point)) {
			map.get(point).add(cell);
		} else {
			map.put(point, cell);
		}
	}

	public void add(int x, int y, Cell cell) {
		add(new Point(x, y), cell);
	}

	public void removeAll(Point point) {
		get(point).clear();
	}

	public void removeAll(int x, int y) {
		removeAll(new Point(x, y));
	}

	public void remove(Point point, Cell cell) {
		get(point).remove(cell);
	}

	public void remove(int x, int y, Cell cell) {
		remove(new Point(x, y), cell);
	}

	public Cell get(Point point) {
		return map.getOrDefault(point, new Cell());
	}

	public Cell get(int x, int y) {
		return get(new Point(x, y));
	}

	public void add(Robot robot) {
		robots.add(robot);
		robot.world = this;
	}

}
