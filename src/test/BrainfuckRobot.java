package test;

import static karelz.Cell.INFINITY;
import static karelz.Direction.*;
import static karelz.Util.path;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import karelz.*;

public class BrainfuckRobot extends SuperRobot {

	static HashMap<Character,Integer> map = getMap();

	public static HashMap<Character,Integer> getMap() {
		HashMap<Character,Integer> map = new HashMap<Character,Integer>(8);
		char[] chars = new char[] {'+','-','<','>',',','.','[',']'};
		for (int i = 0; i < chars.length; i++) {
			map.put(chars[i], i+1);
		}
		//System.out.println(map);
		return map;
	}

	public BrainfuckRobot(int x, int y, Direction direction) {
		super(x, y, direction);
	}

	public BrainfuckRobot(int x, int y, Direction direction, int beepers) {
		super(x, y, direction, beepers);
	}

	public BrainfuckRobot(int x, int y, Direction direction, Color color) {
		super(x, y, direction, color);
	}

	public BrainfuckRobot(int x, int y, Direction direction, int beepers, Color color) {
		super(x, y, direction, beepers, color);
	}

	public void goToWall() throws EndTaskException {
		while (frontIsClear()) {
			move();
		}
	}

	public void goToBeeperStock() throws EndTaskException {
		goToOrigin();
		turnAround();
		iterate(9, () -> move());
	}

	public void pickBeeperSafe() throws EndTaskException {
		if (nextToABeeper()) {
			pickBeeper();
		}
	}

	public void readInstructionAtPointer() throws EndTaskException {
		goToInstructionAtPointer();
		readInstruction();
	}

	public void readInstruction() throws EndTaskException {
		if(nextToABeeper()) {
			pickBeeper();
			if (nextToABeeper()) {
				pickBeeper();
				if (nextToABeeper()) {
					pickBeeper();
					if (nextToABeeper()) {
						pickBeeper();
						if (nextToABeeper()) {
							pickBeeper();
							if (nextToABeeper()) {
								pickBeeper();
								if (nextToABeeper()) {
									pickBeeper();
									if (nextToABeeper()) {//8 beepers "]"
										pickBeeper();
										putAllBeepers();
										goToTapeAtPointer();
										if (nextToABeeper()) {//if value is not 0, enter reverse loop mode, otherwise continue as normal
											incrementLoopCounter();
											while (nextToABeeper()) {//as we are by the counter, this means "while (counter > 0)"
												moveInstructionPointerBackward();
												goToInstructionAtPointer();
												iterate(6, () -> pickBeeperSafe());
												if (nextToABeeper()) {
													pickBeeper();
													if (nextToABeeper()) {//8 beepers "]"
														putAllBeepers();
														incrementLoopCounter();
													} else {//7 beepers "["
														putAllBeepers();
														decrementLoopCounter();
													}
												} else {//1-6 beepers
													putAllBeepers();
												}
												goToLoopCounter();//important so that the while condition can be checked
											}
										}

									} else {//7 beepers "["
										putAllBeepers();
										goToTapeAtPointer();
										if (notNextToABeeper()) {//if value is 0, enter loop mode, otherwise continue as normal
											incrementLoopCounter();
											while (nextToABeeper()) {//as we are by the counter, this means "while (counter > 0)"
												moveInstructionPointerForward();
												goToInstructionAtPointer();
												if (nextToABeeper()) {
													iterate(6, () -> pickBeeperSafe());
													if (nextToABeeper()) {
														pickBeeper();
														if (nextToABeeper()) {//8 beepers "]"
															putAllBeepers();
															decrementLoopCounter();
														} else {//7 beepers "["
															putAllBeepers();
															incrementLoopCounter();
														}
													} else {//1-6 beepers
														putAllBeepers();
													}
												} else {
													//if we reach end of program when matching brackets, they are unmatched, so we crash
													System.out.println("Error: Unmatched Brackets");
													forceCrash("Error: Unmatched Brackets");
												}
												goToLoopCounter();//important so that the while condition can be checked
											}
										}
									}
								} else {//6 beepers "."
									putAllBeepers();
									goToTapeAtPointer();
									if (nextToABeeper()) {//only output if not 0
										while (nextToABeeper()) {//checks condition at start of every loop
											pickBeeper();
											move();//we are facing down and one spot below is the work area
											putBeeper();
											goToBeeperStock();
											pickBeeper();
											goToOutputAtPointer();
											putBeeper();
											goToTapeAtPointer();//important so that the while condition can be checked
										}
										move();//move back to the work area
										pickAllBeepers();
										turnAround();
										move();//now back on the tape
										putAllBeepers();
										moveOutputPointerForward();
									}
								}
							} else {//5 beepers ","
								putAllBeepers();
								goToInputAtPointer();
								if (nextToABeeper()) {//only load from input if not 0
									goToTapeAtPointer();
									if (nextToABeeper()) {//clear the spot if it's not 0
										pickAllBeepers();
										goToBeeperStock();
										putAllBeepers();
									}
									goToInputAtPointer();
									pickAllBeepers();
									goToTapeAtPointer();
									putAllBeepers();
									moveInputPointerForward();
								}
							}
						} else {//4 beepers ">"
							putAllBeepers();
							moveTapePointerForward();
						}
					} else {//3 beepers "<"
						putAllBeepers();
						moveTapePointerBackward();
					}
				} else {//2 beepers "-"
					putAllBeepers();
					goToTapeAtPointer();
					if (nextToABeeper()) {
						pickBeeper();
						goToBeeperStock();
						putBeeper();
					} else {//there is 0 beepers, need to wrap over to 255
						goToBeeperStock();
						iterate(255, () -> pickBeeper());
						goToTapeAtPointer();
						putAllBeepers();
					}
				}
			} else {//1 beeper "+"
				putAllBeepers();
				goToTapeAtPointer();
				if (nextToABeeper()) {//speeds up writing if there's no beeper
					iterate(254, () -> pickBeeperSafe());
					if (nextToABeeper()) {//there was 255 beepers
						pickBeeper();
						goToBeeperStock();
						while (hasBeepers()) {
							putBeeper();
						}
					} else {
						putAllBeepers();
						goToBeeperStock();
						pickBeeper();
						goToTapeAtPointer();
						putBeeper();
					}
				} else {
					goToBeeperStock();
					pickBeeper();
					goToTapeAtPointer();
					putBeeper();
				}
			}
			moveInstructionPointerForward();
		} else {//0 beepers
			finishTask();
		}
	}

	public void moveInstructionPointerForward() throws EndTaskException {
		goToInstructionPointer();
		movePointerForward();
	}

	public void moveInstructionPointerBackward() throws EndTaskException {
		goToInstructionPointer();
		movePointerBackward();
	}

	public void goToInstructionPointer() throws EndTaskException {
		goToOrigin();
		turnAround();
		move();//lane 1
		turnRight();
		goToPointer();
	}

	public void goToInstructionAtPointer() throws EndTaskException {
		goToInstructionPointer();
		turnRight();
		move();
	}

	public void moveTapePointerForward() throws EndTaskException {
		goToTapePointer();
		movePointerForward();
	}

	public void moveTapePointerBackward() throws EndTaskException {
		goToTapePointer();
		movePointerBackward();
	}

	public void goToTapePointer() throws EndTaskException {
		goToOrigin();
		turnAround();
		move();//lane 4
		move();
		move();
		move();
		turnRight();
		goToPointer();
	}

	public void goToTapeAtPointer() throws EndTaskException {
		goToTapePointer();
		turnRight();
		move();
	}


	public void goToInputPointer() throws EndTaskException {
		goToOrigin();
		turnAround();
		iterate(6, () -> move());//lane 6
		turnRight();
		goToPointer();
	}

	public void goToInputAtPointer() throws EndTaskException {
		goToInputPointer();
		turnRight();
		move();
	}

	public void moveInputPointerForward() throws EndTaskException {
		goToInputPointer();
		movePointerForward();
	}


	public void goToOutputPointer() throws EndTaskException {
		goToOrigin();
		turnAround();
		iterate(8, () -> move());//lane 8
		turnRight();
		goToPointer();
	}

	public void goToOutputAtPointer() throws EndTaskException {
		goToOutputPointer();
		turnRight();
		move();
	}

	public void moveOutputPointerForward() throws EndTaskException {
		goToOutputPointer();
		movePointerForward();
	}

	public void goToOrigin() throws EndTaskException {
		faceLeft();
		goToWall();
		turnLeft();
		goToWall();
	}

	public void incrementLoopCounter() throws EndTaskException {//finishes at loop counter
		goToBeeperStock();
		pickBeeper();
		goToLoopCounter();
		putBeeper();
	}

	public void decrementLoopCounter() throws EndTaskException {//does not finish at loop counter
		goToLoopCounter();
		pickBeeper();
		goToBeeperStock();
		putBeeper();
	}

	public void goToLoopCounter() throws EndTaskException {//it's in the first slot of the work area
		goToOrigin();
		turnAround();
		move();//lane 2
		move();
	}

	public void goToPointer() throws EndTaskException {//for going to any pointer
		while (notNextToABeeper()) {
			move();
		}
	}

	public void movePointerForward() throws EndTaskException {//for moving any pointer
		pickBeeper();
		move();
		putBeeper();
	}

	public void movePointerBackward() throws EndTaskException {//for moving any pointer backward
		pickBeeper();
		turnAround();
		move();
		putBeeper();
	}

	public void pickAllBeepers() throws EndTaskException {
		while (nextToABeeper()) {
			pickBeeper();
		}
	}

	public void putAllBeepers() throws EndTaskException {
		while (hasBeepers()) {
			putBeeper();
		}
	}

	public void faceUp() {
		while (notFacingUp()) {
			turnLeft();
		}
	}

	public void faceRight() {
		while (notFacingRight()) {
			turnLeft();
		}
	}

	public void faceDown() {
		while (notFacingDown()) {
			turnLeft();
		}
	}

	public void faceLeft() {
		while (notFacingLeft()) {
			turnLeft();
		}
	}

	public void finishTask() throws EndTaskException {
		System.out.println("Finished in "+getStepCount()+(getStepCount() == 1 ? " step" : " steps"));
		printOutput();
		printEscapedOutput();
		printRawOutput();
		System.out.println("--------------------------------------------------");
		turnOff();
	}

	public void task() throws EndTaskException {
		while (running()) {
			readInstructionAtPointer();
		}
	}

	public static void main(String[] args) {
		World world = new World(path("brainfuck-helloworld.kzw")).add(new BrainfuckRobot(0, 0, RIGHT));
		Window window = new Window(world, 1);
		window.setVisible(true);
		Util.sleep(250);

		window.setStepOverdrive(10000).runTest(500, world);
		window.runTest(500, generateWorld("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++."));//hello world
		window.setStepOverdrive(10000).runTest(500, generateWorld(",[.[-],]","ABCDEFG"));//cat
		window.setStepOverdrive(1).runTest(500, generateWorld(",++.", "A"));//prints C
		window.setStepOverdrive(100).runTest(500, generateWorld("+.>++.>-."));// raw [1, 2, 255]
	}

	public static World generateWorld(String program, String input) {
		World world = new World((int)(program.length()*1.5)+1, 10);

		program = program.replaceAll("[^\\+\\-><\\.,\\[\\]]+", "");

		for (int i = 0; i < program.length(); i++) {//program
			world.add(i, 0, Cell.newBeeperPile(map.get(program.charAt(i))));
		}

		for (int i = 0; i < input.length(); i++) {//input
			if (input.charAt(i) <= 255) {
				world.add(i, 5, Cell.newBeeperPile(input.charAt(i)));
			}
		}

		world.add(0, 1, Cell.newBeeperPile());//program read head
		world.add(0, 4, Cell.newBeeperPile());//tape read head
		world.add(0, 6, Cell.newBeeperPile());//input read head
		world.add(0, 8, Cell.newBeeperPile());//output write head
		world.add(0, 9, Cell.newBeeperPile(INFINITY));//infinite beeper stock

		return world.add(new BrainfuckRobot(0, 0, RIGHT));
	}

	public static World generateWorld(String program) {
		return generateWorld(program, "");
	}

	public void printOutput() {
		StringBuilder output = new StringBuilder(getWorld().getWidth());

		for (int i = 0; getWorld().get(i, 7).getBeeperCount() > 0 && i < getWorld().getWidth(); i++) {//read output
			output.append((char)getWorld().get(i, 7).getBeeperCount());
		}
		if (output.length() > 0) {
			System.out.println("Output: \""+output.toString()+"\"");
		}
	}

	public void printEscapedOutput() {
		StringBuilder output = new StringBuilder(getWorld().getWidth());

		for (int i = 0; getWorld().get(i, 7).getBeeperCount() > 0 && i < getWorld().getWidth(); i++) {//read output
			output.append((char)getWorld().get(i, 7).getBeeperCount());
		}
		if (output.length() > 0) {
			System.out.println("Escaped Output: \""+escape(output.toString())+"\"");
		}
	}

	public void printRawOutput() {
		ArrayList<Integer> output = new ArrayList<Integer>(getWorld().getWidth());

		for (int i = 0; getWorld().get(i, 7).getBeeperCount() > 0 && i < getWorld().getWidth(); i++) {//read output
			output.add(getWorld().get(i, 7).getBeeperCount());
		}
		if (output.size() > 0) {
			System.out.println("Raw Output: "+output.toString());
		}
	}

	/**
	 * Based on <a href=
	 * "http://commons.apache.org/proper/commons-lang/javadocs/api-2.6/src-html/org/apache/commons/lang/StringEscapeUtils.html#line.181"
	 * >StringEscapeUtils</a> from <a href="http://apache.org">apache.org</a>
	 */
	public static String escape(String string) {

		if (string == null) {
			return null;
		}

		StringBuilder output = new StringBuilder(string.length());

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			String hex = Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);

			// handle unicode
			if (ch > 0xfff) {
				output.append("\\u").append(hex);
			} else if (ch > 0xff) {
				output.append("\\u0").append(hex);
			} else if (ch > 0x7f) {
				output.append("\\u00").append(hex);
			} else if (ch < 32) {
				switch (ch) {
				case '\b':
					output.append("\\b");
					break;
				case '\n':
					output.append("\\n");
					break;
				case '\t':
					output.append("\\t");
					break;
				case '\f':
					output.append("\\f");
					break;
				case '\r':
					output.append("\\r");
					break;
				default:
					if (ch > 0xf) {
						output.append("\\u00").append(hex);
					} else {
						output.append("\\u000").append(hex);
					}
					break;
				}
			} else {
				switch (ch) {
				case '"':
					output.append("\\\"");
					break;
				case '\\':
					output.append("\\\\");
					break;
				default:
					output.append(ch);
					break;
				}
			}
		}
		return output.toString();
	}
}
