package terrain.generator;

import editor.Blueprint;
import engine.Math3D;
import terrain.Terrain;
import terrain.module.FullGray;

import static terrain.Terrain.CHUNK_SIZE;

public class BlueprintGenerator extends Generator {
	public static final int BLUEPRINT_SCALE = 10;
	private Blueprint blueprint;
	
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
		int startX = cx * CHUNK_SIZE;
		int startY = cy * CHUNK_SIZE;
		int startZ = cz * CHUNK_SIZE;
		int endX = Math3D.min(cx * CHUNK_SIZE + CHUNK_SIZE, terrain.width);
		int endY = Math3D.min(cy * CHUNK_SIZE + CHUNK_SIZE, terrain.length);
		int endZ = Math3D.min(cz * CHUNK_SIZE + CHUNK_SIZE, terrain.height);
		
		for (int x = startX; x < endX; x++)
			for (int y = startY; y < endY; y++)
				for (int z = startZ; z < endZ; z++) {
					if (blueprint.blueprint[x / BLUEPRINT_SCALE][y / BLUEPRINT_SCALE][z / BLUEPRINT_SCALE][0] == 1)
						terrain.add(x, y, z, new FullGray());
				}
	}
}
