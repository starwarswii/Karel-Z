package karelz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {//represents an object that displays and updates a world
	
	static final int CELL_SIZE = 30;
	static final int CELL_MARGIN = 2;
	static final int WINDOW_MARGIN = CELL_SIZE/5;
	static final int WALL_THICKNESS = CELL_MARGIN+1;
	static final int EDGE_WALL_MULTIPLIER = 1;
	static final int IMAGE_OFFSET = CELL_SIZE/7;
	
	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	
	World world;
	ZoomAndPanPanel panel;
	int delay;

	public Window(World aWorld, int delay) {
		super("Karel-Z");
		
		world = aWorld;
		this.delay = delay;
		
		//the +1's give a border of .5 cells, with 20 extra vertical pixels for the title bar
		setBounds(0, 0, Math.min((world.width+1)*CELL_SIZE+WINDOW_MARGIN, (int)SCREEN_SIZE.getWidth()), Math.min((world.height+1)*CELL_SIZE+WINDOW_MARGIN+20, (int)SCREEN_SIZE.getHeight()));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PaintStrategy strategy = new PaintStrategy() {
			
			public void paint(Graphics2D g, Point mouse) {
				

				
				//no need to fill the background color as it will already be present due to panel.setBackground();
				
				//drawing grid lines
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.setColor(world.lineColor);
				
				//drawing vertical grid lines
				for (int i = 0; i < world.width+1; i++) {
					g.drawLine((i*CELL_SIZE)+WINDOW_MARGIN, (world.height*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, WINDOW_MARGIN);
				}
				
				//drawing horizontal grid lines
				for (int i = 0; i < world.height+1; i++) {
					g.drawLine(WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, (world.width*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN);
				}
				
				//drawing edge walls
				g.setColor(world.wallColor);
				
				//drawing horizontal edge wall
				g.fillRect(WINDOW_MARGIN, WINDOW_MARGIN-((WALL_THICKNESS-1)/2), world.width*CELL_SIZE*EDGE_WALL_MULTIPLIER, WALL_THICKNESS);

				//drawing vertical edge wall
				g.fillRect(WINDOW_MARGIN-((WALL_THICKNESS-1)/2), WINDOW_MARGIN, WALL_THICKNESS, world.height*CELL_SIZE*EDGE_WALL_MULTIPLIER);

				//drawing cell objects
				world.map.forEach((a, b) -> {

					//drawing beeper pile
					if (b.containsValidBeeperPile()) {
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.setColor(world.beeperColor);

						g.fillOval((a.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, (a.y*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));

						//drawing beeper pile label
						if (b.beepers > 1 || b.beepers == Cell.INFINITY) {
							g.setColor(world.beeperLabelColor);

							Font font = new Font("Consolas", Font.PLAIN, 12);

							String text = b.beepers > 1 ? Integer.toString(b.beepers) : "\u221e";//infinity symbol

							//creates a font that fits in the desired area, then rotates it upside down, as everything is flipped on the y axis
							g.setFont(Util.sizeFontToFit(g, font, text, CELL_SIZE-(6*CELL_MARGIN), CELL_SIZE-(4*CELL_MARGIN)).deriveFont(AffineTransform.getScaleInstance(1, -1)));

							//get the bounds of the fitted string. note bounds.getHeight() is negative because it is flipped upside down
							Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

							g.drawString(text, (a.x*CELL_SIZE)+((CELL_SIZE-(int)bounds.getWidth())/2)+WINDOW_MARGIN, (a.y*CELL_SIZE)+(2*CELL_MARGIN)+((CELL_SIZE+(int)bounds.getHeight())/2)+WINDOW_MARGIN);	
						}
					}

					//by convention horizontal walls are drawn on the bottom of the occupied cell,
					//whereas vertical walls are drawn on the left of the occupied cell.
					//block walls take up the entire occupied cell

					//drawing walls
					if (b.containsWall()) {
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						g.setColor(world.wallColor);

						if (b.containsHorizontalWall()) {
							g.fillRect((a.x*CELL_SIZE)+WINDOW_MARGIN, (a.y*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), CELL_SIZE, WALL_THICKNESS);
						}

						if (b.containsVerticalWall()) {
							g.fillRect((a.x*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), (a.y*CELL_SIZE)+WINDOW_MARGIN, WALL_THICKNESS, CELL_SIZE);
						}

						if (b.containsBlockWall()) {
							g.fillRect((a.x*CELL_SIZE)+WINDOW_MARGIN, (a.y*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
						}
					}
				});
				
				//drawing robots
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				world.robots.forEach(a -> g.drawImage(a.getCurrentImage(), (a.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, ((a.y+1)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN-IMAGE_OFFSET, CELL_SIZE-(2*CELL_MARGIN), -(CELL_SIZE-(2*CELL_MARGIN)), null));
			
				//drawing the mouse selector thingy
				if (mouse.x/CELL_SIZE >= 0 && mouse.y/CELL_SIZE >= 0) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					g.setColor(new Color(255, 0, 0, 100));
					g.fillRect(((mouse.x/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, ((mouse.y/CELL_SIZE)*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);	
				}
			}
		};
		
		panel = new ZoomAndPanPanel(strategy);
		panel.setBackground(world.backgroundColor);
		
		add(panel, BorderLayout.CENTER);
		
	}
	
	public void start() {//starts all the bots
		
		ArrayList<Robot> runningRobots = new ArrayList<Robot>(world.robots);
		
		runningRobots.forEach(Robot::launchThread);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				for (int i = 0; i < runningRobots.size(); i++) {
					Robot robot = runningRobots.get(i);
					if (robot.threadIsActive) {
						robot.step();
					} else {
						runningRobots.remove(i);
						i--;
					}
				}
				//these both need to be here. it worked before with just repaint, but once i started using bots from another package, it stopped working
				//panel.paint(panel.getGraphics());
				//DONT TOUCH, IT WORKS NOW WITH JUST THIS
				//somehow theres no ficker, just like make NO EDITS to this file
				panel.repaint();
				
				if (runningRobots.isEmpty()) {
					timer.cancel();
				}
			}
			
		}, 0, delay);
	}
}
