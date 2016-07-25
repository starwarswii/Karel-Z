package karelz;

/**
 * An Enum expressing the relative directions in which Karel robots can move: up, right, down, and left.
 */
public enum Direction {
	UP,
	RIGHT,
	DOWN,
	LEFT;
	
	/**
	 * Gets the direction relative to this one after rotating 90 degrees clockwise.
	 *
	 * @return the clockwise rotated direction
	 */
	public Direction getClockwiseDirection() {
		return values()[(ordinal()+1)%4];
	}
	
	/**
	 * Gets the direction relative to this one after rotating 90 degrees counterclockwise.
	 *
	 * @return the counterclockwise rotated direction
	 */
	public Direction getCounterclockwiseDirection() {
		return values()[(ordinal()+3)%4];
	}
	
	/**
	 * Gets the direction relative to this one after rotating 180 degrees.
	 *
	 * @return the rotated direction
	 */
	public Direction getOppositeDirection() {
		return values()[(ordinal()+2)%4];
	}
}
