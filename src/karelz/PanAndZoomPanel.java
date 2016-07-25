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

/**
 * {@code PanAndZoomPanel} objects make use of {@code PanAndZoomListeners} to allow panning and zooming of something painted on them.
 * It supports left click and drag to pan, right click to reset, and scroll wheel to zoom.
 * 
 * @see PanAndZoomListener
 */
@SuppressWarnings("serial")
public class PanAndZoomPanel extends JPanel {
	
	boolean init;
	PanAndZoomListener panAndZoomListener;
	PaintStrategy paintStrategy;
	
	/**
	 * Instantiates a new pan and zoom panel with the default zoom and transform values.
	 *
	 * @param paintStrategy the paint strategy to use when painting
	 * @see #paintComponent(Graphics)
	 * @see PaintStrategy
	 */
	public PanAndZoomPanel(PaintStrategy paintStrategy) {
		panAndZoomListener = new PanAndZoomListener(this);
		addMouseListener(panAndZoomListener);
		addMouseMotionListener(panAndZoomListener);
		addMouseWheelListener(panAndZoomListener);
		this.paintStrategy = paintStrategy;
		init = true;
	}
	
	/**
	 * Instantiates a new pan and zoom panel.
	 * 
	 * @param minZoomLevel the minimum zoom level
	 * @param maxZoomLevel the maximum zoom level
	 * @param zoomMultiplicationFactor the zoom multiplication factor, which controls how much the panel is zoomed in or out
	 * after a single notch is scrolled on the mouse wheel
	 * @param defaultTransform the default transform translation and scaling, used to reset the zoom and pan when the mouse is right clicked
	 * @param paintStrategy the paint strategy to use when painting
	 */
	public PanAndZoomPanel(int minZoomLevel, int maxZoomLevel, double zoomMultiplicationFactor, AffineTransform defaultTransform, PaintStrategy paintStrategy) {
		panAndZoomListener = new PanAndZoomListener(this, minZoomLevel, maxZoomLevel, zoomMultiplicationFactor, defaultTransform);
		addMouseListener(panAndZoomListener);
		addMouseMotionListener(panAndZoomListener);
		addMouseWheelListener(panAndZoomListener);
		this.paintStrategy = paintStrategy;
		init = true;
	}
	
	/**
	 * Paints this component using its {@code PaintStrategy}.
	 * <p>{@inheritDoc}
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//we create a new graphics object as the paintComponent Javadoc says "you should not make permanent changes to the passed in Graphics"
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
	
	/**
	 * Resets the pan and zoom of the target using the default transform.
	 * This is equivalent to {@code somePanAndZoomPanel.panAndZoomListener.resetPanAndZoom();}
	 */
	public void resetPanAndZoom() {
		panAndZoomListener.resetPanAndZoom();
	}
}
