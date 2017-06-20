package paint.painterelement;

import paint.painter.Painter;

import java.awt.*;

public class PainterText extends PainterElement {
	private double x, y, width, height;
	private Color color;
	private String text;
	private boolean vertical;
	
	public PainterText(double x, double y, Color color, String text) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.text = text;
	}
	
	public PainterText(double x, double y, double width, double height, Color color, String text) {
		this(x, y, color, text);
		this.width = width;
		this.height = height;
	}
	
	public PainterText(double x, double y, double width, double height, Color color, String text, boolean vertical) {
		this(x, y, width, height, color, text);
		this.vertical = vertical;
	}
	
	void draw(Painter painter) {
		if (vertical)
			painter.drawTextVertical(x, y, width, height, color, text);
		else if (width == 0)
			painter.drawText(x, y, color, text);
		else
			painter.drawTextCentered(x, y, width, height, color, text);
	}
}
