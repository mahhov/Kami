package paint.painter.TextureLwjgl;

import engine.Math3D;
import paint.painter.Painter;

import java.awt.image.BufferedImage;

class BackgroundTextureGroup extends TextureGroup<BackgroundTexture> {
	private int active;
	
	BackgroundTextureGroup(BufferedImage image) {
		int[] imageARGB = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int width = image.getWidth();
		int height = image.getHeight();
		
		texture = new BackgroundTexture[4];
		for (int i = 0; i < texture.length; i++) {
			texture[i] = new BackgroundTexture();
			texture[i].init(imageARGB, width, height, (120 * (i + 1) / texture.length));
		}
		
		active = texture.length - 1;
	}
	
	void setTexture(double alpha) {
		active = (int) Math3D.maxMin(alpha * texture.length, texture.length - .5, 0);
		Painter.DEBUG_STRING[4] = "alpha: "+alpha + " active: " + active;
	}
	
	BackgroundTexture getTexture() {
		return texture[active];
	}
}
