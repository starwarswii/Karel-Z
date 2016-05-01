package karelz;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Window extends JFrame {
	

	static Window window;
	static World world;
	
	static final int SCREEN_WIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
	static final int SCREEN_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
	
	static final int WINDOW_WIDTH = SCREEN_WIDTH-600;
	static final int WINDOW_HEIGHT = SCREEN_HEIGHT-200;
	
	
	static final int CELL_SIZE = 30;
	static final int CELL_MARGIN = 2;
	
	static final Cursor HAND_OPENED = createCursor(getImage("hand_opened.png"), "hand_opened");
	static final Cursor HAND_CLOSED = createCursor(getImage("hand_closed.png"), "hand_closed");
	
	static BufferedImage testImage = getImage("test.png");//TODO remove?
	static BufferedImage image = new BufferedImage(1425, 950, BufferedImage.TYPE_INT_RGB);
	
	static JViewport viewport;

	static class ImagePanel extends JPanel {
		
		BufferedImage image;
		double scale;
		static final double ZOOM_IN_MULTIPLIER = 1.1;//should be greater than 1
		static final double ZOOM_OUT_MULTIPLIER = 0.9;//should be less than 1
		
		Dimension preferredSize;
		

		public ImagePanel(int width, int height) {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			preferredSize = new Dimension(width, height);
			scale = CELL_SIZE/(1.25*((double)Math.min(world.width,world.height)));//TODO fix for large cell sizes
			//scale = 2;
			setBackground(world.backgroundColor.darker());//TODO remove the darker
			//setBackground(world.backgroundColor);
		}

//		protected void paintComponent(Graphics g) {//TODO fix jumping when zooming on point
//			super.paintComponent(g);
//			//image = getWorldImage(Math.min(10, Math.max(1, (int)((1d/scale)*10d))), new World(10, 10));
//			image = getWorldImage(world);
//			Graphics2D g2 = (Graphics2D)g;
//			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);//TODO maybe use bicubic
//			double x = (getWidth()-scale*image.getWidth())/2;
//			double y = (getHeight()-scale*image.getHeight())/2;
//			AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
//			transform.scale(scale, scale);
//			g2.drawRenderedImage(image, transform);
//		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			testWorldTEMP(this, (Graphics2D)g, world);
			revalidate();
		}

		
//		//For the scroll pane
//		public Dimension getPreferredSize() {
//			return new Dimension((int)(scale*image.getWidth()), (int)(scale*image.getHeight()));
//		}
		
		public Dimension getPreferredSize() {
		    return preferredSize;
		}

		public void setScale(Point point, double s) {
			scale = s;
			revalidate();//update the scroll pane
			repaint();
		}
		
		public void updateZoomAmount(int n, Point p) {
			double d = (double) n * 1.08;
		    d = (n > 0) ? 1 / d : -d;

		    int w = (int) (getWidth() * d);
		    int h = (int) (getHeight() * d);
		    preferredSize.setSize(w, h);

		    int offX = (int)(p.x * d) - p.x;
		    int offY = (int)(p.y * d) - p.y;
		    setLocation(getLocation().x-offX,getLocation().y-offY);

		    getParent().doLayout();
		    getParent().validate();
		    revalidate();
		    repaint();
		}
		
		public void zoomIn(Point point) {
			scale*=ZOOM_IN_MULTIPLIER;
			//prevents flickering
			revalidate();
			repaint();
			Point viewPosition = viewport.getViewPosition();
			viewport.setViewPosition(new Point((int)(point.x*(ZOOM_IN_MULTIPLIER-1)+ZOOM_IN_MULTIPLIER*viewPosition.x), (int)(point.y*(ZOOM_IN_MULTIPLIER-1)+ZOOM_IN_MULTIPLIER*viewPosition.y)));
		    revalidate();
		    repaint();
		}
		
		public void zoomOut(Point point) {
			if (scale*ZOOM_OUT_MULTIPLIER >= 0.01) {
				scale*=ZOOM_OUT_MULTIPLIER;
				//System.out.println(scale);
				//System.out.println(viewport.getExtentSize());
				//prevents flickering
				revalidate();
				repaint();
			    Point viewPosition = viewport.getViewPosition();
			    viewport.setViewPosition(new Point((int)(point.x*(ZOOM_OUT_MULTIPLIER-1)+ZOOM_OUT_MULTIPLIER*viewPosition.x), (int)(point.y*(ZOOM_OUT_MULTIPLIER-1)+ZOOM_OUT_MULTIPLIER*viewPosition.y)));
			    revalidate();
			    repaint();
			}
		}
		
	}

	public static void main(String[] args) {
		window = new Window();
		window.setVisible(true);
	}


	public Window() {
		super("Karel-Z");
		setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		//scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		scrollPane.setWheelScrollingEnabled(false);
		
		viewport = scrollPane.getViewport();

		
		world = new World(10, 10);//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		world.addObject(new BeeperPile(1), 3, 3);
		world.addObject(new BeeperPile(1), 25, 25);
		world.addObject(new Wall(Walls.BLOCK), 0, 9);
		
		ImagePanel worldView = new ImagePanel(0, 0);

		MouseAdapter mouseListener = new MouseAdapter() {

			Point dragPoint = new Point();

			public boolean isPannable() {
				return !viewport.getSize().equals(worldView.getSize());
			}

			public void mouseDragged(MouseEvent e) {
				if (isPannable()) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						Point eventPoint = e.getPoint();
						Point viewPosition = viewport.getViewPosition();
						viewPosition.translate(dragPoint.x-eventPoint.x, dragPoint.y-eventPoint.y);
						//this kinda looks strange, going into the child and then forwarding to the parent, but is is needed
						((JComponent)viewport.getView()).scrollRectToVisible(new Rectangle(viewPosition, viewport.getSize()));
						dragPoint.setLocation(eventPoint);
					}
				} else {
					worldView.setCursor(Cursor.getDefaultCursor());
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (isPannable() && SwingUtilities.isLeftMouseButton(e)) {
					worldView.setCursor(HAND_CLOSED);
					dragPoint.setLocation(e.getPoint());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (isPannable() && SwingUtilities.isLeftMouseButton(e)) {
					worldView.setCursor(HAND_OPENED);
				}
			}
			
			public void mouseEntered(MouseEvent e) {
				if (isPannable()) {
					worldView.setCursor(HAND_OPENED);
				}
			}
			
			public void mouseWheelMoved(MouseWheelEvent e) {
				worldView.updateZoomAmount(e.getWheelRotation(), e.getPoint());
				
				if (e.getModifiersEx() == 0) {//if no mouse buttons are being pressed
					if (e.getWheelRotation() < 0) {//zoom in
						worldView.revalidate();
						worldView.repaint();
						worldView.zoomIn(e.getPoint());
					} else if (e.getWheelRotation() > 0) {//zoom out
						worldView.zoomOut(e.getPoint());
					}
				}
				if (isPannable()) {
					worldView.setCursor(HAND_OPENED);
				}
			}
		};

		viewport.addMouseListener(mouseListener);
		viewport.addMouseMotionListener(mouseListener);
		viewport.addMouseWheelListener(mouseListener);
		
		viewport.addComponentListener(new ComponentAdapter() {
			
			public void componentResized(ComponentEvent e) {
				//System.out.println(viewport.getSize());
				//System.out.println(worldView.scale);
				if (viewport.getSize().equals(worldView.getSize())) {
					worldView.setCursor(Cursor.getDefaultCursor());
				}
			}
		});
		//scrollPane.setViewportView(worldView);
		viewport.setView(worldView);
		
		//scrolls to the bottom left of the world
		((JComponent)viewport.getView()).scrollRectToVisible(new Rectangle(new Point(0, viewport.getView().getHeight()-viewport.getHeight()), viewport.getSize()));

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
						.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
						.addGap(44))
				);

		getContentPane().setLayout(groupLayout);

		//worldView.preferredSize;
				
		Rectangle r = new Rectangle(worldView.preferredSize);
		int cell = Math.min(r.width, r.height)/world.width;
		Dimension d = new Dimension(cell*world.width, cell*world.height);
		
		worldView.preferredSize = d;
		setBounds(new Rectangle(worldView.preferredSize));
		
	}
	
	
	
	public static void testWorldTEMP(ImagePanel panel, Graphics2D graphics, World world) {//TODO make -1's and stuff be -strokeSize's, and try to make zooming in make lines thicker maybe?
		//System.out.println(bounds);
		Rectangle r = viewport.getViewRect();
		r = new Rectangle(panel.preferredSize);
		int cell = Math.min(r.width, r.height)/world.width;
		r = new Rectangle(cell*world.width, cell*world.height);
		graphics.setColor(world.backgroundColor);
		graphics.fill(r);
		
		graphics.setColor(world.lineColor);
		
		
		
		
		
		//vertical grid lines
		for (int i = 0; i < world.width; i++) {
			graphics.drawLine(r.x+i*cell, r.y, i*cell, r.height-1);
		}
		graphics.drawLine(r.x+r.width-1, r.y, r.width-1, r.height-1);
		
		//horizontal grid lines
		for (int i = 0; i < world.height; i++) {
			graphics.drawLine(r.x, r.y+i*cell, r.width-1, i*cell);
		}
		graphics.drawLine(r.x, r.y+r.height-1, r.width-1, r.height-1);
		
		boolean b = true;//TODO Remove
		if (b){return;}
		
		for (Point a : world.map.keySet()) {
			WorldObject object = world.map.get(a);
			if (object instanceof BeeperPile) {//draw beeper pile
				BeeperPile pile = (BeeperPile)object;
				graphics.fillOval(a.x*CELL_SIZE+CELL_MARGIN, (world.height-1-a.y)*CELL_SIZE+CELL_MARGIN, CELL_SIZE-2*CELL_MARGIN, CELL_SIZE-2*CELL_MARGIN);
				if (pile.count > 1) {
					
				//draw number in white	
				} else if (pile.count == BeeperPile.ININITY) {
					
				}
			} else if (object instanceof Wall) {//draw wall
				//TODO  draw wall
			}
		}
		//TODO also draw bots from the arraylist in world
		//Rectangle r = graphics.getClipBounds();
		//graphics.setColor(Color.MAGENTA);
		//graphics.fill(r);
	}
	
	public static BufferedImage getWorldImage(World world) {
		BufferedImage image = new BufferedImage(world.width*CELL_SIZE, world.height*CELL_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		//graphics.setBackground(world.backgroundColor);
		//System.out.println(stroke);
		//graphics.setStroke(new BasicStroke(stroke));
		graphics.setColor(world.backgroundColor);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.setColor(world.lineColor);
		//graphics.drawLine(0, 0, 0, image.getHeight()-1);
		
		//vertical grid lines
		for (int i = 0; i < world.width; i++) {
			graphics.drawLine(i*CELL_SIZE, 0, i*CELL_SIZE, image.getHeight()-1);
		}
		graphics.drawLine(image.getWidth()-1, 0, image.getWidth()-1, image.getHeight()-1);
		
		//horizontal grid lines
		for (int i = 0; i < world.height; i++) {
			graphics.drawLine(0, i*CELL_SIZE, image.getWidth()-1, i*CELL_SIZE);
		}
		graphics.drawLine(0, image.getHeight()-1, image.getWidth()-1, image.getHeight()-1);
		
		for (Point a : world.map.keySet()) {
			WorldObject object = world.map.get(a);
			if (object instanceof BeeperPile) {//draw beeper pile
				BeeperPile pile = (BeeperPile)object;
				graphics.fillOval(a.x*CELL_SIZE+CELL_MARGIN, (world.height-1-a.y)*CELL_SIZE+CELL_MARGIN, CELL_SIZE-2*CELL_MARGIN, CELL_SIZE-2*CELL_MARGIN);
				if (pile.count > 1) {
					
				//draw number in white	
				} else if (pile.count == BeeperPile.ININITY) {
					
				}
			} else if (object instanceof Wall) {//draw wall
				//TODO  draw wall
			}
		}
		//TODO also draw bots from the arraylist in world
		return image;
		
	}
	
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(Window.class.getClassLoader().getResource("resources/"+filename));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Cursor createCursor(BufferedImage image, String name) {
		return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth()/2,image.getHeight()/2), name);
	}
	
	
}
