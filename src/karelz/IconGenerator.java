package karelz;

import javax.swing.ImageIcon;

/**
 * {@code IconGenerators} are objects that dynamically create an icon given information about a world,
 * specifically a {@code WorldColorCollection} and a number of beepers.
 * <br>This is used by the {@code Tool} objects to create their icons based on the world color.
 * 
 * @see Tool
 */
public interface IconGenerator {
	
	/**
	 * Generates an {@code ImageIcon} given a {@code WorldColorCollection} and a number of beepers.
	 *
	 * @param colorCollection the color collection
	 * @param beepers the number of beepers
	 * @return the generated icon
	 */
	public ImageIcon generate(WorldColorCollection colorCollection, int beepers);
	
}
