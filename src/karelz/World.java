package karelz;

import java.awt.Color;
import java.awt.Point;
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
	Color wallColor;
	Color beeperColor;
	Color beeperLabelColor;
	Color lineColor;
	Color backgroundColor;

	public World(int width, int height, Color wallColor, Color beeperColor, Color beeperLabelColor, Color lineColor, Color backgroundColor) {
		map = new HashMap<Point,Cell>();
		robots = new ArrayList<Robot>();
		this.width = width;
		this.height = height;
		this.wallColor = wallColor;
		this.beeperColor = beeperColor;
		this.beeperLabelColor = beeperLabelColor;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;
	}

	public World(int width, int height) {
		this(width, height, Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE);//default color values
	}


	public World(String path) {
		this(0, 0);//to set default color values
		loadWorld(path);
	}

	public void saveWorld(String path) {
		try {
			Files.write(Paths.get(path), getWorldAsArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadWorld(String path) {
		try {
			loadWorldString(Files.readAllLines(Paths.get(path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadWorldString(String worldString) {
		loadWorldString(Arrays.asList(worldString.split("\n")));
	}

	//TODO add support for converting karel worlds to this new format maybe?
	public void loadWorldString(List<String> lines) {//TODO this forEach thing might not work for multiline Robot serialized bytecode
		lines.forEach(line -> {
			String[] tokens = line.split(" ");
			switch (tokens[0]) {
			case "world-size":
				width = Integer.parseInt(tokens[1]);
				height = Integer.parseInt(tokens[2]);
				break;
			case "wall-color":
				wallColor = new Color(Integer.parseInt(tokens[1]));
				break;
			case "beeper-color":
				beeperColor = new Color(Integer.parseInt(tokens[1]));
				break;
			case "beeper-label-color":
				beeperLabelColor = new Color(Integer.parseInt(tokens[1]));
				break;
			case "line-color":
				lineColor = new Color(Integer.parseInt(tokens[1]));
				break;
			case "background-color":
				backgroundColor = new Color(Integer.parseInt(tokens[1]));
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

	public List<String> getWorldAsArray() {
		List<String> lines = new ArrayList<String>();

		lines.add("world-size "+width+" "+height);
		lines.add("wall-color "+Integer.toString(wallColor.getRGB()));
		lines.add("beeper-color "+Integer.toString(beeperColor.getRGB()));
		lines.add("beeper-label-color "+Integer.toString(beeperLabelColor.getRGB()));
		lines.add("line-color "+Integer.toString(lineColor.getRGB()));
		lines.add("background-color "+Integer.toString(backgroundColor.getRGB()));

		map.forEach((a, b) -> {
			if (b.containsValidBeeperPile()) {
				lines.add("beeper-pile "+a.x+" "+a.y+" "+b.beepers);
			}

			if (b.containsHorizontalWall()) {
				lines.add("horizontal-wall "+a.x+" "+a.y);
			}

			if (b.containsVerticalWall()) {
				lines.add("vertical-wall "+a.x+" "+a.y);
			}

			if (b.containsBlockWall()) {
				lines.add("block-wall "+a.x+" "+a.y);
			}
		});

		return lines;
	}

	public void printWorld() {
		getWorldAsArray().forEach(System.out::println);
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
