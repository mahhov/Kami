package paint.painter.TextureLwjgl;

import static org.lwjgl.opengl.GL11.*;

class BackgroundTexture extends Texture {
	void coordDraw(float x, float y) {
		float[] textureCoord = new float[] {x / width, y / height, (x + 800) / width, y / height, (x + 800) / width, (y + 800) / height, x / width, (y + 800) / height};
		float[] quad = new float[] {-1, 1, 1, 1, 1, -1, -1, -1};
		
		glTexCoordPointer(2, GL_FLOAT, 0, textureCoord);
		glVertexPointer(2, GL_FLOAT, 0, quad);
	}
}