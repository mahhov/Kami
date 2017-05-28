package paint.painter.TextureLwjgl;

class CharGlyphTextureGroup extends TextureGroup<CharGlyphTexture> {
	CharGlyphTextureGroup() {
		texture = new CharGlyphTexture[127];
		for (int i = 32; i < texture.length; i++)
			texture[i] = new CharGlyphTexture((char) i + "");
	}
	
	void drawString(String s, int y) {
		char[] c = s.toCharArray();
		for (int x = 0; x < c.length; x++)
			if (c[x] >= 0 && c[x] < texture.length)
				texture[c[x]].draw(x, y);
	}
}
