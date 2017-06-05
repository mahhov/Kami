package editor;

import control.Controller;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;
import paint.painterelement.PainterText;

import java.awt.*;

class ScreenButton extends ScreenItem {
	private String text;
	private char shortcutKey;
	boolean press, down, highlight;
	
	ScreenButton(String text) {
		this.text = text;
	}
	
	ScreenButton(String text, char shortcutKey) {
		this.text = text + " [" + shortcutKey + "]";
		this.shortcutKey = shortcutKey;
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		highlight = containsScreenCoord(screenX, screenY);
		boolean shortcutKeyInput = charInput == shortcutKey;
		press = shortcutKeyInput && charState == Controller.PRESSED || highlight && mouseState == Controller.PRESSED; // order of ops: && before ||
		down = press || shortcutKeyInput && charState == Controller.DOWN || highlight && mouseState == Controller.DOWN;
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
