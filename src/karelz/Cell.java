package karelz;

import java.util.Objects;

//although there is nothing prohibiting it, by convention a cell should hold (nothing) OR (any combination of beeper pile, horizontal wall, and vertical wall), OR (only block wall)
public class Cell {

	public static final int INFINITY = -1;

	static final int NO_MASK = 0;
	static final int BEEPER_PILE_MASK = 1 << 0;
	static final int HORIZONTAL_WALL_MASK = 1 << 1;
	static final int VERTICAL_WALL_MASK = 1 << 2;
	static final int BLOCK_WALL_MASK = 1 << 3;

	int flags;
	int beepers;

	public Cell(int flags, int beepers) {
		this.flags = flags;
		this.beepers = beepers;
	}

	public Cell(int flags) {
		this(flags, 0);
	}

	public Cell() {
		this(NO_MASK, 0);
	}

	public Cell(Cell cell) {
		this(cell.flags, cell.beepers);
	}

	public int hashCode() {
		return Objects.hash(flags, beepers);
	}

	public boolean equals(Object object) {
		return object instanceof Cell && flags == ((Cell)object).flags && beepers == ((Cell)object).beepers;
	}

	public String toString() {
		return "Flags: "+flags+", Beepers: "+beepers;
	}

	public static Cell newBeeperPile() {
		return newBeeperPile(1);
	}

	public static Cell newBeeperPile(int count) {
		return new Cell(BEEPER_PILE_MASK, count);
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

	//note that this doesn't indicate that there is a valid number of beepers in this pile ie not 0
	private boolean containsBeeperPile() {
		return containsFlag(BEEPER_PILE_MASK);
	}

	public boolean containsValidBeeperPile() {
		return containsBeeperPile() && (beepers > 0 || beepers == INFINITY);
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

	public int getBeeperCount() {
		return containsValidBeeperPile() ? beepers : 0;
	}

	public void clearBeeperPileIfEmpty() {
		if (containsBeeperPile() && beepers == 0) {
			unsetFlags(BEEPER_PILE_MASK);
		}
	}

	public void clear() {
		flags = NO_MASK;
		beepers = 0;
	}

	public void setFlags(int flags) {
		this.flags|=flags;
	}

	public void unsetFlags(int flags) {
		this.flags &= ~flags;
	}

	public Cell add(Cell cell) {
		setFlags(cell.flags);
		if (beepers == INFINITY || cell.beepers == INFINITY) {
			beepers = INFINITY;
		} else {
			beepers+=cell.beepers;
		}

		return this;
	}

	public Cell remove(Cell cell) {
		unsetFlags(cell.flags);
		if (beepers > 0 && cell.containsValidBeeperPile() && cell.beepers != INFINITY) {
			beepers-=cell.beepers;
			if (beepers > 0) {
				setFlags(BEEPER_PILE_MASK);
			} else {
				beepers = 0;
			}
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
