package karelz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class PanAndZoomPanel extends JPanel {

	boolean init;
	PanAndZoomListener panAndZoomListener;
	PaintStrategy paintStrategy;

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		JFrame frame = new JFrame("Zoom and Pan Canvas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BufferedImage up = Util.getImage("karel-on.png");
		BufferedImage right = Util.getRotatedImage(up, 90);
		BufferedImage down = Util.getRotatedImage(up, 180);
		BufferedImage left = Util.getRotatedImage(up, 270);

		RobotImageCollection collectionNone = new RobotImageCollection();
		RobotImageCollection collectionRed = new RobotImageCollection(Color.RED);
		RobotImageCollection collectionGreen = new RobotImageCollection(Color.GREEN);

		PaintStrategy strat = new PaintStrategy() {

			public void paint(Graphics2D g, Point mouse) {
				//System.out.println(g.getClipBounds());
				int w = 600;
				int h = 500;
				g.drawLine(w/2, 0, w/2, h);
				g.drawLine(0, h/2, w, h/2);
				g.fillOval(0, 0, 100, 100);
				//g.drawImage(image, 200,200,300,300, null);
				g.drawImage(collectionNone.getImage(RobotImage.UP_ON), 0, 800, 382, -382, null);
				g.drawImage(collectionRed.getImage(RobotImage.RIGHT_OFF), 400, 800, 382, -382, null);
				g.drawImage(collectionGreen.getImage(RobotImage.LEFT_ERROR), 800, 800, 382, -382, null);
				//g.drawImage(collectionNone.getImage(RobotImage.UP_ON), 0, 0, 100, -100, null);
				//g.drawImage(right, 300, 300, 300, 300, null);
				//g.drawImage(down, 300, 300, 300, 300, null);
				//g.drawImage(left, 300, 300, 300, 300, null);
				//int drawLocationX = 300;
				//int drawLocationY = 300;

				// Rotation information

				//double rotationRequired = Math.toRadians (90);
				//double locationX = image.getWidth() / 2;
				//double locationY = image.getHeight() / 2;
				//AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				//AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

				// Drawing the rotated image at the required drawing locations
				//g.drawImage(op.filter(image, null), drawLocationX, drawLocationY, 300, 300, null);
			}
		};

		PanAndZoomPanel panel = new PanAndZoomPanel(strat);

		frame.add(panel, BorderLayout.CENTER);
		frame.setBounds(0, 0, 600, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		//chart.createBufferStrategy(2);
	}

	public PanAndZoomPanel(PaintStrategy paintStrategy) {
		panAndZoomListener = new PanAndZoomListener(this);
		addMouseListener(panAndZoomListener);
		addMouseMotionListener(panAndZoomListener);
		addMouseWheelListener(panAndZoomListener);
		this.paintStrategy = paintStrategy;
		init = true;
	}

	public PanAndZoomPanel(int minZoomLevel, int maxZoomLevel, double zoomMultiplicationFactor, AffineTransform defaultTransform, PaintStrategy paintStrategy) {
		panAndZoomListener = new PanAndZoomListener(this, minZoomLevel, maxZoomLevel, zoomMultiplicationFactor, defaultTransform);
		addMouseListener(panAndZoomListener);
		addMouseMotionListener(panAndZoomListener);
		addMouseWheelListener(panAndZoomListener);
		this.paintStrategy = paintStrategy;
		init = true;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g.create();

		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		if (init) {
			init = false;
			g2d.translate(0, getHeight());
			g2d.scale(1, -1);
			panAndZoomListener.setTransform(g2d.getTransform());
		} else {
			g2d.setTransform(panAndZoomListener.coordTransform);
		}

		Point mouse = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouse, this);
		Point2D.Float transformed = panAndZoomListener.transformPoint(mouse);

		paintStrategy.paint(g2d, new Point((int)transformed.x, (int)transformed.y));

	}
	
	public void resetPanAndZoom() {
		panAndZoomListener.resetPanAndZoom();
	}
}
