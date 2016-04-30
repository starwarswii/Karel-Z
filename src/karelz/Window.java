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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
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
	
	static final int CELL_SIZE = 30;
	
	static final Cursor HAND_OPENED = createCursor(getImage("hand_opened.png"), "hand_opened");
	static final Cursor HAND_CLOSED = createCursor(getImage("hand_closed.png"), "hand_closed");
	
	static BufferedImage testImage = getImage("test.png");//TODO remove?
	static BufferedImage image = new BufferedImage(1425, 950, BufferedImage.TYPE_INT_RGB);
	
	static JViewport viewport;

	static class ImagePanel extends JPanel {
		BufferedImage image;
		double scale;
		

		public ImagePanel(BufferedImage image) {
			this.image = image;
			scale = 1.0;
			//setBackground(Color.);
		}

		protected void paintComponent(Graphics g) {//TODO fix flickering when zooming
			super.paintComponent(g);
			image = getWorldImage(Math.min(10, Math.max(1, (int)((1d/scale)*10d))), new World(10, 10));
			image = getWorldImage(1, new World(10, 10));
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			double x = (getWidth()-scale*image.getWidth())/2;
			double y = (getHeight()-scale*image.getHeight())/2;
			AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
			transform.scale(scale, scale);
			g2.drawRenderedImage(image, transform);
		}

		/**
		 * For the scroll pane.
		 */
		public Dimension getPreferredSize() {
			return new Dimension((int)(scale*image.getWidth()), (int)(scale*image.getHeight()));
		}

		public void setScale(Point point, double s) {
			scale = s;
			revalidate(); // update the scroll pane
			repaint();
		}
		
		public void zoomOut(Point point) {
			if (scale*0.9 >= 0.01) {
				scale*=0.9;
			    Point viewPosition = viewport.getViewPosition();
			    viewport.setViewPosition(new Point((int)(point.x*(0.9-1)+0.9*viewPosition.x), (int)(point.y*(0.9-1)+0.9*viewPosition.y)));
			    revalidate();
			    repaint();
			}
		}

		public void zoomIn(Point point) {
			scale*=1.1;
			Point viewPosition = viewport.getViewPosition();
			viewport.setViewPosition(new Point((int)(point.x*(1.1-1)+1.1*viewPosition.x), (int)(point.y*(1.1-1)+1.1*viewPosition.y)));
		    revalidate();
		    repaint();
		}



	}

	public static void main(String[] args) {
		window = new Window();
		window.setVisible(true);
	}


	public Window() {
		super("Karel-Z");
		setBounds(100, 100, 1094, 714);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		scrollPane.setWheelScrollingEnabled(false);
		viewport = scrollPane.getViewport();
		//viewport.setBackground(Color.WHITE);
		//viewport.getGraphics().fillRect(0, 0, viewport.getWidth(), viewport.getHeight());

		
		//Graphics2D graphics = image.createGraphics();
		//graphics.setStroke(new BasicStroke(10));
		//graphics.setColor(Color.WHITE);
		//graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		//graphics.drawImage(getImage("karel.png"), 500, 500, viewport);
		//graphics.setColor(Color.BLACK);
		//graphics.drawLine(0, 0, 300, 300);
		
		//ImagePanel worldView = new ImagePanel(testImage);
		ImagePanel worldView = new ImagePanel(getWorldImage(10, new World(10,10)));
		//worldView.setCursor(HAND_OPENED);

		MouseAdapter listener = new MouseAdapter() {

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
				if (e.getModifiersEx() == 0) {//if no mouse buttons are being pressed
					if (e.getWheelRotation() < 0) {//zoom in
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

		viewport.addMouseListener(listener);
		viewport.addMouseMotionListener(listener);
		viewport.addMouseWheelListener(listener);
		viewport.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (viewport.getSize().equals(worldView.getSize())) {
					worldView.setCursor(Cursor.getDefaultCursor());
				}
			}
		});
		scrollPane.setViewportView(worldView);

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

	}
	
	public static BufferedImage getWorldImage(int stroke, World world) {
		BufferedImage image = new BufferedImage(Math.min((world.width*CELL_SIZE),SCREEN_WIDTH), Math.min((world.height*CELL_SIZE),SCREEN_HEIGHT), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		//graphics.setBackground(world.backgroundColor);
		//System.out.println(stroke);
		graphics.setStroke(new BasicStroke(stroke));
		graphics.setColor(world.backgroundColor);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.setColor(world.lineColor);
		//graphics.drawLine(0, 0, 0, image.getHeight()-1);
		//
		for (int i = 0; i < world.width; i++) {
			graphics.drawLine(i*CELL_SIZE, 0, i*CELL_SIZE, image.getHeight()-1);
		}
		graphics.drawLine(image.getWidth()-1, 0, image.getWidth()-1, image.getHeight()-1);
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
