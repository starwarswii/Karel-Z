package karelz;

import java.util.Objects;

public class Cell {
	
	static final int INFINITY = -1;
	
	static final int NO_MASK = 0;
	static final int BEEPER_MASK = 1 << 0;
	static final int HORIZONTAL_WALL_MASK = 1 << 1;
	static final int VERTICAL_WALL_MASK = 1 << 2;
	static final int BLOCK_WALL_MASK = 1 << 3;
	
	int flags;
	int beeperCount;

	public Cell(int flags, int count) {
		this.flags = flags;
		this.beeperCount = count;
	}
	
	public Cell(int flags) {
		this(flags, 0);
	}
	
	public Cell() {
		this(NO_MASK, 0);
	}
	
	public int hashCode() {
		return Objects.hash(flags, beeperCount);
	}
	
	public static Cell newBeeperPile(int count) {
		return new Cell(BEEPER_MASK, count);
	}
	
	public static Cell newHorizontalWall() {
		return new Cell(HORIZONTAL_WALL_MASK);
	}
	
	public static Cell newVerticalWall() {
		return new Cell(VERTICAL_WALL_MASK);
	}
	
	public static Cell newBlockWall() {
		return new Cell(BLOCK_WALL_MASK);
	}
	
	public boolean containsFlag(int flag) {
		return (flags & flag) == flag;
	}
	
	public boolean containsBeeperPile() {
		return containsFlag(BEEPER_MASK);
	}
	
	public boolean containsHorizontalWall() {
		return containsFlag(HORIZONTAL_WALL_MASK);
	}
	
	public boolean containsVerticalWall() {
		return containsFlag(VERTICAL_WALL_MASK);
	}
	
	public boolean containsBlockWall() {
		return containsFlag(BLOCK_WALL_MASK);
	}
	
	public boolean containsWall() {
		return containsHorizontalWall() || containsVerticalWall() || containsBlockWall();
	}
	
	public void clear() {
		flags = NO_MASK;
		beeperCount = 0;
	}
	
	public Cell add(Cell cell) {
		flags|=cell.flags;
		if (beeperCount == INFINITY || cell.beeperCount == INFINITY) {
			beeperCount = INFINITY;
		} else {
			beeperCount+=cell.beeperCount;
		}
		
		return this;
	}
	
	public static Cell combine(Cell... cells) {
		
		Cell cell = new Cell();
		
		for (Cell a : cells) {
			cell.add(a);
		}
		
		return cell;
	}
	
}
