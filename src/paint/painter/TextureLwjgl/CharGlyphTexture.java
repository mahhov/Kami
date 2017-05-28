package paint.painter.TextureLwjgl;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

class CharGlyphTexture extends Texture {
	private static final float[] TEXTURE_COORD = new float[] {0, 0, 1, 0, 1, 1, 0, 1};
	private static final int SIZE = 12;
	
	CharGlyphTexture(String s) {
		init(createImage(s));
	}
	
	private static BufferedImage createImage(String s) {
		BufferedImage textImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) textImage.getGraphics();
		g.setColor(Color.WHITE);
		g.drawString(s, 1, 10);
		return textImage;
	}
	
	void coordDraw(float x, float y) {
		float size = .03f;
		float leftX = -1 + x * size;
		float topY = 1 - y * size;
		float rightX = leftX + size;
		float bottomY = topY - size;
		float[] quad = new float[] {leftX, topY, rightX, topY, rightX, bottomY, leftX, bottomY};
		
		glTexCoordPointer(2, GL_FLOAT, 0, TEXTURE_COORD);
		glVertexPointer(2, GL_FLOAT, 0, quad);
	}
}