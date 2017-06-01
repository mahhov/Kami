package editor;

import paint.painterelement.PainterQueue;

class ScreenToggleButton extends ScreenButton {
	boolean toggle;
	
	ScreenToggleButton(String text) {
		super(text);
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState) {
		super.handleMouseInput(screenX, screenY, mouseState);
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
