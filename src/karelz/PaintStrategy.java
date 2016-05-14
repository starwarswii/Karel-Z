package karelz;

import java.awt.Graphics2D;
import java.awt.Point;

public interface PaintStrategy {

	public void paint(Graphics2D g2d, Point mouse);

}
