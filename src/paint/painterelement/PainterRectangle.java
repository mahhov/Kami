package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterRectangle extends PainterElement {
	private double x, y, width, height;
	private Color color;
	private boolean fill;
	
	public PainterRectangle(double x, double y, double width, double height, Color color) {
		this(x, y, width, height, color, true);
	}
	
	public PainterRectangle(double x, double y, double width, double height, Color color, boolean fill) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.fill = fill;
	}
	
	void draw(Painter painter) {
		if (fill)
			painter.drawRectangle(x, y, width, height, color);
		else
			painter.drawRectangleFrame(x, y, width, height, color);
	}
}
