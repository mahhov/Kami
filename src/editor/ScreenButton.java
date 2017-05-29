package editor;

import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;
import paint.painterelement.PainterText;

import java.awt.*;

class ScreenButton extends ScreenItem {
	private static final Color UP_COLOR = Color.GRAY, DOWN_COLOR = Color.LIGHT_GRAY, TEXT_COLOR = Color.DARK_GRAY;
	private String text;
	private boolean down;
	
	ScreenButton(double left, double width, double top, double height, String text) {
		super(left, width, top, height);
		this.text = text;
	}
	
	void handleMouseClick(double screenX, double screenY) {
		if (containsScreenCoord(screenX, screenY))
			down = true;
	}
	
	void draw(PainterQueue painterQueue) {
		if (down)
			painterQueue.add(new PainterRectangle(left, top, width, height, DOWN_COLOR));
		else
			painterQueue.add(new PainterRectangle(left, top, width, height, UP_COLOR));
		painterQueue.add(new PainterText(left, top, width, height, TEXT_COLOR, text));
	}
	
	boolean isDown() {
		boolean r = down;
		down = false;
		return r;
	}
}
