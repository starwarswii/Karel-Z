package karelz;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

/**
 * The {@code PanAndZoomListener} object is used by {@code PanAndZoomPanel} objects to handle panning and zooming using the mouse.
 * It supports left click and drag to pan, right click to reset, and scroll wheel to zoom.
 * 
 * @see ZoomAndPanPanel
 */
public class PanAndZoomListener extends MouseAdapter {
	
	static final int DEFAULT_MIN_ZOOM_LEVEL = -20;
	static final int DEFAULT_MAX_ZOOM_LEVEL = 10;
	static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;
	static final Cursor PAN_HOVER = Util.createCursor("pan-hover.png", "pan-hover");
	static final Cursor PAN_DRAG = Util.createCursor("pan-drag.png", "pan-drag");
	
	PanAndZoomPanel targetPanel;
	int zoomLevel;
	int minZoomLevel;
	int maxZoomLevel;
	double zoomMultiplicationFactor;
	Point dragStartScreen;
	Point dragEndScreen;
	AffineTransform coordTransform;
	AffineTransform defaultTransform;
	
	boolean enabled;
	
	/**
	 * Instantiates a new pan and zoom listener with the default zoom and transform values.
	 *
	 * @param targetPanel the target {@code PanAndZoomPanel} to manipulate
	 */
	public PanAndZoomListener(PanAndZoomPanel targetPanel) {
		this(targetPanel, DEFAULT_MIN_ZOOM_LEVEL, DEFAULT_MAX_ZOOM_LEVEL, DEFAULT_ZOOM_MULTIPLICATION_FACTOR, new AffineTransform());
	}
	
	/**
	 * Instantiates a new pan and zoom listener.
	 * 
	 * @param targetPanel the target {@code PanAndZoomPanel} to manipulate
	 * @param minZoomLevel the minimum zoom level
	 * @param maxZoomLevel the maximum zoom level
	 * @param zoomMultiplicationFactor the zoom multiplication factor, which controls how much the panel is zoomed in or out
	 * after a single notch is scrolled on the mouse wheel
	 * @param defaultTransform the default transform translation and scaling, used to reset the zoom and pan when the mouse is right clicked
	 */
	public PanAndZoomListener(PanAndZoomPanel targetPanel, int minZoomLevel, int maxZoomLevel, double zoomMultiplicationFactor, AffineTransform defaultTransform) {
		this.targetPanel = targetPanel;
		this.minZoomLevel = minZoomLevel;
		this.maxZoomLevel = maxZoomLevel;
		this.zoomMultiplicationFactor = zoomMultiplicationFactor;
		zoomLevel = 0;
		coordTransform = new AffineTransform(defaultTransform);
		this.defaultTransform = defaultTransform;
		setEnabled(true);
	}
	
	/**
	 * If panning and zooming are enabled and the left mouse button was pressed, then dragging is started.
	 * <p>{@inheritDoc}
	 * 
	 * @see MouseAdapter#mousePressed(MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (enabled && SwingUtilities.isLeftMouseButton(e)) {
			targetPanel.setCursor(PAN_DRAG);
			dragStartScreen = e.getPoint();
			dragEndScreen = null;
		}
		
	}
	
	/**
	 * If panning and zooming are enabled and the left mouse button was released, then dragging is stopped.
	 * If the right mouse button was released, panning and zooming are reset.
	 * <p>{@inheritDoc}
	 * 
	 * @see MouseAdapter#mouseReleased(MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (enabled) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				targetPanel.setCursor(PAN_HOVER);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				resetPanAndZoom();
			}
		}
	}
	
	/**
	 * If panning and zooming are enabled and the left mouse button was pressed, then the target is dragged.
	 * <p>{@inheritDoc}
	 * 
	 * @see MouseAdapter#mouseDragged(MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		if (enabled && SwingUtilities.isLeftMouseButton(e)) {
			pan(e);
		}
		
	}
	
	/**
	 * If panning and zooming are enabled and no mouse buttons were pressed, then the target is zoomed.
	 * <p>{@inheritDoc}
	 * 
	 * @see MouseAdapter#mouseWheelMoved(MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		//if pan and zoom selected and no mouse buttons are being pressed
		if (enabled && e.getModifiersEx() == 0) {
			zoom(e);
		}
	}
	
	/**
	 * Pans the target from the start of a drag to the end of one.
	 *
	 * @param e the {@code MOUSE_DRAGGED MouseEvent}
	 * @see #mouseDragged(MouseEvent)
	 */
	public void pan(MouseEvent e) {
		dragEndScreen = e.getPoint();
		Point2D.Float dragStart = transformPoint(dragStartScreen);
		Point2D.Float dragEnd = transformPoint(dragEndScreen);
		double dx = dragEnd.getX() - dragStart.getX();
		double dy = dragEnd.getY() - dragStart.getY();
		coordTransform.translate(dx, dy);
		dragStartScreen = dragEndScreen;
		dragEndScreen = null;
		targetPanel.repaint();
	}
	
	/**
	 * Zooms the target depending on the scroll direction from a {@code MouseWheelEvent}.
	 *
	 * @param e the {@code MouseWheelEvent} that determines the zoom direction
	 * @see #mouseWheelMoved(MouseWheelEvent)
	 */
	public void zoom(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		Point p = e.getPoint();
		if (wheelRotation > 0 && zoomLevel < maxZoomLevel) {
			zoomLevel++;
			Point2D p1 = transformPoint(p);
			coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
			Point2D p2 = transformPoint(p);
			coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
			targetPanel.repaint();
		} else if (wheelRotation < 0 && zoomLevel > minZoomLevel) {
			zoomLevel--;
			Point2D p1 = transformPoint(p);
			coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
			Point2D p2 = transformPoint(p);
			coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
			targetPanel.repaint();
		}
	}
	
	/**
	 * Resets the pan and zoom of the target using the default transform.
	 * 
	 * @see #mouseReleased(MouseEvent)
	 */
	public void resetPanAndZoom() {
		coordTransform = new AffineTransform(defaultTransform);
		zoomLevel = 0;
		targetPanel.repaint();
	}
	
	/**
	 * Gets the transform of a given point with the inverse of the current {@code AffineTransform}.
	 *
	 * @param point the point to get the transform of
	 * @return the transformed point
	 */
	public Point2D.Float transformPoint(Point point) {
		try {
			return (Point2D.Float)coordTransform.createInverse().transform(point, null);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Sets the current and default {@code AffineTransforms}, therefore setting and reseting the current pan and zoom.
	 *
	 * @param transform the new {@code AffineTransform}
	 */
	public void setTransform(AffineTransform transform) {
		coordTransform = new AffineTransform(transform);
		defaultTransform = transform;
	}
	
	/**
	 * Sets weather or not zooming and panning is enabled. This is used to disable zooming and panning when other {@code Tools} are being used.
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		targetPanel.setCursor(this.enabled ? PAN_HOVER : Cursor.getDefaultCursor());
	}
}
