package terrain;

import terrain.terrainModule.FullGray;
import world.World;

public class Terrain {
	private static final int CHUNK_SIZE = 10;
	int width, length, height;
	//	private TerrainModule[][][] part;
	private TerrainChunk[][][] terrainChunk;
	private IntersectionFinder intersectionFinder;
	
	//	public Terrain() {
	//		width = length = 400;
	//		height = 50;
	//		intersectionFinder = new IntersectionFinder(this);
	//		part = new TerrainModule[width][length][height];
	//		for (int x = 0; x < part.length; x++)
	//			for (int y = 0; y < part[x].length; y++) {
	//				part[x][y][0] = new FullGray();
	//				if (Math.random() > .9993)
	//					generateTree(x, y, 0);
	//			}
	//	}
	
	public Terrain() {
		intersectionFinder = new IntersectionFinder(this);
		terrainChunk = new TerrainChunk[50][50][10];
		width = terrainChunk.length * CHUNK_SIZE;
		length = terrainChunk[0].length * CHUNK_SIZE;
		height = terrainChunk[0][0].length * CHUNK_SIZE;
	}
	
	private void generate(int cx, int cy, int cz, World world) {
		for (int x = 0; x < CHUNK_SIZE; x++)
			for (int y = 0; y < CHUNK_SIZE; y++)
				terrainChunk[cx][cy][cz].add(x, y, 0, new FullGray());
		addToWorld(cx, cy, cz, world);
	}
	
	public void expand(int x, int y, int z, int buffer, World world) {
		for (int xi = -1; xi < 2; xi++)
			for (int yi = -1; yi < 2; yi++)
				for (int zi = -1; zi < 2; zi++)
					expand(x + xi * buffer, y + yi * buffer, z + zi * buffer, world);
	}
	
	private void expand(int x, int y, int z, World world) {
		if (x < 0 || x >= width || y < 0 || y >= length || z < 0 || z >= height)
			return;
		int[] coord = getChunkCoord(x, y, z);
		if (terrainChunk[coord[0]][coord[1]][coord[2]] == null) {
			terrainChunk[coord[0]][coord[1]][coord[2]] = new TerrainChunk(CHUNK_SIZE);
			generate(coord[0], coord[1], coord[2], world);
		}
	}
	
	private void addToWorld(int cx, int cy, int cz, World world) {
		int offX = cx * CHUNK_SIZE;
		int offY = cy * CHUNK_SIZE;
		int offZ = cz * CHUNK_SIZE;
		terrainChunk[cx][cy][cz].addToWorld(offX, offY, offZ, world);
	}
	
	public double[] findIntersection(double[] orig, double[] dir, boolean allowSlide, boolean limitDistance, boolean allowCollideWithEdge) {
		return intersectionFinder.find(orig, dir, allowSlide, limitDistance, allowCollideWithEdge);
	}
	
	public boolean[] getIntersectionCollide() {
		return intersectionFinder.collide;
	}
	
	private int[] getChunkCoord(int x, int y, int z) {
		int cx = x / CHUNK_SIZE;
		int cy = y / CHUNK_SIZE;
		int cz = z / CHUNK_SIZE;
		x -= cx * CHUNK_SIZE;
		y -= cy * CHUNK_SIZE;
		z -= cz * CHUNK_SIZE;
		return new int[] {cx, cy, cz, x, y, z};
	}
	
	boolean isEmpty(int x, int y, int z) {
		int[] coord = getChunkCoord(x, y, z);
		return terrainChunk[coord[0]][coord[1]][coord[2]] != null && terrainChunk[coord[0]][coord[1]][coord[2]].isEmpty(coord[3], coord[4], coord[5]);
	}
}
