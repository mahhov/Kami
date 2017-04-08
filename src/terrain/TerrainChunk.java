package terrain;

import terrain.terrainModule.TerrainModule;

public class TerrainChunk {
	private TerrainModule[][][] part;
	private int count;
	
	void init(int size) {
		part = new TerrainModule[size][size][size];
		count = 0;
	}
	
	void safeInit(int size) {
		if (part == null)
			init(size);
	}
	
	private void add(int x, int y, int z, TerrainModule module) {
		if (part[x][y][z] == null)
			count++;
		part[x][y][z] = module;
	}
	
	boolean isEmpty(int x, int y, int z) {
		return part[x][y][z] == null;
	}
	
	boolean isEmpty() {
		return count == 0;
	}
}
