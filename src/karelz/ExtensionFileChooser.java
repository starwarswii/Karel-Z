package karelz;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * The {@code ExtensionFileChooser} is an improvement on the {@code JFileChooser}, adding support to append a file extension in save dialogs when one isn't provided and
 * showing a confirmation dialog when attempting to overwrite an existing file.
 * 
 * @see JFileChooser
 */
@SuppressWarnings("serial")
public class ExtensionFileChooser extends JFileChooser {
	
	String extension;
	
	/**
	 * Instantiates a new extension file chooser with the file extension to append when one isn't provided in save dialogs.
	 *
	 * @param extension the extension to append
	 */
	public ExtensionFileChooser(String extension) {
		this.extension = extension;
	}
	
	/**
	 * Instantiates a new extension file chooser with the file extension to append when one isn't provided in save dialogs
	 * and the directory to start in.
	 *
	 * @param currentDirectoryPath the starting directory
	 * @param extension the extension to append
	 */
	public ExtensionFileChooser(String currentDirectoryPath, String extension) {
		super(currentDirectoryPath);
		this.extension = extension;
	}
	
	/**
	 * Gets the selected file, appending the file extension if one isn't provided.
	 * 
	 * @see JFileChooser#getSelectedFile()
	 */
	public File getSelectedFile() {
		File selectedFile = super.getSelectedFile();
		if (selectedFile != null) {
			String name = selectedFile.getName();
			if (!name.contains(".")) {
				selectedFile = new File(selectedFile.getParentFile(), name+"."+extension);
			}
		}
		return selectedFile;
	}
	
	/**
	 * Approves the file selection, showing a confirmation dialog if attempting to save over an existing file.
	 * 
	 * @see JFileChooser#approveSelection()
	 */
	public void approveSelection() {
		if (getDialogType() == SAVE_DIALOG) {
			File selectedFile = getSelectedFile();
			if (selectedFile != null && selectedFile.exists()) {
				if (JOptionPane.showOptionDialog(this, "The file "+selectedFile.getName()+" already exists. Do you want to replace the existing file?", "Ovewrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Yes", "No"}, "No") == JOptionPane.NO_OPTION) {
					return;
				}	
			}
		}
		super.approveSelection();
	}
}
