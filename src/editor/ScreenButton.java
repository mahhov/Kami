package editor;

import control.Controller;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;
import paint.painterelement.PainterText;

import java.awt.*;

class ScreenButton extends ScreenItem {
	private static final Color UP_COLOR = Color.WHITE, HIGHLIGHT_COLOR = Color.LIGHT_GRAY, PRESS_COLOR = Color.GRAY, TEXT_COLOR = Color.BLACK;
	private String text;
	private boolean press, highlight;
	
	ScreenButton(double left, double width, double top, double height, String text) {
		super(left, width, top, height);
		this.text = text;
	}
	
	public void handleMouseInput(double screenX, double screenY, int mouseState) {
		highlight = containsScreenCoord(screenX, screenY);
		press = highlight && mouseState == Controller.DOWN;
	}
	
	void draw(PainterQueue painterQueue) {
		if (press)
			painterQueue.add(new PainterRectangle(left, top, width, height, PRESS_COLOR));
		else if (highlight)
			painterQueue.add(new PainterRectangle(left, top, width, height, HIGHLIGHT_COLOR));
		else
			painterQueue.add(new PainterRectangle(left, top, width, height, UP_COLOR));
		painterQueue.add(new PainterRectangle(left, top, width, height, TEXT_COLOR, false));
		painterQueue.add(new PainterText(left, top, width, height, TEXT_COLOR, text));
	}
	
	boolean isDown() {
		return press;
	}
}
