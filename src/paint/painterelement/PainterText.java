package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterText extends PainterElement {
	private double x, y, width, height;
	private Color color;
	private String text;
	
	public PainterText(double x, double y, Color color, String text) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.text = text;
	}
	
	public PainterText(double x, double y, double width, double height, Color color, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.text = text;
	}
	
	void draw(Painter painter) {
		if (width == 0)
			painter.drawText(x, y, color, text);
		else
			painter.drawTextCentered(x, y, width, height, color, text);
	}
}
