package karelz;

public enum Direction {
	UP,
	RIGHT,
	DOWN,
	LEFT;
	
	public Direction getClockwiseDirection() {
		return values()[(ordinal()+1)%4];
	}
	
	public Direction getCounterclockwiseDirection() {
		return values()[(ordinal()+3)%4];
	}

}
