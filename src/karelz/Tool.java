package karelz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import static karelz.Window.CELL_SIZE;
import static karelz.Window.CELL_MARGIN;
import static karelz.Window.WINDOW_MARGIN;
import static karelz.Window.WALL_THICKNESS;

public enum Tool {//TODO add a "place robot" tool?

	PAN_AND_ZOOM(ToolImageOverlay.PAN_AND_ZOOM_OVERLAY, "Pan and Zoom"),

	BEEPER_PILE(
			(world, beepers) -> {//generate icon
				BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g.setColor(world.colorCollection.backgroundColor);
				g.fillRect(0, 0, 24, 24);
				g.setColor(world.colorCollection.beeperColor);
				g.fillOval(0, 0, 24, 24);

				if (beepers > 1 || beepers == Cell.INFINITY) {
					g.setColor(world.colorCollection.beeperLabelColor);

					Font font = new Font("Consolas", Font.PLAIN, 12);

					String text = beepers > 1 ? Integer.toString(beepers) : "\u221e";//infinity symbol

					//creates a font that fits in the desired area
					g.setFont(Util.sizeFontToFit(g, font, text, 20, 20));

					//get the bounds of the fitted string
					Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

					g.drawString(text, (24-(int)bounds.getWidth())/2, ((24+(int)bounds.getHeight())/2)-2);
				}

				return new ImageIcon(image);
			},

			(graphics, point) -> {//draw selector
				graphics.setColor(new Color(0, 0, 200, 150));
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				graphics.fillOval(((point.x/CELL_SIZE)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, ((point.y/CELL_SIZE)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));
			},

			(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newBeeperPile(beepers), remove),//modify world

			"Place beeper pile"

			),

	HORIZONTAL_WALL(
			(world, beepers) -> {//generate icon
				BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				Graphics g = image.createGraphics();

				g.setColor(world.colorCollection.backgroundColor);
				g.fillRect(0, 0, 24, 18);
				g.setColor(world.colorCollection.wallColor);
				g.fillRect(0, 18, 24, 6);
				return new ImageIcon(image);
			},

			(graphics, point) -> {//draw selector
				graphics.setColor(new Color(0, 0, 200, 150));
				graphics.fillRect(((point.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((point.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), CELL_SIZE, WALL_THICKNESS*2);
			},

			(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newHorizontalWall(), remove),//modify world

			"Place horizontal wall"

			),

	VERTICAL_WALL(
			(world, beepers) -> {//generate icon
				BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				Graphics g = image.createGraphics();

				g.setColor(world.colorCollection.backgroundColor);
				g.fillRect(6, 0, 18, 24);
				g.setColor(world.colorCollection.wallColor);
				g.fillRect(0, 0, 6, 24);
				return new ImageIcon(image);
			},

			(graphics, point) -> {//draw selector
				graphics.setColor(new Color(0, 0, 200, 150));
				graphics.fillRect(((point.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), ((point.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, WALL_THICKNESS*2, CELL_SIZE);
			},

			(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newVerticalWall(), remove),//modify world

			"Place vertical wall"

			),

	BLOCK_WALL(
			(world, beepers) -> {//generate icon
				BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				Graphics g = image.createGraphics();

				g.setColor(world.colorCollection.wallColor);
				g.fillRect(0, 0, 24, 24);
				return new ImageIcon(image);
			},

			(graphics, point) -> {//draw selector
				graphics.setColor(new Color(0, 0, 200, 150));
				graphics.fillRect(((point.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((point.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
			},

			(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newBlockWall(), remove),//modify world

			"Place block wall"

			),

	ERASER(
			ToolImageOverlay.ERASER_OVERLAY,

			(graphics, point) -> {//draw selector
				graphics.setColor(new Color(250, 0, 0, 150));
				graphics.fillRect(((point.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((point.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
			},

			(world, point, beepers, remove) -> world.removeAll(point),//modify world

			"Eraser"

			);

	IconGenerator generator;
	PaintStrategy selector;
	WorldModifier modifier;
	String toolTip;

	Tool(IconGenerator generator, PaintStrategy selector, WorldModifier modifier, String toolTip) {
		this.generator = generator;
		this.selector = selector;
		this.modifier = modifier;
		this.toolTip = toolTip;
	}

	Tool(BufferedImage staticImage, String toolTip) {//for PAN_AND_ZOOM
		this(staticImage, (graphics, point) -> {}, (world, point, beepers, remove) -> {}, toolTip);
	}

	Tool(BufferedImage staticImage, PaintStrategy selector, WorldModifier modifier, String toolTip) {//for ERASER
		this((world, beepers) -> {
			BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(staticImage, 0, 0, world.colorCollection.backgroundColor, null);
			return new ImageIcon(image);
		}, selector, modifier, toolTip);
	}

	public static void addOrRemove(World world, Point point, Cell cell, boolean remove) {
		if (remove) {
			world.remove(point, cell);
		} else {
			world.add(point, cell);
		}
	}

	public ImageIcon generateIcon(World world, int beepers) {
		return generator.generate(world, beepers);
	}

	public void drawSelector(Graphics2D g, Point point) {
		selector.paint(g, point);
	}

	public void modifyWorld(World world, Point point, int beepers, boolean remove) {
		modifier.modify(world, point, beepers, remove);
	}

	public static ToolButton[] getButtons() {
		ToolButton[] buttons = new ToolButton[values().length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new ToolButton(values()[i]);
		}
		return buttons;
	}

}
