package terrain;

import engine.Math3D;
import shapes.Shape;
import terrain.terrainModule.FullGray;
import terrain.terrainModule.TerrainModule;
import world.World;

public class Terrain {
	private TerrainModule[][][] part;
	
	public Terrain() {
		part = new TerrainModule[200][200][100];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++) {
				part[x][y][0] = new FullGray();
				if (Math.random() > .999)
					generateTree(x, y);
			}
	}
	
	private void generateTree(int x, int y) {
		int height = Math3D.rand(7, 14);
		int brushSpread = Math3D.rand(1, 4) + height / 5;
		int brushHeight = Math3D.rand(1, 3);
		for (int z = 0; z < height; z++)
			part[x][y][z] = new FullGray();
		int xs = Math3D.max(x - brushSpread, 0);
		int xe = Math3D.min(x + brushSpread, part.length - 1);
		int ys = Math3D.max(y - brushSpread, 0);
		int ye = Math3D.min(y + brushSpread, part[x].length - 1);
		for (int xi = xs; xi <= xe; xi++)
			for (int yi = ys; yi <= ye; yi++)
				for (int z = height; z < height + brushHeight; z++)
					part[xi][yi][z] = new FullGray();
	}
	
	public void addToWorld(World world) {
		Shape shape;
		int block[];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++)
					if (part[x][y][z] != null) {
						block = getBlock(x, y, z);
						
						shape = part[x][y][z].getShape(x, y, z, block);
						if (shape != null)
							world.addShape(x, y, z, shape);
					}
	}
	
	public boolean checkCollide(double x, double y, double z) {
		return part[(int) (x + .5)][(int) (y + .5)][(int) (z + .5)] != null;
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
}
