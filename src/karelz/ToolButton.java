package karelz;

import javax.swing.JButton;

/**
 * A {@code ToolButton} is a type of {@code JButton} that is designed to display and trigger a {@code Tool}.
 * 
 * @see Tool
 * @see JButton
 */
@SuppressWarnings("serial")
public class ToolButton extends JButton {
	
	Tool tool;
	
	/**
	 * Instantiates a new tool button.
	 * Note that {@link #generateAndSetIcon(WorldColorCollection, int)} must be called at least once
	 * before attempting to display this button.
	 *
	 * @param tool the tool to display and trigger with this button.
	 */
	public ToolButton(Tool tool) {
		super();
		this.tool = tool;
		setToolTipText(this.tool.toolTip);
	}
	
	/**
	 * Generates and sets the tool icon by calling
	 * {@link Tool#generateIcon(WorldColorCollection, int)} on the tool and setting the button icon to the result.
	 *
	 * @param colorCollection the color collection
	 * @param beepers the number of beepers
	 */
	public void generateAndSetIcon(WorldColorCollection colorCollection, int beepers) {
		setIcon(tool.generateIcon(colorCollection, beepers));
	}
}
