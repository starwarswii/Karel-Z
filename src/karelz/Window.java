package karelz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.DefaultFormatter;

@SuppressWarnings("serial")
public class Window extends JFrame {//represents an object that displays and updates a world

	static final int CELL_SIZE = 40;
	static final int CELL_MARGIN = 2;
	static final int WINDOW_MARGIN = CELL_SIZE/2;
	static final int WALL_THICKNESS = CELL_MARGIN+1;
	static final int EDGE_WALL_MULTIPLIER = 1;
	static final int IMAGE_OFFSET = CELL_SIZE/7;

	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	static final String DIRECTORY = System.getProperty("user.home")+"/Desktop";//starting location for loading and saving worlds

	World world;
	PanAndZoomPanel panel;
	int delay;

	ToolButton currentToolButton;
	JComponent[] beeperComponents;
	
	boolean dirty;
	File saveFile;
	ExtensionFileChooser fileChooser;
	JMenuItem saveButton;

	public Window(World aWorld, int delay) {
		this(aWorld, delay, false);
	}

	public Window(World aWorld, int delay, boolean showWorldEditor) {
		super("Karel-Z");

		world = aWorld;
		this.delay = delay;

		//the +1's give a border of .5 cells, with 20 extra vertical pixels for the title bar
		setBounds(0, 0, Math.min((world.width+1)*CELL_SIZE+WINDOW_MARGIN, (int)SCREEN_SIZE.getWidth()), Math.min((world.height+1)*CELL_SIZE+WINDOW_MARGIN+20+(showWorldEditor ? 48 : 0), (int)SCREEN_SIZE.getHeight()));
		setLocationRelativeTo(null);
		setIconImage(Util.getImage("karel-on.png"));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				if (!saveWorld(false, true)) {//do you want to save changes?
					return;
				}
				
				System.gc();
				dispose();
				
				//used to stop "Exception while removing reference." print outs due to a sometime occurring Interrupted Exception
				PrintStream nullStream = new PrintStream(new OutputStream() {
					public void write(int b) throws IOException {}
					public void write(byte b[]) throws IOException {}
					public void write(byte b[], int off, int len) throws IOException {}
				});
				System.setErr(nullStream);
				System.setOut(nullStream);
				
				System.exit(0);
			}
		});

		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

		panel = new PanAndZoomPanel((g, mouse) -> {
			//no need to fill the background color as it will already be present due to panel.setBackground();

			//drawing grid lines
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setColor(world.lineColor);

			//drawing vertical grid lines
			for (int i = 0; i < world.width+1; i++) {
				g.drawLine((i*CELL_SIZE)+WINDOW_MARGIN, (world.height*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, WINDOW_MARGIN);
			}

			//drawing horizontal grid lines
			for (int i = 0; i < world.height+1; i++) {
				g.drawLine(WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, (world.width*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN);
			}

			//drawing edge walls
			g.setColor(world.wallColor);

			//drawing horizontal edge wall
			g.fillRect(WINDOW_MARGIN, WINDOW_MARGIN-((WALL_THICKNESS-1)/2), world.width*CELL_SIZE*EDGE_WALL_MULTIPLIER, WALL_THICKNESS);

			//drawing vertical edge wall
			g.fillRect(WINDOW_MARGIN-((WALL_THICKNESS-1)/2), WINDOW_MARGIN, WALL_THICKNESS, world.height*CELL_SIZE*EDGE_WALL_MULTIPLIER);

			//drawing cell objects
			world.map.forEach((point, cell) -> {

				//drawing beeper pile
				if (cell.containsValidBeeperPile()) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(world.beeperColor);

					g.fillOval((point.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, (point.y*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));

					//drawing beeper pile label
					if (cell.beepers > 1 || cell.beepers == Cell.INFINITY) {
						g.setColor(world.beeperLabelColor);

						Font font = new Font("Consolas", Font.PLAIN, 12);

						String text = cell.beepers > 1 ? Integer.toString(cell.beepers) : "\u221e";//infinity symbol

						//creates a font that fits in the desired area, then rotates it upside down, as everything is flipped on the y axis
						g.setFont(Util.sizeFontToFit(g, font, text, CELL_SIZE-(6*CELL_MARGIN), CELL_SIZE-(4*CELL_MARGIN)).deriveFont(AffineTransform.getScaleInstance(1, -1)));

						//get the bounds of the fitted string. note bounds.getHeight() is negative because it is flipped upside down
						Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

						g.drawString(text, (point.x*CELL_SIZE)+((CELL_SIZE-(int)bounds.getWidth())/2)+WINDOW_MARGIN, (point.y*CELL_SIZE)+(3*CELL_MARGIN)+((CELL_SIZE+(int)bounds.getHeight())/2)+WINDOW_MARGIN);	
					}
				}

				//by convention horizontal walls are drawn on the bottom of the occupied cell,
				//whereas vertical walls are drawn on the left of the occupied cell.
				//block walls take up the entire occupied cell

				//drawing walls
				if (cell.containsWall()) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					g.setColor(world.wallColor);

					if (cell.containsHorizontalWall()) {
						g.fillRect((point.x*CELL_SIZE)+WINDOW_MARGIN, (point.y*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), CELL_SIZE, WALL_THICKNESS);
					}

					if (cell.containsVerticalWall()) {
						g.fillRect((point.x*CELL_SIZE)+WINDOW_MARGIN-((WALL_THICKNESS-1)/2), (point.y*CELL_SIZE)+WINDOW_MARGIN, WALL_THICKNESS, CELL_SIZE);
					}

					if (cell.containsBlockWall()) {
						g.fillRect((point.x*CELL_SIZE)+WINDOW_MARGIN, (point.y*CELL_SIZE)+WINDOW_MARGIN, CELL_SIZE, CELL_SIZE);
					}
				}
			});

			//drawing robots
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			world.robots.forEach(robot -> g.drawImage(robot.getCurrentImage(), (robot.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, ((robot.y+1)*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN-IMAGE_OFFSET, CELL_SIZE-(2*CELL_MARGIN), -(CELL_SIZE-(2*CELL_MARGIN)), null));

			//drawing the selector
			if (currentToolButton != null && mouse.x/CELL_SIZE >= 0 && mouse.y/CELL_SIZE >= 0) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);//for all walls
				currentToolButton.tool.drawSelector(g, mouse);
			}

		});

		panel.setBackground(world.backgroundColor);
		add(panel, BorderLayout.CENTER);

		if (showWorldEditor) {
			JToolBar toolBar = new JToolBar();
			toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
			toolBar.setFloatable(false);

			ToolButton[] buttons = Tool.getButtons();
			currentToolButton = buttons[0];
			currentToolButton.setSelected(true);

			ActionListener toolButtonListener = e -> {
				panel.repaint();
				currentToolButton.setSelected(false);
				currentToolButton = (ToolButton)e.getSource();
				currentToolButton.setSelected(true);
				panel.panAndZoomListener.setEnabled(currentToolButton.tool == Tool.PAN_AND_ZOOM);
				for (JComponent a : beeperComponents) {
					a.setVisible(currentToolButton.tool == Tool.BEEPER_PILE);
				}
				//TODO maybe change how this works when adding robot tool if choose to, so something with the variable holding the currently active panel
			};

			for (ToolButton a : buttons) {
				a.generateAndSetIcon(world, 1);
				a.addActionListener(toolButtonListener);
				toolBar.add(a);
			}

			JCheckBox paintModeCheckBox = new JCheckBox("Paint Mode", true);
			toolBar.add(paintModeCheckBox);

			beeperComponents = new JComponent[4];

			//separator
			beeperComponents[0] = new Separator(new Dimension(10, 24));

			//spinner label
			beeperComponents[1] = new JLabel("Number of Beepers");

			//spinner
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
			spinner.setPreferredSize(new Dimension(40, 24));

			((DefaultFormatter)((JFormattedTextField)spinner.getEditor().getComponent(0)).getFormatter()).setCommitsOnValidEdit(true);

			//currentToolButton is known to be BEEPER_PILE
			spinner.addChangeListener(e -> currentToolButton.generateAndSetIcon(world, (int)spinner.getValue()));

			spinner.addMouseWheelListener(e -> {
				if (spinner.isEnabled() && (int)spinner.getValue()-e.getWheelRotation() > 0) {
					spinner.setValue((int)spinner.getValue()-e.getWheelRotation());
				}
			});
			beeperComponents[2] = spinner;

			//infinite checkbox
			JCheckBox infiniteCheckBox = new JCheckBox("Infinite");

			infiniteCheckBox.addItemListener(e -> {
				spinner.setEnabled(!infiniteCheckBox.isSelected());
				//currentToolButton is known to be BEEPER_PILE
				currentToolButton.generateAndSetIcon(world, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)spinner.getValue());
			});
			beeperComponents[3] = infiniteCheckBox;

			for (JComponent a : beeperComponents) {
				a.setVisible(false);
				toolBar.add(a);
			}

			add(toolBar, BorderLayout.PAGE_END);

			JFrame colorFrame = new JFrame("Karel-Z World Colors");
			colorFrame.setBounds(0, 0, 418, 240);
			colorFrame.setLocationRelativeTo(this);
			colorFrame.setIconImage(getIconImage());
			colorFrame.setResizable(false);

			JLabel wallLabel = new JLabel("Wall Color");
			JLabel beeperLabel = new JLabel("Beeper Color");
			JLabel beeperLabelLabel = new JLabel("Beeper Label Color");
			JLabel lineLabel = new JLabel("Line Color");
			JLabel backgroundLabel = new JLabel("Background Color");

			JPanel wallPanel = new JPanel();
			wallPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			wallPanel.setBackground(world.wallColor);

			JPanel beeperPanel = new JPanel();
			beeperPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			beeperPanel.setBackground(world.beeperColor);

			JPanel beeperLabelPanel = new JPanel();
			beeperLabelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			beeperLabelPanel.setBackground(world.beeperLabelColor);

			JPanel linePanel = new JPanel();
			linePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			linePanel.setBackground(world.lineColor);

			JPanel backgroundPanel = new JPanel();
			backgroundPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			backgroundPanel.setBackground(world.backgroundColor);

			JButton wallButton = new JButton("Choose Color");
			wallButton.setMargin(new Insets(2, 2, 2, 2));
			wallButton.addActionListener(e -> {
				Color color = JColorChooser.showDialog(colorFrame, "Choose a Wall Color", Color.BLACK);
				if (color != null) {
					wallPanel.setBackground(color);
				}
			});

			JButton beeperButton = new JButton("Choose Color");
			beeperButton.setMargin(new Insets(2, 2, 2, 2));
			beeperButton.addActionListener(e -> {
				Color color = JColorChooser.showDialog(colorFrame, "Choose a Beeper Color", Color.BLACK);
				if (color != null) {
					beeperPanel.setBackground(color);
				}
			});

			JButton beeperLabelButton = new JButton("Choose Color");
			beeperLabelButton.setMargin(new Insets(2, 2, 2, 2));
			beeperLabelButton.addActionListener(e -> {
				Color color = JColorChooser.showDialog(colorFrame, "Choose a Beeper Label Color", Color.WHITE);
				if (color != null) {
					beeperLabelPanel.setBackground(color);
				}
			});

			JButton lineButton = new JButton("Choose Color");
			lineButton.setMargin(new Insets(2, 2, 2, 2));
			lineButton.addActionListener(e -> {
				Color color = JColorChooser.showDialog(colorFrame, "Choose a Line Color", Color.BLACK);
				if (color != null) {
					linePanel.setBackground(color);
				}
			});

			JButton backgroundButton = new JButton("Choose Color");
			backgroundButton.setMargin(new Insets(2, 2, 2, 2));
			backgroundButton.addActionListener(e -> {
				Color color = JColorChooser.showDialog(colorFrame, "Choose a Background Color", Color.WHITE);
				if (color != null) {
					backgroundPanel.setBackground(color);
				}
			});

			JButton okButton = new JButton("OK");
			okButton.addActionListener(e -> {
				world.wallColor = wallPanel.getBackground();
				world.beeperColor = beeperPanel.getBackground();
				world.beeperLabelColor = beeperLabelPanel.getBackground();
				world.lineColor = linePanel.getBackground();
				world.backgroundColor = backgroundPanel.getBackground();
				panel.setBackground(world.backgroundColor);
				updateDirty(true);
				panel.repaint();

				for (Component a : toolBar.getComponents()) {
					if (a instanceof ToolButton) {
						((ToolButton)a).generateAndSetIcon(world, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)spinner.getValue());
					}
				}

				colorFrame.setVisible(false);
			});

			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(e -> colorFrame.setVisible(false));

			JButton resetButton = new JButton("Reset World Colors");
			resetButton.addActionListener(e -> {
				wallPanel.setBackground(Color.BLACK);
				beeperPanel.setBackground(Color.BLACK);
				beeperLabelPanel.setBackground(Color.WHITE);
				linePanel.setBackground(Color.BLACK);
				backgroundPanel.setBackground(Color.WHITE);
				//click the OK button
				okButton.doClick(0);
			});

			//generated code, don't touch
			GroupLayout groupLayout = new GroupLayout(colorFrame.getContentPane());
			groupLayout.setHorizontalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(wallLabel)
													.addComponent(beeperLabel)
													.addComponent(beeperLabelLabel)
													.addComponent(lineLabel)
													.addComponent(backgroundLabel, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
											.addGap(67)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
													.addComponent(linePanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
													.addComponent(beeperLabelPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
													.addComponent(beeperPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
													.addComponent(wallPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)))
									.addGroup(groupLayout.createSequentialGroup()
											.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(beeperButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
									.addComponent(beeperLabelButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
									.addComponent(lineButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
									.addComponent(backgroundButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
									.addComponent(wallButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(12))
					);
			groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(Alignment.TRAILING)
					.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(wallPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
									.addComponent(wallLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
									.addComponent(wallButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(beeperPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
									.addComponent(beeperLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
									.addComponent(beeperButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(groupLayout.createSequentialGroup()
											.addComponent(beeperLabelLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lineLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(backgroundLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
									.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(beeperLabelPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
													.addComponent(beeperLabelButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(linePanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
													.addComponent(lineButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(backgroundButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
													.addComponent(backgroundPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE, false)
									.addComponent(cancelButton)
									.addComponent(okButton)
									.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
					);
			colorFrame.getContentPane().setLayout(groupLayout);

			//top menu bar
			dirty = false;
			saveFile = null;

			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);

			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);

			JMenu worldMenu = new JMenu("World");
			menuBar.add(worldMenu);

			//TODO any world-change action marks it as unsaved things like load/new call a checkforSave method that brings up "do u want to save" dialog
			
			JMenuItem newButton = new JMenuItem("New");//TODO make a group-layouted world size popup with two spinners. is invoked on "new" and avalable through menu option
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (saveWorld(false, true)) {
						world = new World(10, 10);
						panel.resetPanAndZoom();
						panel.repaint();	
					}
				}
			});
			newButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			fileMenu.add(newButton);

			//TODO if the filetype is .kwld, convert to karelz format
			fileChooser = new ExtensionFileChooser(DIRECTORY, "kzw");
			fileChooser.setFileFilter(new FileNameExtensionFilter("Karel Worlds", "txt", "kzw", "kwld"));


			JMenuItem loadButton = new JMenuItem("Load");
			loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
			loadButton.addActionListener(e -> {//TODO on load update button image colors, may need to extract method. also should setbackground to new world color on load
				if (saveWorld(false, true) && fileChooser.showDialog(this, "Load") == JFileChooser.APPROVE_OPTION) {
					saveFile = fileChooser.getSelectedFile();
					world.loadWorld(saveFile);
					panel.resetPanAndZoom();
					updateDirty(false);
					panel.repaint();
				}
			});
			fileMenu.add(loadButton);

			saveButton = new JMenuItem("Save");
			saveButton.setEnabled(false);//is enabled as soon as a change is made
			saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
			saveButton.addActionListener(e -> saveWorld(false, false));
			fileMenu.add(saveButton);

			JMenuItem saveAsButton = new JMenuItem("Save As...");
			saveAsButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
			saveAsButton.addActionListener(e -> saveWorld(true, false));
			fileMenu.add(saveAsButton);

			JMenuItem exitButton = new JMenuItem("Exit");
			exitButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
			exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
			fileMenu.add(exitButton);

			JMenuItem editColorsButton = new JMenuItem("Edit Colors");
			editColorsButton.addActionListener(e -> colorFrame.setVisible(true));
			worldMenu.add(editColorsButton);

			JMenuItem worldDimensionsButton = new JMenuItem("World Dimensions");
			//TODO action listener
			worldMenu.add(worldDimensionsButton);
			
			//this listener handles object placing and deleting
			MouseAdapter listener = new MouseAdapter() {

				Point lastCellPointClicked = new Point(-1,-1);

				public Point getCellPoint(MouseEvent e) {
					Point2D.Float p = panel.panAndZoomListener.transformPoint(e.getPoint());
					return new Point((int)p.x/CELL_SIZE, (int)p.y/CELL_SIZE);
				}

				public void placeObject(MouseEvent e) {
					Point point = getCellPoint(e);
					if (point.x >= 0 && point.y >= 0) {
						Cell oldCell = new Cell(world.get(point));
						currentToolButton.tool.modifyWorld(world, point, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)spinner.getValue(), !SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e));
						if (!oldCell.equals(new Cell(world.get(point)))) {//if the cell was actually changed
							updateDirty(true);
						}
					}
					lastCellPointClicked = point;
					panel.repaint();
				}


				public void mouseMoved(MouseEvent e) {
					if (currentToolButton.tool != Tool.PAN_AND_ZOOM) {
						panel.repaint();
					}
				}

				public void mousePressed(MouseEvent e) {
					placeObject(e);
				}

				public void mouseDragged(MouseEvent e) {
					if (!lastCellPointClicked.equals(getCellPoint(e)) && (paintModeCheckBox.isSelected() || currentToolButton.tool == Tool.ERASER)) {
						placeObject(e);
					}
				}
			};

			panel.addMouseListener(listener);
			panel.addMouseMotionListener(listener);
			
		}
	}
	
	
	
//	public void setVisible(boolean b) {//TODO this is evil
//		new Timer().scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//				panel.repaint();
//			}
//			
//		}, 0, 500);
//		super.setVisible(b);
//	}
	
	public boolean saveWorld(boolean saveAs, boolean confirm) {//returns false if should stop any future actions, like loading or exiting
		if (dirty || saveAs) {
			if (confirm) {
				switch (JOptionPane.showOptionDialog(this, "Do you want to save the changes made?", "Karel-Z", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Save", "Don't Save", "Cancel"}, "Save")) {
				case JOptionPane.NO_OPTION: return true;//don't save, but continue whatever future actions
				case JOptionPane.CANCEL_OPTION: return false;//don't save, and cancel future actions
				}
			}

			if (saveFile == null || saveAs) {
				if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					saveFile = fileChooser.getSelectedFile();
				} else {
					return false;
				}
			}
			world.saveWorld(saveFile);
			updateDirty(false);
		}
		return true;
	}

	public void updateTitle() {//\u2013 is EN_DASH
		setTitle("Karel-Z"+(saveFile != null ? " \u2013 "+saveFile.getName() : "")+(dirty ? "*" : ""));
	}

	public void updateDirty(boolean value) {
		dirty = value;
		saveButton.setEnabled(value);
		updateTitle();
	}

	public void start() {//starts all the bots

		ArrayList<Robot> runningRobots = new ArrayList<Robot>(world.robots);

		runningRobots.forEach(Robot::launchThread);

		if (delay > 0) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				public void run() {
					for (int i = 0; i < runningRobots.size(); i++) {
						Robot robot = runningRobots.get(i);
						if (robot.threadIsActive) {
							robot.step();
						} else {
							runningRobots.remove(i);
							i--;
						}
					}
					panel.repaint();

					if (runningRobots.isEmpty()) {
						timer.cancel();
					}
				}

			}, 0, delay);
		} else {//needed as timer won't accept delay 0. this below just makes it run as fast as possible
			Thread thread = new Thread(() -> {
				while (true) {
					for (int i = 0; i < runningRobots.size(); i++) {
						Robot robot = runningRobots.get(i);
						if (robot.threadIsActive) {
							robot.step();
						} else {
							runningRobots.remove(i);
							i--;
						}
					}
					panel.repaint();

					if (runningRobots.isEmpty()) {
						break;
					}
				}
			});
			thread.start();
		}
	}

}
