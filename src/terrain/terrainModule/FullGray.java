package terrain.terrainModule;

import engine.Math3D;

import java.awt.*;

public class FullGray extends TerrainModule {
	public FullGray() {
		super(randColor());
	}
	
	private static Color randColor() {
		if (Math3D.flip(.1))
			return new Color(Math3D.rand(0, 255), Math3D.rand(0, 255), Math3D.rand(0, 255));
		return Color.GRAY;
	}
}
