package karelz;

import java.awt.Point;

/**
 * A {@code WorldModifier} is an object that modifies a given world at a valid given point.
 * It is also given a number of beepers and a boolean deciding if the modification is an "add" action or a "remove" action.
 */
public interface WorldModifier {
	
	/**
	 * Modifies the given world at a given point.
	 * If {@code remove} is {@code true}, then the "remove" action is performed instead of the "add" action.
	 *
	 * @param world the world to modify
	 * @param point a valid point in the world to modify
	 * @param beepers the number of beepers
	 * @param remove the boolean that decides whether to add or remove
	 */
	public void modify(World world, Point point, int beepers, boolean remove);
	
}
