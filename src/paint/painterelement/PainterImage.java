package paint.painterelement;

import paint.painter.Painter;

import java.awt.image.BufferedImage;

public class PainterImage extends PainterElement {
	private BufferedImage image;
	private double dstx, dsty, width, height;
	
	public PainterImage(BufferedImage image, double dstx, double dsty, double width, double height) {
		this.image = image;
		this.dstx = dstx;
		this.dsty = dsty;
		this.width = width;
		this.height = height;
	}
	
	void draw(Painter painter) {
		painter.drawImage(image, dstx, dsty, width, height);
	}
}
