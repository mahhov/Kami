package editor;

import control.Controller;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;
import paint.painterelement.PainterText;

import java.awt.*;

class ScreenButton extends ScreenItem {
	private String text;
	boolean press, down, highlight;
	
	ScreenButton(String text) {
		this.text = text;
	}
	
	public boolean handleMouseInput(double screenX, double screenY, int mouseState) {
		highlight = containsScreenCoord(screenX, screenY);
		press = highlight && mouseState == Controller.PRESSED;
		down = press || (highlight && mouseState == Controller.DOWN);
		return !highlight;
	}
	
	void draw(PainterQueue painterQueue) {
		if (down)
			draw(painterQueue, PRESS_COLOR);
		else if (highlight)
			draw(painterQueue, HIGHLIGHT_COLOR);
		else
			draw(painterQueue, UP_COLOR);
	}
	
	void draw(PainterQueue painterQueue, Color fillColor) {
		painterQueue.add(new PainterRectangle(left, top, width, height, fillColor));
		painterQueue.add(new PainterRectangle(left, top, width, height, TEXT_COLOR, false));
		painterQueue.add(new PainterText(left, top, width, height, TEXT_COLOR, text));
	}
}	
