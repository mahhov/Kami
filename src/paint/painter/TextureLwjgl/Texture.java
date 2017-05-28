package paint.painter.TextureLwjgl;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

abstract class Texture {
	int width, height;
	private int textureId;
	
	void init(BufferedImage image) {
		init(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()), image.getWidth(), image.getHeight());
	}
	
	void init(BufferedImage image, byte alpha) {
		init(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()), image.getWidth(), image.getHeight(), alpha);
	}
	
	void init(int[] imageARGB, int width, int height) {
		this.width = width;
		this.height = height;
		ByteBuffer byteBuffer = createByteBuffer(imageARGB, width, height);
		textureId = createTexture(byteBuffer, width, height);
	}
	
	void init(int[] imageARGB, int width, int height, byte alpha) {
		this.width = width;
		this.height = height;
		ByteBuffer byteBuffer = createByteBuffer(imageARGB, width, height, alpha);
		textureId = createTexture(byteBuffer, width, height);
	}
	
	private static ByteBuffer createByteBuffer(int[] imageARGB, int width, int height) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = imageARGB[y * width + x];
				byteBuffer.put((byte) ((argb >> 16) & 0xFF));
				byteBuffer.put((byte) ((argb >> 8) & 0xFF));
				byteBuffer.put((byte) (argb & 0xFF));
				byteBuffer.put((byte) ((argb >> 24) & 0xFF));
			}
		}
		byteBuffer.flip();
		
		return byteBuffer;
	}
	
	private static ByteBuffer createByteBuffer(int[] imageARGB, int width, int height, byte alpha) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = imageARGB[y * width + x];
				byteBuffer.put((byte) ((argb >> 16) & 0xFF));
				byteBuffer.put((byte) ((argb >> 8) & 0xFF));
				byteBuffer.put((byte) (argb & 0xFF));
				byteBuffer.put(alpha);
			}
		}
		byteBuffer.flip();
		
		return byteBuffer;
	}
	
	private static int createTexture(ByteBuffer byteBuffer, int width, int height) {
		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		return textureId;
	}
	
	void draw(float x, float y) {
		prepDraw();
		coordDraw(x, y);
		endDraw();
	}
	
	private void prepDraw() {
		glBindTexture(GL_TEXTURE_2D, textureId);
		glEnable(GL_TEXTURE_2D);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	abstract void coordDraw(float x, float y);
	
	private void endDraw() {
		glDrawArrays(GL_QUADS, 0, 4);
		glDisable(GL_TEXTURE_2D);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisable(GL_BLEND);
	}
}
