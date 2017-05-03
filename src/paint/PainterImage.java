package paint;

import java.awt.image.BufferedImage;

public class PainterImage extends PainterElement {
	private BufferedImage image;
	private int shift;
	private int shiftVert;
	
	public PainterImage(BufferedImage image, int shift, int shiftVert) {
		this.image = image;
		this.shift = shift;
		this.shiftVert = shiftVert;
	}
	
	void paint(Painter painter) {
		painter.drawImage(image, shift, shiftVert);
	}
}
