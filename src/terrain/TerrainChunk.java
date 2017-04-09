package terrain;

import engine.Math3D;
import shapes.Shape;
import terrain.terrainModule.TerrainModule;
import world.World;

public class TerrainChunk {
	private TerrainModule[][][] part;
	private int count;
	
	TerrainChunk(int size) {
		part = new TerrainModule[size][size][size];
		count = 0;
	}
	
	void add(int x, int y, int z, TerrainModule module) {
		if (part[x][y][z] == null)
			count++;
		part[x][y][z] = module;
	}
	
	TerrainModule get(int x, int y, int z) {
		return part[x][y][z];
	}
	
	void addToWorld(int offX, int offY, int offZ, World world) {
		if (isEmpty())
			return;
		Shape shape;
		int block[];
		TerrainModule module;
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++) {
					module = get(x, y, z);
					if (module != null) {
						block = getBlock(x, y, z);
						shape = module.getShape(offX + x + .5, offY + y + .5, offZ + z + .5, block);
						if (shape != null)
							world.addShape(offX + x, offY + y, offZ + z, shape);
					}
				}
	}
	
	private int[] getBlock(int x, int y, int z) {
		int[] block = new int[6];
		if (x > 0 && part[x - 1][y][z] != null)
			block[Math3D.LEFT] = part[x - 1][y][z].block[Math3D.RIGHT];
		if (x < part.length - 1 && part[x + 1][y][z] != null)
			block[Math3D.RIGHT] = part[x + 1][y][z].block[Math3D.LEFT];
		if (y > 0 && part[x][y - 1][z] != null)
			block[Math3D.FRONT] = part[x][y - 1][z].block[Math3D.BACK];
		if (y < part[0].length - 1 && part[x][y + 1][z] != null)
			block[Math3D.BACK] = part[x][y + 1][z].block[Math3D.FRONT];
		if (z > 0 && part[x][y][z - 1] != null)
			block[Math3D.BOTTOM] = part[x][y][z - 1].block[Math3D.TOP];
		if (z < part[0][0].length - 1 && part[x][y][z + 1] != null)
			block[Math3D.TOP] = part[x][y][z + 1].block[Math3D.BOTTOM];
		return block;
	}
	
	boolean isEmpty(int x, int y, int z) {
		return part[x][y][z] == null;
	}
	
	private boolean isEmpty() {
		return count == 0;
	}
}
