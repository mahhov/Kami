package paint.painterelement;

import paint.painter.Painter;

public class PainterBlur extends PainterElement{
	private double blur;
	
	public PainterBlur(double blur) {
		this.blur = blur;
	}
	
	void draw(Painter painter) {
		painter.drawBlur(blur);
	}
}
