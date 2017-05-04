package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterRectangle extends PainterElement {
	private double x, y, width, height;
	private Color color;
	
	public PainterRectangle(double x, double y, double width, double height, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	void paint(Painter painter) {
		painter.drawRectangle(x, y, width, height, color);
	}
}
