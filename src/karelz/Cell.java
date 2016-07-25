package karelz;

import java.util.Objects;

/**
 * A {@code Cell} object represents a single square in a world, and the fixed objects inside of it. These include walls and beepers, but not robots.
 * <br>Although there is nothing prohibiting it, by convention a cell should hold
 * (nothing) <b>OR</b> (any combination of a beeper pile, horizontal wall, or vertical wall), <b>OR</b> (only a block wall).
 */
public class Cell {
	
	public static final int INFINITY = -1;
	
	static final int NO_MASK = 0;
	static final int BEEPER_PILE_MASK = 1 << 0;
	static final int HORIZONTAL_WALL_MASK = 1 << 1;
	static final int VERTICAL_WALL_MASK = 1 << 2;
	static final int BLOCK_WALL_MASK = 1 << 3;
	
	int flags;
	int beepers;
	
	/**
	 * Instantiates a new cell.
	 *
	 * @param flags the bit flags to set, indicating what the cell contains
	 * @param beepers the number of beepers in this cell
	 */
	public Cell(int flags, int beepers) {
		this.flags = flags;
		this.beepers = beepers;
	}
	
	/**
	 * Instantiates a new cell with 0 beepers.
	 *
	 * @param flags the bit flags to set, indicating what the cell contains
	 */
	public Cell(int flags) {
		this(flags, 0);
	}
	
	/**
	 * Instantiates a new empty cell.
	 */
	public Cell() {
		this(NO_MASK, 0);
	}
	
	/**
	 * Instantiates a new cell given another cell.
	 *
	 * @param cell the cell to copy from
	 */
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
	
	/**
	 * Constructs a new cell with 1 beeper.
	 *
	 * @return the cell
	 */
	public static Cell newBeeperPile() {
		return newBeeperPile(1);
	}
	
	/**
	 * Constructs a new cell with a number of beepers.
	 *
	 * @param count number of beepers
	 * @return the cell
	 */
	public static Cell newBeeperPile(int count) {
		return new Cell(BEEPER_PILE_MASK, count);
	}
	
	/**
	 * Constructs a new cell with a horizontal wall.
	 *
	 * @return the cell
	 */
	public static Cell newHorizontalWall() {
		return new Cell(HORIZONTAL_WALL_MASK);
	}
	
	/**
	 * Constructs a new cell with a vertical wall.
	 *
	 * @return the cell
	 */
	public static Cell newVerticalWall() {
		return new Cell(VERTICAL_WALL_MASK);
	}
	
	/**
	 * Constructs a new cell with a block wall.
	 *
	 * @return the cell
	 */
	public static Cell newBlockWall() {
		return new Cell(BLOCK_WALL_MASK);
	}
	
	/**
	 * Returns {@code true} if this cell contains a given bit flag.
	 *
	 * @param flag the flag to check for
	 * @return {@code true} if this cell contains the flag.
	 */
	public boolean containsFlag(int flag) {
		return (flags & flag) == flag;
	}
	
	/**
	 * Returns {@code true} if this cell contains a beeper pile.
	 * Note that this doesn't necessarily mean there are a valid number of beepers in this pile (that is, not 0).
	 * Use {@link Cell#containsValidBeeperPile()} to check for a valid beeper pile
	 *
	 * @return {@code true} if this cell contains a beeper pile
	 * @see #containsValidBeeperPile()
	 */
	private boolean containsBeeperPile() {
		return containsFlag(BEEPER_PILE_MASK);
	}
	
	/**
	 * Returns {@code true} if this cell contains a valid beeper pile.
	 *
	 * @return {@code true} if this cell contains a valid beeper pile
	 */
	public boolean containsValidBeeperPile() {
		return containsBeeperPile() && (beepers > 0 || beepers == INFINITY);
	}
	
	/**
	 * Returns {@code true} if this cell contains a horizontal wall.
	 *
	 * @return {@code true} if this cell contains a horizontal wall
	 */
	public boolean containsHorizontalWall() {
		return containsFlag(HORIZONTAL_WALL_MASK);
	}
	
	/**
	 * Returns {@code true} if this cell contains a vertical wall.
	 *
	 * @return {@code true} if this cell contains a vertical wall
	 */
	public boolean containsVerticalWall() {
		return containsFlag(VERTICAL_WALL_MASK);
	}
	
	/**
	 * Returns {@code true} if this cell contains a block wall.
	 *
	 * @return {@code true} if this cell contains a block wall
	 */
	public boolean containsBlockWall() {
		return containsFlag(BLOCK_WALL_MASK);
	}
	
	/**
	 * Returns {@code true} if this cell contains a wall. This includes horizontal walls, vertical walls, and block walls
	 *
	 * @return {@code true} if this cell contains a wall
	 */
	public boolean containsWall() {
		return containsHorizontalWall() || containsVerticalWall() || containsBlockWall();
	}
	
	/**
	 * Gets the number of beepers in this cell.
	 *
	 * @return the number of beepers
	 */
	public int getBeeperCount() {
		return containsValidBeeperPile() ? beepers : 0;
	}
	
	/**
	 * Removes the beeper pile flag from this cell if it contains 0 beepers.
	 */
	public void clearBeeperPileIfEmpty() {
		if (containsBeeperPile() && beepers == 0) {
			unsetFlags(BEEPER_PILE_MASK);
		}
	}
	
	/**
	 * Removes everything from this cell.
	 */
	public void clear() {
		flags = NO_MASK;
		beepers = 0;
	}
	
	/**
	 * Sets the given flags on this cell.
	 *
	 * @param flags the flags to set
	 */
	public void setFlags(int flags) {
		this.flags|=flags;
	}
	
	/**
	 * Unset the given flags on this cell.
	 *
	 * @param flags the flags to unset
	 */
	public void unsetFlags(int flags) {
		this.flags &= ~flags;
	}
	
	/**
	 * Combines a given cell with this one.
	 * When adding beepers, if either of the cells contain infinite beepers, the sum is infinity. Otherwise, the beepers are added normally.
	 *
	 * @param cell the cell to combine with this cell
	 * @return this {@code Cell}
	 */
	public Cell add(Cell cell) {
		setFlags(cell.flags);
		if (beepers == INFINITY || cell.beepers == INFINITY) {
			beepers = INFINITY;
		} else {
			beepers+=cell.beepers;
		}
		
		return this;
	}
	
	/**
	 * Subtracts a given cell from this one.
	 * When subtracting beepers, if either of the cells contain infinite beepers or the difference would be negative,
	 * the result is zero. Otherwise, the beepers are subtracted normally.
	 *
	 * @param cell the cell to subtract from this cell
	 * @return this {@code Cell}
	 */
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
	
	/**
	 * Combines multiple cells and returns the result.
	 * When adding beepers, if any of the cells contain infinite beepers, the sum is infinity. Otherwise, the beepers are added normally.
	 *
	 * @param cells the cells to combine
	 * @return the combined cell
	 */
	public static Cell combine(Cell... cells) {
		
		Cell cell = new Cell();
		
		for (Cell a : cells) {
			cell.add(a);
		}
		
		return cell;
	}
}
