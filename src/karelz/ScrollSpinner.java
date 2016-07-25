package karelz;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.text.DefaultFormatter;

/**
 * The {@code ScrollSpinner} is an improvement on the {@code JSpinner},
 * adding support for changing values via scrolling the mouse scroll wheel over the spinner, as well as other small tweaks.
 * These tweaks include allowing easy access to changing the {@code commitsOnValidEdit} property and removing comma number formatting.
 * 
 * @see JSpinner
 */
@SuppressWarnings("serial")
public class ScrollSpinner extends JSpinner {
	
	/**
	 * Instantiates a new scroll spinner with given model and the {@code commitsOnValidEdit} property set to false.
	 *
	 * @param model the model
	 */
	public ScrollSpinner(SpinnerModel model) {
		this(model, false);
	}
	
	/**
	 * Instantiates a new scroll spinner.
	 *
	 * @param model the given model
	 * @param commitsOnValidEdit the {@code commitsOnValidEdit} property
	 */
	public ScrollSpinner(SpinnerModel model, boolean commitsOnValidEdit) {
		super(model);
		
		//disables comma separation e.g. 1,000
		setEditor(new JSpinner.NumberEditor(this, "#"));
		
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
}
