package editor;

import paint.painterelement.PainterQueue;

abstract class ScreenItem {
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
	
	// return false on stop propogation
	boolean handleMouseInput(double screenX, double screenY, int mouseState) {
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
