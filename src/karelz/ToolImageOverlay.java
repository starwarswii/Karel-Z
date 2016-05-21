package karelz;

import java.awt.image.BufferedImage;

public class ToolImageOverlay {

	//these need to be in different class than Tool because enum static shenanigans.
	static final BufferedImage PAN_AND_ZOOM_OVERLAY = Util.getImage("pan-and-zoom.png");
	static final BufferedImage ERASER_OVERLAY = Util.getImage("eraser.png");

}
