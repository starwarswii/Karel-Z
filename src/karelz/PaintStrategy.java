package karelz;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 * A {@code PaintStrategy} is a way of painting some object or area given a
 * {@code Graphics2D} object used to paint and the position of the mouse pointer given as a {@code Point}.
 * <br>It is used in {@link PanAndZoomPanel} objects to define how the panel is painted.
 * 
 * @see PanAndZoomPanel#paintComponent(Graphics)
 * @see Graphics2D
 */
public interface PaintStrategy {
	
	/**
	 * Paints using the given {@code Graphics2D} object and the mouse position.
	 *
	 * @param g the graphics object used to paint with
	 * @param mouse the mouse position as a {@code Point}
	 */
	public void paint(Graphics2D g, Point mouse);
	
}
