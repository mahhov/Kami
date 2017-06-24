package terrain.generator;

import editor.Blueprint;
import engine.Math3D;
import engine.Timer;
import terrain.Terrain;
import terrain.module.FullGray;

import static terrain.Terrain.CHUNK_SIZE;

public class BlueprintGenerator extends Generator {
	public static final int BLUEPRINT_SCALE = 10;
	private Blueprint blueprint;
	
	int startX, startY, startZ, endX, endY, endZ;
	int x, y, z, sx, sy, sz, ex, ey, ez;
	
	public BlueprintGenerator(Terrain terrain, Blueprint blueprint) {
		super(terrain);
		this.blueprint = blueprint;
	}
	
	public int getwidth() {
		return blueprint.width * BLUEPRINT_SCALE;
	}
	
	public int getlength() {
		return blueprint.length * BLUEPRINT_SCALE;
	}
	
	public int getheight() {
		return blueprint.height * BLUEPRINT_SCALE;
	}
	
	public void generate(int cx, int cy, int cz) {
		Timer.TERRAIN_GENERATOR.timeStart();
		
		startX = cx * CHUNK_SIZE;
		startY = cy * CHUNK_SIZE;
		startZ = cz * CHUNK_SIZE;
		endX = Math3D.min(cx * CHUNK_SIZE + CHUNK_SIZE, terrain.width);
		endY = Math3D.min(cy * CHUNK_SIZE + CHUNK_SIZE, terrain.length);
		endZ = Math3D.min(cz * CHUNK_SIZE + CHUNK_SIZE, terrain.height);
		
		int startBlueX = startX / BLUEPRINT_SCALE;
		int startBlueY = startY / BLUEPRINT_SCALE;
		int startBlueZ = startZ / BLUEPRINT_SCALE;
		int endBlueX = (endX - 1) / BLUEPRINT_SCALE + 1;
		int endBlueY = (endY - 1) / BLUEPRINT_SCALE + 1;
		int endBlueZ = (endZ - 1) / BLUEPRINT_SCALE + 1;
		
		for (int bx = startBlueX; bx < endBlueX; bx++)
			for (int by = startBlueY; by < endBlueY; by++)
				for (int bz = startBlueZ; bz < endBlueZ; bz++)
					if (blueprint.blueprint[bx][by][bz][0] == 1)
						generateBlueprintCoord(bx, by, bz);
		
		Timer.TERRAIN_GENERATOR.timeEnd();
	}
	
	private void generateBlueprintCoord(int bx, int by, int bz) {
		sx = bx * BLUEPRINT_SCALE;
		sy = by * BLUEPRINT_SCALE;
		sz = bz * BLUEPRINT_SCALE;
		ex = Math3D.min(sx + BLUEPRINT_SCALE, endX);
		ey = Math3D.min(sy + BLUEPRINT_SCALE, endY);
		ez = Math3D.min(sz + BLUEPRINT_SCALE, endZ);
		generateSide(sx, sy, sz, ex, ey, ez);
	}
	
	private void generateSide(int sx, int sy, int sz, int ex, int ey, int ez) {
		for (x = sx; x < ex; x++)
			for (y = sy; y < ey; y++)
				for (z = sz; z < ez; z++)
					terrain.add(x, y, z, new FullGray());
	}
}
