package editor;

import paint.painterelement.PainterQueue;

class ScreenToggleButton extends ScreenButton {
	boolean toggle;
	
	ScreenToggleButton(String text) {
		super(text);
	}
	
	ScreenToggleButton(String text, char shortcutKey) {
		super(text, shortcutKey);
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		super.handleMouseInput(screenX, screenY, mouseState, charInput, charState);
		toggle ^= press;
		return !highlight;
	}
	
	void draw(PainterQueue painterQueue) {
		if (highlight && toggle)
			draw(painterQueue, PRESS_COLOR);
		else if (highlight || toggle)
			draw(painterQueue, HIGHLIGHT_COLOR);
		else
			draw(painterQueue, UP_COLOR);
	}
}
