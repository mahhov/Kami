package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterLine extends PainterElement {
	private double x1, y1, x2, y2, light;
	private Color color;
	
	public PainterLine(double x1, double y1, double x2, double y2, double light, Color color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.light = light;
		this.color = color;
	}
	
	void paint(Painter painter) {
		painter.drawLine(x1, y1, x2, y2, light, color);
	}
}