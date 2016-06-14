package karelz;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ExtensionFileChooser extends JFileChooser {

	String extension;

	public ExtensionFileChooser(String extension) {
		super();
		this.extension = extension;
	}

	public ExtensionFileChooser(String currentDirectoryPath, String extension) {
		super(currentDirectoryPath);
		this.extension = extension;
	}

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
