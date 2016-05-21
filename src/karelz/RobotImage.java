package karelz;

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


	public static RobotImage getRobotImage(Direction direction, RobotState state) {
		return values()[(state.ordinal()*4)+direction.ordinal()];

	}

}
