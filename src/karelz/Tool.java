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

/**
 * The Tool Enum contains all of the tools used to modify worlds.
 * Each tool contains lambda functions that generate its icon, draw its selector, and modify the world at a given spot.
 * The tools also contain their tool tip strings.
 * 
 * @see ToolButton
 * @see IconGenerator
 * @see PaintStrategy
 * @see WorldModifier
 */
public enum Tool {//TODO add a "place robot" tool?
	
	PAN_AND_ZOOM(ImageOverlay.PAN_AND_ZOOM_OVERLAY, "Pan and Zoom"),
	
	BEEPER_PILE(
		(colorCollection, beepers) -> {//generate icon
			BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setColor(colorCollection.backgroundColor);
			g.fillRect(0, 0, 24, 24);
			g.setColor(colorCollection.beeperColor);
			g.fillOval(0, 0, 24, 24);
			
			if (beepers > 1 || beepers == Cell.INFINITY) {
				g.setColor(colorCollection.beeperLabelColor);
				
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
		
		(graphics, mouse) -> {//draw selector
			graphics.setColor(new Color(0, 0, 200, 150));
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.fillOval(((mouse.x/CELL_SIZE)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, ((mouse.y/CELL_SIZE)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));
		},
		
		(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newBeeperPile(beepers), remove),//modify world
		
		"Place beeper pile"
		
		),
	
	HORIZONTAL_WALL(
		(colorCollection, beepers) -> {//generate icon
			BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			
			g.setColor(colorCollection.backgroundColor);
			g.fillRect(0, 0, 24, 18);
			g.setColor(colorCollection.wallColor);
			g.fillRect(0, 18, 24, 6);
			return new ImageIcon(image);
		},
		
		(graphics, mouse) -> {//draw selector
			graphics.setColor(new Color(0, 0, 200, 150));
			graphics.fillRect(((mouse.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((mouse.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), CELL_SIZE, WALL_THICKNESS*2);
		},
		
		(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newHorizontalWall(), remove),//modify world
		
		"Place horizontal wall"
		
		),
	
	VERTICAL_WALL(
		(colorCollection, beepers) -> {//generate icon
			BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			
			g.setColor(colorCollection.backgroundColor);
			g.fillRect(6, 0, 18, 24);
			g.setColor(colorCollection.wallColor);
			g.fillRect(0, 0, 6, 24);
			return new ImageIcon(image);
		},
		
		(graphics, mouse) -> {//draw selector
			graphics.setColor(new Color(0, 0, 200, 150));
			graphics.fillRect(((mouse.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), ((mouse.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, WALL_THICKNESS*2, CELL_SIZE);
		},
		
		(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newVerticalWall(), remove),//modify world
		
		"Place vertical wall"
		
		),
	
	BLOCK_WALL(
		(colorCollection, beepers) -> {//generate icon
			BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			
			g.setColor(colorCollection.wallColor);
			g.fillRect(0, 0, 24, 24);
			return new ImageIcon(image);
		},
		
		(graphics, mouse) -> {//draw selector
			graphics.setColor(new Color(0, 0, 200, 150));
			graphics.fillRect(((mouse.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((mouse.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
		},
		
		(world, point, beepers, remove) -> addOrRemove(world, point, Cell.newBlockWall(), remove),//modify world
		
		"Place block wall"
		
		),
	
	ERASER(
		ImageOverlay.ERASER_OVERLAY,
		
		(graphics, mouse) -> {//draw selector
			graphics.setColor(new Color(250, 0, 0, 150));
			graphics.fillRect(((mouse.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((mouse.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
		},
		
		(world, point, beepers, remove) -> world.removeAll(point),//modify world
		
		"Eraser"
		
		);
	
	IconGenerator generator;
	PaintStrategy selector;
	WorldModifier modifier;
	String toolTip;
	
	/**
	 * The static class ImageOverlay is used to store loaded {@code BufferedImage} overlays used in the tools.
	 * It needs to exist as otherwise the images would be loaded from files again each time a tool was used.
	 * The image overlays are loaded statically when in this method, meaning they are loaded only once.
	 */
	static class ImageOverlay {
		static final BufferedImage PAN_AND_ZOOM_OVERLAY = Util.getImage("pan-and-zoom.png");
		static final BufferedImage ERASER_OVERLAY = Util.getImage("eraser.png");
	}
	
	/**
	 * Instantiates a new tool.
	 *
	 * @param generator an {@code IconGenerator} lambda expression that returns a generated icon
	 * @param selector a {@code PaintStrategy} lambda expression that paints a selector
	 * @param modifier a {@code WorldModifier} lambda expression that performs the tool's action on a a point in a world
	 * @param toolTip the tool tip
	 */
	Tool(IconGenerator generator, PaintStrategy selector, WorldModifier modifier, String toolTip) {
		this.generator = generator;
		this.selector = selector;
		this.modifier = modifier;
		this.toolTip = toolTip;
	}
	
	/**
	 * Instantiates a new tool with an image overlay that has no selector and has no world modifier.
	 * <br>This constructor is used to create the {@link #PAN_AND_ZOOM} tool as it needs no selector
	 * and all the functions are contained within the {@code ZoomAndPanPanel} the world is drawn on.
	 *
	 * @param imageOverlay the image overlay
	 * @param toolTip the tool tip
	 */
	Tool(BufferedImage imageOverlay, String toolTip) {
		this(imageOverlay, (graphics, point) -> {}, (world, point, beepers, remove) -> {}, toolTip);
	}
	
	/**
	 * Instantiates a new tool with an image overlay.
	 * <br>This is used to construct tools that use a static image as an icon, such as the {@link #ERASER}.
	 *
	 * @param imageOverlay the image overlay
	 * @param selector a {@code PaintStrategy} lambda expression that paints a selector
	 * @param modifier a {@code WorldModifier} lambda expression that performs the tool's action on a a point in a world
	 * @param toolTip the tool tip
	 */
	Tool(BufferedImage imageOverlay, PaintStrategy selector, WorldModifier modifier, String toolTip) {
		this(
			(colorCollection, beepers) -> {//generate icon
				BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				Graphics g = image.createGraphics();
				
				g.drawImage(imageOverlay, 0, 0, colorCollection.backgroundColor, null);
				return new ImageIcon(image);
			},
			
			selector,//draw selector
			
			modifier,//modify world
			
			toolTip
			
			);
	}
	
	/**
	 * If {@code remove} is {@code true}, removes the given cell from the cell at the given point in the world.
	 * <br>Otherwise, adds the given cell to the cell at the given point in the world.
	 * <br>This is a utility method used by tools to simplify their {@code WorldModifier} functions, as this is how most tools modify the world
	 *
	 * @param world the world
	 * @param point a valid point in the world
	 * @param cell the cell to be added to or removed from a world cell
	 * @param remove the boolean that decides whether to add or remove
	 */
	public static void addOrRemove(World world, Point point, Cell cell, boolean remove) {
		if (remove) {
			world.remove(point, cell);
		} else {
			world.add(point, cell);
		}
	}
	
	/**
	 * Calls {@link IconGenerator#generate(WorldColorCollection, int)} on this tool's {@code IconGenerator} and returns the result.
	 *
	 * @param colorCollection the color collection
	 * @param beepers the number of beepers
	 * @return the generated icon
	 */
	public ImageIcon generateIcon(WorldColorCollection colorCollection, int beepers) {
		return generator.generate(colorCollection, beepers);
	}
	
	/**
	 * Calls {@link PaintStrategy#paint(Graphics2D, Point)} on this tool's {@code PaintStrategy}, painting a selector on the screen.
	 *
	 * @param g the graphics object used to paint with
	 * @param mouse the point
	 */
	public void drawSelector(Graphics2D g, Point mouse) {
		selector.paint(g, mouse);
	}
	
	/**
	 * Calls {@link WorldModifier#modify(World, Point, int, boolean)} on this tool's {@code WorldModifier}, modifying the world at a given point.
	 *
	 * @param world the world to modify
	 * @param point a valid point in the world to modify
	 * @param beepers the number of beepers
	 * @param remove the boolean that decides whether to add or remove
	 */
	public void modifyWorld(World world, Point point, int beepers, boolean remove) {
		modifier.modify(world, point, beepers, remove);
	}
	
	/**
	 * Creates and returns an array of {@code ToolButtons} containing one {@code ToolButton} for each {@code Tool}.
	 *
	 * @return an array of {@code ToolButtons} containing one {@code ToolButton} for each {@code Tool}
	 * @see ToolButton
	 */
	public static ToolButton[] getButtons() {
		ToolButton[] buttons = new ToolButton[values().length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new ToolButton(values()[i]);
		}
		return buttons;
	}
	
}
