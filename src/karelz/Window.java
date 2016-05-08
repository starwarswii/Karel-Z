package karelz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {//represents an object that displays and updates a world
	
	static final int CELL_SIZE = 30;
	static final int CELL_MARGIN = 2;
	static final int WINDOW_MARGIN = CELL_SIZE/5;
	
	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	
	World world;

	public Window(World aWorld) {
		super("Karel-Z");
		
		world = aWorld;
		
		//the +1's give a border of .5 cells, with 20 extra vertical pixels for the title bar
		setBounds(0, 0, Math.min((world.width+1)*CELL_SIZE+WINDOW_MARGIN, (int)SCREEN_SIZE.getWidth()), Math.min((world.height+1)*CELL_SIZE+WINDOW_MARGIN+20, (int)SCREEN_SIZE.getHeight()));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PaintStrategy strategy = new PaintStrategy() {
			
			//Font font = new Font("Consolas", Font.PLAIN, 10);
			
			public void paint(Graphics2D g) {//TODO remove width height as not needed i think
				
				//no need to fill the background color as it will already be present due to panel.setBackground();
				
				//don't antialias anything except the beepers and the robots maybe
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				
				g.setColor(world.lineColor);
				
				//draw vertical grid lines
				for (int i = 0; i < world.width+1; i++) {//TODO draw walls going up and to the right forever
					g.drawLine((i*CELL_SIZE)+WINDOW_MARGIN, (world.height*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, WINDOW_MARGIN);
				}
				
				//draw horizontal grid lines
				for (int i = 0; i < world.height+1; i++) {
					g.drawLine(WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, (world.width*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN);
				}
				
				
				//drawing world objects
				for (Point a : world.map.keySet()) {
					
					WorldObject object = world.map.get(a);
					
					//draw beeper pile
					if (object instanceof BeeperPile) {
						
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
						BeeperPile pile = (BeeperPile)object;
						g.setColor(world.beeperColor);
						
						g.fillOval((a.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, (a.y*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));
						
						//draw label
						if (pile.count > 1 || pile.count == BeeperPile.ININITY) {
							g.setColor(world.beeperLabelColor);
							Font font = new Font("Consolas", Font.PLAIN, 12);

							String text = pile.count > 1 ? Integer.toString(pile.count) : "\u221e";//infinity symbol

							//creates a font that fits in the desired area, then rotates it upside down, as everything is flipped on the y axis
							g.setFont(Util.sizeFontToFit(g, font, text, CELL_SIZE-(6*CELL_MARGIN), CELL_SIZE-(4*CELL_MARGIN)).deriveFont(AffineTransform.getScaleInstance(1, -1)));

							//get the bounds of the fitted string. note bounds.getHeight() is negative because it is flipped upside down
							Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

							g.drawString(text, (a.x*CELL_SIZE)+((CELL_SIZE-(int)bounds.getWidth())/2)+WINDOW_MARGIN, (a.y*CELL_SIZE)+(2*CELL_MARGIN)+((CELL_SIZE+(int)bounds.getHeight())/2)+WINDOW_MARGIN);	
						}
					} else if (object instanceof Wall) {//draw wall
						//im pretty sure we shouldn't antialias the walls
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
						//TODO  draw wall
					}
				}
				//TODO also draw bots from the arraylist in world
				

				
			}
		};
		
		ZoomAndPanPanel panel = new ZoomAndPanPanel(strategy);
		panel.setBackground(world.backgroundColor);
		
		add(panel, BorderLayout.CENTER);
		
	}
	
	public void start() {//runs the bots n stuff
		
	}
}
