package paint;

import java.awt.*;

public class PainterPolygon extends PainterElement {
	private double[][] xy;
	private double light;
	private Color color;
	private boolean frame;
	
	public PainterPolygon(double[][] xy, double light, Color color, boolean frame) {
		this.xy = xy;
		this.light = light;
		this.color = color;
		this.frame = frame;
	}
	
	void paint(Painter painter) {
		painter.drawPolygon(xy, light, color, frame);
	}
}