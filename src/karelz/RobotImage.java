package karelz;

/**
 * An Enum expressing the all the possible images in which Karel robots can look like.
 * This includes all combinations of the 4 relative directions (UP, RIGHT, DOWN, LEFT) and the 3 robot states (On, OFF, ERROR).
 * 
 * @see Direction
 * @see RobotState
 * @see RobotImageCollection
 */
public enum RobotImage {
	
	UP_ON,
	RIGHT_ON,
	DOWN_ON,
	LEFT_ON,
	
	UP_OFF,
	RIGHT_OFF,
	DOWN_OFF,
	LEFT_OFF,
	
	UP_ERROR,
	RIGHT_ERROR,
	DOWN_ERROR,
	LEFT_ERROR;
	
	/**
	 * Gets the matching robot image given a direction and a robot state.
	 *
	 * @param direction the direction
	 * @param state the state
	 * @return the matching robot image
	 */
	public static RobotImage getRobotImage(Direction direction, RobotState state) {
		return values()[(state.ordinal()*4)+direction.ordinal()];
		
	}
}
