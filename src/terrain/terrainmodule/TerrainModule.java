package terrain.terrainmodule;

import shapes.Shape;
import shapes.StaticCube;
import terrain.TerrainChunk;

import java.awt.*;

import static engine.Math3D.BLOCK_FULL;
import static engine.Math3D.BLOCK_NONE;

public abstract class TerrainModule {
	Color color;
	public int[] block;
	
	TerrainModule(Color color) {
		this.color = color;
		block = new int[6];
		for (int i = 0; i < block.length; i++)
			block[i] = BLOCK_FULL;
	}
	
	public Shape getShape(double xc, double yc, double zc, int[] block, TerrainChunk chunk) {
		boolean[] side = new boolean[6];
		boolean visible = false;
		for (int i = 0; i < side.length; i++) {
			side[i] = block[i] == BLOCK_NONE;
			visible = visible || side[i];
		}
		if (!visible)
			return null;
		return new StaticCube(xc, yc, zc, color, side, .5, chunk);
	}
}
