package karelz;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.SwingUtilities;

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

	public PanAndZoomListener(PanAndZoomPanel targetPanel) {
		this(targetPanel, DEFAULT_MIN_ZOOM_LEVEL, DEFAULT_MAX_ZOOM_LEVEL, DEFAULT_ZOOM_MULTIPLICATION_FACTOR, new AffineTransform());
	}

	public void mousePressed(MouseEvent e) {
		if (enabled && SwingUtilities.isLeftMouseButton(e)) {
			targetPanel.setCursor(PAN_DRAG);
			dragStartScreen = e.getPoint();
			dragEndScreen = null;
		}

	}

	public void mouseReleased(MouseEvent e) {
		if (enabled) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				targetPanel.setCursor(PAN_HOVER);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				resetPanAndZoom();
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (enabled && SwingUtilities.isLeftMouseButton(e)) {
			moveCamera(e);
		}

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		//if pan and zoom selected and no mouse buttons are being pressed
		if (enabled && e.getModifiersEx() == 0) {
			zoomCamera(e);
		}
	}

	public void moveCamera(MouseEvent e) {
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

	public void zoomCamera(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		Point p = e.getPoint();
		if (wheelRotation > 0) {
			if (zoomLevel < maxZoomLevel) {
				zoomLevel++;
				Point2D p1 = transformPoint(p);
				coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
				Point2D p2 = transformPoint(p);
				coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
				targetPanel.repaint();
			}
		} else {
			if (zoomLevel > minZoomLevel) {
				zoomLevel--;
				Point2D p1 = transformPoint(p);
				coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
				Point2D p2 = transformPoint(p);
				coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
				targetPanel.repaint();
			}
		}
	}

	public void resetPanAndZoom() {
		coordTransform = new AffineTransform(defaultTransform);
		zoomLevel = 0;
		targetPanel.repaint();
	}

	public Point2D.Float transformPoint(Point p1) {
		try {
			AffineTransform inverse;
			inverse = coordTransform.createInverse();
			Point2D.Float p2 = new Point2D.Float();
			inverse.transform(p1, p2);
			return p2;
		} catch (Exception e) {
			return null;
		}
	}

	public void setTransform(AffineTransform transform) {
		coordTransform = new AffineTransform(transform);
		defaultTransform = transform;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		targetPanel.setCursor(this.enabled ? PAN_HOVER : Cursor.getDefaultCursor());
	}
}
