package paint;

public class PainterBlur extends PainterElement{
	private double blur;
	
	public PainterBlur(double blur) {
		this.blur = blur;
	}
	
	void paint(Painter painter) {
		painter.drawBlur(blur);
	}
}
