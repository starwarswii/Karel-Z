package karelz;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ToolButton extends JButton {

	Tool tool;

	public ToolButton(Tool tool) {//note you must generateAndSetIcon before attempting to display
		super();
		this.tool = tool;
		setToolTipText(this.tool.toolTip);
	}

	public void generateAndSetIcon(WorldColorCollection colorCollection, int beepers) {
		setIcon(tool.generateIcon(colorCollection, beepers));
	}


}
