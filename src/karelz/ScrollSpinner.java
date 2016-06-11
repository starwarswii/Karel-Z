package karelz;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.text.DefaultFormatter;

@SuppressWarnings("serial")
public class ScrollSpinner extends JSpinner {

	public ScrollSpinner(SpinnerModel model, boolean commitsOnValidEdit) {
		super(model);

		//makes any edit to the spinner take effect immediately if commitsOnValidEdit is true
		((DefaultFormatter)((JFormattedTextField)getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(commitsOnValidEdit);

		addMouseWheelListener(e -> {
			if (isEnabled()) {
				if (e.getWheelRotation() < 0 && getNextValue() != null) {
					setValue(getNextValue());
				} 
				if (e.getWheelRotation() > 0 && getPreviousValue() != null) {
					setValue(getPreviousValue());
				}
			}
		});
	}

	public ScrollSpinner(SpinnerModel model) {
		this(model, false);
	}
}