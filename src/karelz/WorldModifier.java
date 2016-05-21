package karelz;

import java.awt.Point;

public interface WorldModifier {
	//if remove is true, perform remove action instead of add
	public void modify(World world, Point point, int beepers, boolean remove);//the point is a valid world position

}
