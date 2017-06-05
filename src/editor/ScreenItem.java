package editor;

import paint.painterelement.PainterQueue;

import java.awt.*;

abstract class ScreenItem {
	static final Color UP_COLOR = Color.WHITE, HIGHLIGHT_COLOR = Color.LIGHT_GRAY, PRESS_COLOR = Color.GRAY, TEXT_COLOR = Color.BLACK;
	double left, right, width;
	double top, bottom, height;
	
	void setPosition(double left, double top, double width, double height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		right = left + width;
		bottom = top + height;
	}
	
	// return false on stop propogation - todo: reverse, return true on stop propogation
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		return true;
	}
	
	void draw(PainterQueue painterQueue) {
	}
	
	final boolean containsScreenCoord(double screenX, double screenY) {
		return screenX >= left && screenX < right && screenY > top && screenY < bottom;
	}
	
	final double[] screenToItemCoord(double screenX, double screenY) {
		return new double[] {(screenX - left) / width, (screenY - top) / height};
	}
	
	final double[] ItemToScreenCoord(double x, double y) {
		return new double[] {left + x * width, top + y * height};
	}
}
