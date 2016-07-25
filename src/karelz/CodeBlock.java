package karelz;

/**
 * {@code CodeBlocks} are used to allow arbitrary Karel robot instructions.
 * 
 * @see Robot#iterate(int, CodeBlock)
 */
public interface CodeBlock {
	
	/**
	 * Executes this code.
	 *
	 * @throws EndTaskException when the robot task is terminated either by a crash or the program ending
	 * @see EndTaskException
	 */
	public void execute() throws EndTaskException;
	
}
