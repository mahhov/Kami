package world.interfaceelement;

import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

public class Bar implements InterfaceElement {
	private double x, y, width, height, fill;
	private static final Color DEFAULT_BACK_COLOR = Color.CYAN, DEFAULT_FILL_COLOR = Color.GREEN;
	private Color backColor, fillColor;

	public Bar(double x, double y, double width, double height) {
		this(x, y, width, height, DEFAULT_BACK_COLOR, DEFAULT_FILL_COLOR);
	}

	public Bar(double x, double y, double width, double height, Color backColor, Color fillColor) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.backColor = backColor;
		this.fillColor = fillColor;
	}

	public void setFill(double fill) {
		this.fill = fill;
	}

	public void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(x, y, width, height, backColor));
		painterQueue.add(new PainterRectangle(x, y, width * fill, height, fillColor));
	}
}
