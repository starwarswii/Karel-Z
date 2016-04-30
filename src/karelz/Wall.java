package karelz;
//the convention is that a wall occupies a cell, but renders to the left or above it,depending on horizontal, vertical
public class Wall implements WorldObject {
	
	Walls type;
	
	public Wall(Walls type) {
		this.type = type;
	}
}
