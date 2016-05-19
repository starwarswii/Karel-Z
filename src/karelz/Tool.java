package karelz;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public enum Tool {//TODO add a "place robot" tool?
	
	//TODO fill tooltips out
	PAN_AND_ZOOM((a, b) -> {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		g.drawImage(Util.PAN_AND_ZOOM_OVERLAY, 0, 0, a.backgroundColor, null);
		return new ImageIcon(image);
	}, "TODO"),
	
	BEEPER_PILE((a, b) -> {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(a.backgroundColor);
		g.fillRect(0, 0, 24, 24);
		g.setColor(a.beeperColor);
		g.fillOval(0, 0, 24, 24);
		
		if (b > 1 || b == Cell.INFINITY) {
			g.setColor(a.beeperLabelColor);

			Font font = new Font("Consolas", Font.PLAIN, 12);

			String text = b > 1 ? Integer.toString(b) : "\u221e";//infinity symbol

			//creates a font that fits in the desired area
			g.setFont(Util.sizeFontToFit(g, font, text, 20, 20));

			//get the bounds of the fitted string
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

			g.drawString(text, (24-(int)bounds.getWidth())/2, ((24+(int)bounds.getHeight())/2)-2);
		}
		
		return new ImageIcon(image);
	}, "TODO"),
	
	HORIZONTAL_WALL((a, b) -> {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		
		g.setColor(a.backgroundColor);
		g.fillRect(0, 0, 24, 18);
		g.setColor(a.wallColor);
		g.fillRect(0, 18, 24, 6);
		return new ImageIcon(image);
	}, "TODO"),
	
	VERTICAL_WALL((a, b) -> {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		
		g.setColor(a.backgroundColor);
		g.fillRect(6, 0, 18, 24);
		g.setColor(a.wallColor);
		g.fillRect(0, 0, 6, 24);
		return new ImageIcon(image);
	}, "TODO"),
	
	BLOCK_WALL((a, b) -> {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		
		g.setColor(a.wallColor);
		g.fillRect(0, 0, 24, 24);
		return new ImageIcon(image);
	}, "TODO");
	
	IconGenerator generator;
	String toolTip;
	
	Tool(IconGenerator generator, String toolTip) {
		this.generator = generator;
		this.toolTip = toolTip;
	}
	
	public ImageIcon generateIcon(World world, int beepers) {
		return generator.generate(world, beepers);
	}
	
	public static ImageIcon generateIcon(Tool tool, World world, int beepers) {
		return tool.generateIcon(world, beepers);
	}
	
	public static ToolButton[] getButtons() {
		ToolButton[] buttons = new ToolButton[values().length];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new ToolButton(values()[i]);
		}
		return buttons;
	}

}
