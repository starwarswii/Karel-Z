package karelz;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class PanAndZoomPanel extends JPanel {

	boolean init;
	PanAndZoomListener panAndZoomListener;
	PaintStrategy paintStrategy;

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

		//we create a new graphics object as paintComponent says its bad to transform the current graphics object
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
