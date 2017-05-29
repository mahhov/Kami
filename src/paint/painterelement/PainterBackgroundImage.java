package paint.painterelement;

import paint.painter.Painter;

import java.awt.image.BufferedImage;

public class PainterBackgroundImage extends PainterElement {
	private BufferedImage image;
	private int shift;
	private int shiftVert;
	private boolean set;
	
	public PainterBackgroundImage(BufferedImage image) {
		this.image = image;
	}
	
	public void setShift(int shift, int shiftVert) {
		this.shift = shift;
		this.shiftVert = shiftVert;
	}
	
	void draw(Painter painter) {
		if (!set) {
			set = true;
			painter.setBackgroundImage(image);
		}
		painter.drawBackgroundImage(shift, shiftVert);
	}
}
