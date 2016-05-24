package karelz;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ToolButton extends JButton {

	Tool tool;

	public ToolButton(Tool tool) {//note you must generateIcon before attempting to display
		super();
		this.tool = tool;
		setToolTipText(this.tool.toolTip);
	}

	public void generateAndSetIcon(World world, int beepers) {
		setIcon(tool.generateIcon(world, beepers));
	}


}
