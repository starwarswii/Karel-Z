package karelz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Window extends JFrame {

	static Window window;
	
	static final Cursor HAND_OPENED = createCursor(getImage("hand_opened.png"), "hand_opened");
	static final Cursor HAND_CLOSED = createCursor(getImage("hand_closed.png"), "hand_closed");
	
	static final BufferedImage testImage = getImage("test.png");//TODO remove?
	
	public static void main(String[] args) {
		window = new Window();
		window.setVisible(true);
	}


	public Window() {
		super("Karel-Z");
		setBounds(100, 100, 1094, 714);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		JViewport viewport = scrollPane.getViewport();

		JLabel label = new JLabel(new ImageIcon(testImage));
		label.setCursor(HAND_OPENED);

		MouseAdapter listener = new MouseAdapter() {

			Point dragPoint = new Point();

			public boolean isPannable(MouseEvent e) {
				return !((JViewport)e.getSource()).getSize().equals(label.getSize());
			}

			public void mouseDragged(MouseEvent e) {
				if (isPannable(e)) {
					Point eventPoint = e.getPoint();
					Point viewPosition = viewport.getViewPosition();
					viewPosition.translate(dragPoint.x-eventPoint.x, dragPoint.y-eventPoint.y);
					((JComponent)viewport.getView()).scrollRectToVisible(new Rectangle(viewPosition, viewport.getSize()));
					dragPoint.setLocation(eventPoint);
				} else {
					label.setCursor(Cursor.getDefaultCursor());
				}
			}
			public void mousePressed(MouseEvent e) {
				if (isPannable(e)) {
					label.setCursor(HAND_CLOSED);
					dragPoint.setLocation(e.getPoint());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (isPannable(e)) {
					label.setCursor(HAND_OPENED);
				}
			}
			
			public void mouseEntered(MouseEvent e) {
				if (isPannable(e)) {
					label.setCursor(HAND_OPENED);
				}
			}
		};

		viewport.addMouseListener(listener);
		viewport.addMouseMotionListener(listener);
		viewport.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (viewport.getSize().equals(label.getSize())) {
					label.setCursor(Cursor.getDefaultCursor());
				}
			}
		});
		scrollPane.setViewportView(label);

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
						.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
						.addGap(44))
				);

		getContentPane().setLayout(groupLayout);

	}
	
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(Window.class.getClassLoader().getResource("resources/"+filename));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Cursor createCursor(BufferedImage image, String name) {
		return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth()/2,image.getHeight()/2), name);
	}
	
	
}
