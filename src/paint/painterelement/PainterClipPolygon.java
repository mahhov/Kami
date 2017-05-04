package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterClipPolygon extends PainterElement {
	private double[][] xy;
	private double light;
	private Color color;
	private int clipState;
	private boolean frame;
	
	public PainterClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
		this.xy = xy;
		this.light = light;
		this.color = color;
		this.clipState = clipState;
		this.frame = frame;
	}
	
	void paint(Painter painter) {
		painter.drawClipPolygon(xy, light, color, clipState, frame);
	}
}
