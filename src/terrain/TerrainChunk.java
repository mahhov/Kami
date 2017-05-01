package terrain;

import engine.Math3D;
import shapes.Shape;
import shapes.ShapeParent;
import terrain.terrainmodule.TerrainModule;
import world.World;

public class TerrainChunk implements ShapeParent {
	private long drawCounter;
	boolean drawDirty;
	boolean generated;
	private int offX, offY, offZ;
	private TerrainModule[][][] part;
	private int count;
	
	TerrainChunk(int offX, int offY, int offZ, int size) {
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		part = new TerrainModule[size][size][size];
		count = 0;
	}
	
	void add(int x, int y, int z, TerrainModule module) {
		drawDirty = true;
		if (part[x][y][z] == null)
			count++;
		part[x][y][z] = module;
	}
	
	void addToWorld(World world) {
		if (isEmpty())
			return;
		drawCounter++;
		drawDirty = false;
		
		Shape shape;
		int block[];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++) {
					if (part[x][y][z] != null) {
						block = getBlock(x, y, z);
						shape = part[x][y][z].getShape(offX + x + .5, offY + y + .5, offZ + z + .5, block, this);
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
	
	public long getDrawCounter() {
		return drawCounter;
	}
}
