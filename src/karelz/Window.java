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
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Window extends JFrame {//represents an object that displays and updates a world

	static final int CELL_SIZE = 40;
	static final int CELL_MARGIN = 2;
	static final int WINDOW_MARGIN = CELL_SIZE/2;
	static final int WALL_THICKNESS = CELL_MARGIN+1;
	static final int EDGE_WALL_MULTIPLIER = 1;
	static final int IMAGE_OFFSET = CELL_SIZE/7;

	static final ImageIcon PLAY_ICON = Util.getIcon("play.png");
	static final ImageIcon PAUSE_ICON = Util.getIcon("pause.png");

	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	static final String DIRECTORY = System.getProperty("user.home")+"/Desktop";//starting location for loading and saving worlds

	World world;
	PanAndZoomPanel panel;
	int delay;
	boolean showEditorTools;
	boolean showPlaybackTools;
	long autoplayAfter;

	Timer refreshTimer;
	TimerTask refreshTask;

	volatile boolean playing;
	ArrayList<Robot> runningRobots;
	Timer robotTimer;
	TimerTask robotTask;

	JButton playPauseButton;
	JButton stepButton;
	JLabel delayLabel;
	ScrollSpinner delaySpinner;

	JToolBar toolBar;

	ToolButton currentToolButton;
	JComponent[] beeperComponents;
	JCheckBox infiniteCheckBox;
	ScrollSpinner beeperSpinner;

	JPanel wallPanel;
	JPanel beeperPanel;
	JPanel beeperLabelPanel;
	JPanel linePanel;
	JPanel backgroundPanel;

	boolean dirty;
	boolean newWorld;
	File saveFile;
	ExtensionFileChooser fileChooser;
	JMenuItem saveButton;

	public Window() {
		this(new World());
	}

	public Window(World aWorld) {
		this(aWorld, 100);
	}

	public Window(World aWorld, int delay) {
		this(aWorld, delay, false);
	}

	public Window(World aWorld, boolean showEditorTools) {
		this(aWorld, 100, showEditorTools);
	}

	public Window(World aWorld, int delay, boolean showEditorTools) {
		this(aWorld, delay, showEditorTools, false);
	}

	public Window(World aWorld, int delay, boolean showEditorTools, boolean showPlaybackTools) {
		this(aWorld, delay, showEditorTools, showPlaybackTools, -1);
	}

	public Window(World aWorld, int delay, boolean showEditorTools, boolean showPlaybackTools, long autoplayAfter) {
		super("Karel-Z");

		world = aWorld;
		this.delay = Math.max(delay, 1);
		this.showEditorTools = showEditorTools;
		this.showPlaybackTools = showPlaybackTools;
		this.autoplayAfter = autoplayAfter;
		playing = false;

		//the +1's give a border of .5 cells, with 20 extra vertical pixels for the title bar
		setBounds(0, 0, Math.min((world.width+1)*CELL_SIZE+WINDOW_MARGIN, (int)SCREEN_SIZE.getWidth()), Math.min((world.height+1)*CELL_SIZE+WINDOW_MARGIN+20+(showEditorTools ? 48 : 0), (int)SCREEN_SIZE.getHeight()));
		setLocationRelativeTo(null);
		setIconImage(Util.getImage("icon.png"));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				if (!saveWorld(false, true)) {//do you want to save changes?
					return;
				}

				System.gc();
				dispose();//TODO maybe make all other pop up windows instance variables and dispose of them here too?

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
			g.setColor(world.colorCollection.lineColor);

			//drawing vertical grid lines
			for (int i = 0; i < world.width+1; i++) {
				g.drawLine((i*CELL_SIZE)+WINDOW_MARGIN, (world.height*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, WINDOW_MARGIN);
			}

			//drawing horizontal grid lines
			for (int i = 0; i < world.height+1; i++) {
				g.drawLine(WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN, (world.width*CELL_SIZE)-1+WINDOW_MARGIN, (i*CELL_SIZE)+WINDOW_MARGIN);
			}

			//drawing edge walls
			g.setColor(world.colorCollection.wallColor);

			//drawing horizontal edge wall
			g.fillRect(WINDOW_MARGIN, WINDOW_MARGIN-((WALL_THICKNESS-1)/2), world.width*CELL_SIZE*EDGE_WALL_MULTIPLIER, WALL_THICKNESS);

			//drawing vertical edge wall
			g.fillRect(WINDOW_MARGIN-((WALL_THICKNESS-1)/2), WINDOW_MARGIN, WALL_THICKNESS, world.height*CELL_SIZE*EDGE_WALL_MULTIPLIER);

			//drawing cell objects
			world.map.forEach((point, cell) -> {

				//drawing beeper pile
				if (cell.containsValidBeeperPile()) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(world.colorCollection.beeperColor);

					g.fillOval((point.x*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, (point.y*CELL_SIZE)+CELL_MARGIN+WINDOW_MARGIN, CELL_SIZE-(2*CELL_MARGIN), CELL_SIZE-(2*CELL_MARGIN));

					//drawing beeper pile label
					if (cell.beepers > 1 || cell.beepers == Cell.INFINITY) {
						g.setColor(world.colorCollection.beeperLabelColor);

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
					g.setColor(world.colorCollection.wallColor);

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

		panel.setBackground(world.colorCollection.backgroundColor);
		add(panel, BorderLayout.CENTER);

		if (showEditorTools || showPlaybackTools) {

			toolBar = new JToolBar();
			toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
			toolBar.setFloatable(false);
			add(toolBar, BorderLayout.PAGE_END);

			if (showPlaybackTools) {

				stepButton = new JButton(Util.getIcon("step.png"));
				stepButton.addActionListener(e -> step());

				delayLabel = new JLabel("Tick Delay");

				delaySpinner = new ScrollSpinner(new SpinnerNumberModel(this.delay, 1, null, 1), true);
				delaySpinner.setPreferredSize(new Dimension(46, 24));
				delaySpinner.addChangeListener(e -> this.delay = (int)delaySpinner.getValue());

				playPauseButton = new JButton(PLAY_ICON);
				playPauseButton.addActionListener(e -> playPause());

				toolBar.add(playPauseButton);
				toolBar.add(stepButton);
				toolBar.add(delayLabel);
				toolBar.add(delaySpinner);

				if (showEditorTools) {
					toolBar.add(new Separator(new Dimension(10, 24)));	
				}
			}

			if (showEditorTools) {

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
					a.generateAndSetIcon(world.colorCollection, 1);
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

				beeperSpinner = new ScrollSpinner(new SpinnerNumberModel(1, 1, null, 1), true);
				beeperSpinner.setPreferredSize(new Dimension(40, 24));

				//currentToolButton is known to be BEEPER_PILE
				beeperSpinner.addChangeListener(e -> currentToolButton.generateAndSetIcon(world.colorCollection, (int)beeperSpinner.getValue()));

				beeperComponents[2] = beeperSpinner;

				infiniteCheckBox = new JCheckBox("Infinite");

				infiniteCheckBox.addItemListener(e -> {
					beeperSpinner.setEnabled(!infiniteCheckBox.isSelected());
					//currentToolButton is known to be BEEPER_PILE
					currentToolButton.generateAndSetIcon(world.colorCollection, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)beeperSpinner.getValue());
				});
				beeperComponents[3] = infiniteCheckBox;

				for (JComponent a : beeperComponents) {
					a.setVisible(false);
					toolBar.add(a);
				}

				//Color Window
				JDialog colorDialog = new JDialog(this, "World Colors", true);
				colorDialog.setBounds(0, 0, 418, 240);
				colorDialog.setLocationRelativeTo(this);
				colorDialog.setIconImage(getIconImage());
				colorDialog.setResizable(false);

				JLabel wallLabel = new JLabel("Wall Color");
				JLabel beeperLabel = new JLabel("Beeper Color");
				JLabel beeperLabelLabel = new JLabel("Beeper Label Color");
				JLabel lineLabel = new JLabel("Line Color");
				JLabel backgroundLabel = new JLabel("Background Color");

				wallPanel = new JPanel();
				wallPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				wallPanel.setBackground(world.colorCollection.wallColor);

				beeperPanel = new JPanel();
				beeperPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				beeperPanel.setBackground(world.colorCollection.beeperColor);

				beeperLabelPanel = new JPanel();
				beeperLabelPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				beeperLabelPanel.setBackground(world.colorCollection.beeperLabelColor);

				linePanel = new JPanel();
				linePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				linePanel.setBackground(world.colorCollection.lineColor);

				backgroundPanel = new JPanel();
				backgroundPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				backgroundPanel.setBackground(world.colorCollection.backgroundColor);

				JButton wallButton = new JButton("Choose Color");
				wallButton.setMargin(new Insets(2, 2, 2, 2));
				wallButton.addActionListener(e -> {
					Color color = JColorChooser.showDialog(colorDialog, "Choose a Wall Color", Color.BLACK);
					if (color != null) {
						wallPanel.setBackground(color);
					}
				});

				JButton beeperButton = new JButton("Choose Color");
				beeperButton.setMargin(new Insets(2, 2, 2, 2));
				beeperButton.addActionListener(e -> {
					Color color = JColorChooser.showDialog(colorDialog, "Choose a Beeper Color", Color.BLACK);
					if (color != null) {
						beeperPanel.setBackground(color);
					}
				});

				JButton beeperLabelButton = new JButton("Choose Color");
				beeperLabelButton.setMargin(new Insets(2, 2, 2, 2));
				beeperLabelButton.addActionListener(e -> {
					Color color = JColorChooser.showDialog(colorDialog, "Choose a Beeper Label Color", Color.WHITE);
					if (color != null) {
						beeperLabelPanel.setBackground(color);
					}
				});

				JButton lineButton = new JButton("Choose Color");
				lineButton.setMargin(new Insets(2, 2, 2, 2));
				lineButton.addActionListener(e -> {
					Color color = JColorChooser.showDialog(colorDialog, "Choose a Line Color", Color.BLACK);
					if (color != null) {
						linePanel.setBackground(color);
					}
				});

				JButton backgroundButton = new JButton("Choose Color");
				backgroundButton.setMargin(new Insets(2, 2, 2, 2));
				backgroundButton.addActionListener(e -> {
					Color color = JColorChooser.showDialog(colorDialog, "Choose a Background Color", Color.WHITE);
					if (color != null) {
						backgroundPanel.setBackground(color);
					}
				});

				JButton colorOkButton = new JButton("OK");
				colorOkButton.addActionListener(e -> {
					updateWorldColors(new WorldColorCollection(wallPanel.getBackground(), beeperPanel.getBackground(), beeperLabelPanel.getBackground(), linePanel.getBackground(), backgroundPanel.getBackground()));
					updateDirty(true);
					colorDialog.setVisible(false);
				});

				JButton colorCancelButton = new JButton("Cancel");
				colorCancelButton.addActionListener(e -> colorDialog.setVisible(false));

				JButton resetButton = new JButton("Reset World Colors");
				resetButton.addActionListener(e -> {
					updateWorldColors(WorldColorCollection.getDefaultWorldColorCollection());
					updateDirty(true);
					colorDialog.setVisible(false);
				});

				//generated code, don't touch
				GroupLayout colorLayout = new GroupLayout(colorDialog.getContentPane());
				colorLayout.setHorizontalGroup(
						colorLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(colorLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(colorLayout.createSequentialGroup()
												.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(wallLabel)
														.addComponent(beeperLabel)
														.addComponent(beeperLabelLabel)
														.addComponent(lineLabel)
														.addComponent(backgroundLabel, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
												.addGap(67)
												.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
														.addComponent(linePanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
														.addComponent(beeperLabelPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
														.addComponent(beeperPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
														.addComponent(wallPanel, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)))
										.addGroup(colorLayout.createSequentialGroup()
												.addComponent(colorOkButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(colorCancelButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(colorLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(beeperButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
										.addComponent(beeperLabelButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
										.addComponent(lineButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
										.addComponent(backgroundButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
										.addComponent(wallButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(12))
						);
				colorLayout.setVerticalGroup(
						colorLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(colorLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(wallPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(wallLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(wallButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(colorLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(beeperPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
										.addComponent(beeperLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
										.addComponent(beeperButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(colorLayout.createSequentialGroup()
												.addComponent(beeperLabelLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(lineLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(backgroundLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
										.addGroup(colorLayout.createSequentialGroup()
												.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(beeperLabelPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
														.addComponent(beeperLabelButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(linePanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
														.addComponent(lineButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(colorLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(backgroundButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
														.addComponent(backgroundPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))))
								.addGap(18)
								.addGroup(colorLayout.createParallelGroup(Alignment.BASELINE, false)
										.addComponent(colorCancelButton)
										.addComponent(colorOkButton)
										.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
						);
				colorDialog.getContentPane().setLayout(colorLayout);

				//Size Window
				//defined first so sizeFrame anonymous class can access them
				ScrollSpinner widthSpinner = new ScrollSpinner(new SpinnerNumberModel(world.width, 1, null, 1));
				ScrollSpinner heightSpinner = new ScrollSpinner(new SpinnerNumberModel(world.height, 1, null, 1));

				JDialog sizeDialog = new JDialog(this, "World Size", true) {
					public void setVisible(boolean b) {
						if (newWorld && !b) {
							world = new World();
							panel.resetPanAndZoom();
							updateWorldColors(world.colorCollection);
							updateDirty(false);
							newWorld = false;
							panel.repaint();
						}
						super.setVisible(b);
					}
				};
				sizeDialog.setBounds(0, 0, 252, 135);
				sizeDialog.setLocationRelativeTo(this);
				sizeDialog.setIconImage(getIconImage());
				sizeDialog.setResizable(false);

				JLabel widthLabel = new JLabel("Width");
				JLabel heightLabel = new JLabel("Height");

				widthSpinner.addMouseWheelListener(e -> {
					if ((int)widthSpinner.getValue()-e.getWheelRotation() > 0) {
						widthSpinner.setValue((int)widthSpinner.getValue()-e.getWheelRotation());
					}
				});

				heightSpinner.addMouseWheelListener(e -> {
					if ((int)heightSpinner.getValue()-e.getWheelRotation() > 0) {
						heightSpinner.setValue((int)heightSpinner.getValue()-e.getWheelRotation());
					}
				});

				JButton sizeOkButton = new JButton("OK");
				sizeOkButton.addActionListener(e -> {
					if (newWorld) {
						world = new World((int)widthSpinner.getValue(), (int)heightSpinner.getValue());
						panel.resetPanAndZoom();
						updateWorldColors(world.colorCollection);
						updateDirty(false);
						newWorld = false;
					} else {
						world.width = (int)widthSpinner.getValue();
						world.height = (int)heightSpinner.getValue();
						updateDirty(true);
					}
					panel.repaint();
					sizeDialog.setVisible(false);
				});

				JButton sizeCancelButton = new JButton("Cancel");
				sizeCancelButton.addActionListener(e -> {
					sizeDialog.setVisible(false);
				});

				//generated code, don't touch
				GroupLayout sizeLayout = new GroupLayout(sizeDialog.getContentPane());
				sizeLayout.setHorizontalGroup(
						sizeLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(sizeLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(sizeLayout.createParallelGroup(Alignment.TRAILING)
										.addGroup(sizeLayout.createSequentialGroup()
												.addComponent(widthLabel)
												.addContainerGap(198, Short.MAX_VALUE))
										.addGroup(sizeLayout.createSequentialGroup()
												.addComponent(heightLabel)
												.addContainerGap(195, Short.MAX_VALUE))
										.addGroup(sizeLayout.createSequentialGroup()
												.addGroup(sizeLayout.createParallelGroup(Alignment.TRAILING)
														.addGroup(sizeLayout.createSequentialGroup()
																.addGap(77)
																.addGroup(sizeLayout.createParallelGroup(Alignment.LEADING)
																		.addComponent(heightSpinner, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
																		.addComponent(widthSpinner, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
														.addGroup(Alignment.LEADING, sizeLayout.createSequentialGroup()
																.addComponent(sizeOkButton)
																.addPreferredGap(ComponentPlacement.RELATED)
																.addComponent(sizeCancelButton)))
												.addContainerGap(19, GroupLayout.PREFERRED_SIZE))))
						);
				sizeLayout.setVerticalGroup(
						sizeLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(sizeLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(sizeLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(widthLabel)
										.addComponent(widthSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(sizeLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(heightLabel)
										.addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18)
								.addGroup(sizeLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(sizeOkButton)
										.addComponent(sizeCancelButton))
								.addContainerGap(13, Short.MAX_VALUE))
						);
				sizeDialog.getContentPane().setLayout(sizeLayout);

				//Top Menu Bar
				dirty = false;
				newWorld = false;
				saveFile = null;

				JMenuBar menuBar = new JMenuBar();
				setJMenuBar(menuBar);

				JMenu fileMenu = new JMenu("File");
				menuBar.add(fileMenu);

				JMenu worldMenu = new JMenu("World");
				menuBar.add(worldMenu);

				JMenuItem newButton = new JMenuItem("New");
				newButton.addActionListener(e -> {
					if (saveWorld(false, true)) {
						pause();
						launchThreads();
						newWorld = true;
						saveFile = null;
						widthSpinner.setValue(20);
						heightSpinner.setValue(20);
						sizeDialog.setVisible(true);//sizeFrame and sizeOkButton handle new world creation because newWorld is true
					}
				});
				newButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
				fileMenu.add(newButton);

				//TODO if the filetype is .kwld, convert to karelz format
				fileChooser = new ExtensionFileChooser(DIRECTORY, "kzw");
				fileChooser.setFileFilter(new FileNameExtensionFilter("Karel Worlds", "txt", "kzw", "kwld"));


				JMenuItem loadButton = new JMenuItem("Load");
				loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
				loadButton.addActionListener(e -> {
					if (saveWorld(false, true) && fileChooser.showDialog(this, "Load") == JFileChooser.APPROVE_OPTION) {
						saveFile = fileChooser.getSelectedFile();
						pause();
						world.loadWorld(saveFile);
						launchThreads();
						panel.resetPanAndZoom();
						updateWorldColors(world.colorCollection);
						updateDirty(false);
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
				editColorsButton.addActionListener(e -> colorDialog.setVisible(true));
				worldMenu.add(editColorsButton);

				JMenuItem worldSizeButton = new JMenuItem("Edit World Size");
				worldSizeButton.addActionListener(e -> {
					widthSpinner.setValue(world.width);
					heightSpinner.setValue(world.height);
					sizeDialog.setVisible(true);
				});
				worldMenu.add(worldSizeButton);

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
							currentToolButton.tool.modifyWorld(world, point, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)beeperSpinner.getValue(), !SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e));
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

		refreshTimer = new Timer();

		robotTimer = new Timer();

		launchThreads();
	}

	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b) {
			refreshTask = new TimerTask() {
				public void run() {
					panel.repaint();
				}
			};
			refreshTimer.schedule(refreshTask, 0, 100);
		} else if (refreshTask != null) {
			refreshTask.cancel();
			refreshTimer.purge();
		}

		if (autoplayAfter >= 0 && b) {

			Util.sleep(autoplayAfter);

			play();
		}
		//panel.repaint();
	}

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

	public void updateDirty(boolean value) {
		dirty = value;
		setTitle("Karel-Z"+(saveFile != null ? " \u2013 "+saveFile.getName() : "")+(dirty ? "*" : ""));
		if (showEditorTools) {
			saveButton.setEnabled(value);	
		}
	}

	public void updateWorldColors(WorldColorCollection colorCollection) {
		//update world color collection
		world.colorCollection = colorCollection;

		//update PanAndZoom panel background
		panel.setBackground(colorCollection.backgroundColor);

		if (showEditorTools) {

			//update color panels
			wallPanel.setBackground(colorCollection.wallColor);
			beeperPanel.setBackground(colorCollection.beeperColor);
			beeperLabelPanel.setBackground(colorCollection.beeperLabelColor);
			linePanel.setBackground(colorCollection.lineColor);
			backgroundPanel.setBackground(colorCollection.backgroundColor);

			//update tool icons
			for (Component a : toolBar.getComponents()) {
				if (a instanceof ToolButton) {
					((ToolButton)a).generateAndSetIcon(world.colorCollection, infiniteCheckBox.isSelected() ? Cell.INFINITY : (int)beeperSpinner.getValue());
				}
			}
		}

		//repaint
		panel.repaint();
	}

	public void loadWorld(World aWorld) {
		saveFile = null;
		pause();
		world.loadWorld(aWorld);
		launchThreads();
		panel.resetPanAndZoom();
		updateWorldColors(world.colorCollection);
		updateDirty(false);
	}

	public void launchThreads() {
		//pause();
		runningRobots = new ArrayList<Robot>(world.robots);
		runningRobots.forEach(Robot::launchThread);
	}

	public void playPause() {

		if (showPlaybackTools) {
			playPauseButton.setIcon(playing ? PLAY_ICON : PAUSE_ICON);
			stepButton.setEnabled(playing);
			delaySpinner.setEnabled(playing);
			delayLabel.setEnabled(playing);
		}

		if (playing) {

			//pause
			robotTask.cancel();
			robotTimer.purge();

		} else {

			//play
			robotTask = new TimerTask() {
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
						cancel();
						pause();
					}
				}
			};
			robotTimer.schedule(robotTask, 0, delay);

		}
		playing = !playing;
	}

	public void play() {
		if (!playing) {
			playPause();
		}
	}

	public void pause() {
		if (playing) {
			playPause();
		}
	}

	public void step() {
		for (Robot a : runningRobots) {
			if (a.threadIsActive) {
				a.step();
			}
		}
		panel.repaint();
	}

	public void runTest(World test) {//this returns when the test is finished
		loadWorld(test);
		play();
		while (playing) {//pause is programmatically pressed in the robotTask when the robots finish running
			Util.sleep(delay);
		}
		//return, as the test is finished
	}

	public static void runTests(World... tests) {
		runTests(100, tests);
	}

	public static void runTests(int delay, World... tests) {
		runTests(delay, 500, tests);
	}

	public static void runTests(int delay, int delayBetweenTests, World... tests) {
		runTests(delay, delayBetweenTests, false, false, tests);
	}

	public static void runTests(int delay, int delayBetweenTests, boolean showEditorTools, boolean showPlaybackTools, World... tests) {
		Window window = new Window(tests[0], delay, showEditorTools, showPlaybackTools);
		window.setVisible(true);
		Util.sleep(250);

		for (int i = 0; i < tests.length; i++) {
			window.runTest(tests[i]);
			Util.sleep(delayBetweenTests);
		}
	}
}
