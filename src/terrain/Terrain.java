package terrain;

import editor.Blueprint;
import engine.Timer;
import list.LList;
import terrain.generator.BlueprintGenerator;
import terrain.generator.Generator;
import terrain.generator.TreeGenerator;
import terrain.module.TerrainModule;
import world.World;

public class Terrain {
	public static final int CHUNK_SIZE = 40, CHUNK_BUFFER = 1;
	public int width, length, height;
	private TerrainChunk[][][] terrainChunk;
	private IntersectionFinder intersectionFinder;
	private LList<TerrainChunk> dirtyDrawChunk;
	private Generator generator;
	
	public Terrain() {
		generator = new TreeGenerator(this);
		width = generator.getwidth();
		length = generator.getlength();
		height = generator.getheight();
		
		Timer.TERRAIN_CONSTRUCTOR.timeStart();
		intersectionFinder = new IntersectionFinder(this);
		terrainChunk = new TerrainChunk[(width - 1) / CHUNK_SIZE + 1][(length - 1) / CHUNK_SIZE + 1][(height - 1) / CHUNK_SIZE + 1];
		Timer.TERRAIN_CONSTRUCTOR.timeEnd();
		System.out.println("terrain size " + width + " " + length + " " + height);
	}
	
	public Terrain(String blueprintPath) {
		generator = new BlueprintGenerator(this, Blueprint.load(blueprintPath));
		width = generator.getwidth();
		length = generator.getlength();
		height = generator.getheight();
		
		Timer.TERRAIN_CONSTRUCTOR.timeStart();
		intersectionFinder = new IntersectionFinder(this);
		terrainChunk = new TerrainChunk[(width - 1) / CHUNK_SIZE + 1][(length - 1) / CHUNK_SIZE + 1][(height - 1) / CHUNK_SIZE + 1];
		Timer.TERRAIN_CONSTRUCTOR.timeEnd();
		System.out.println("terrain size " + width + " " + length + " " + height);
	}
	
	public void add(int x, int y, int z, TerrainModule terrainModule) {
		int[] coord = getChunkCoord(x, y, z);
		if (terrainChunk[coord[0]][coord[1]][coord[2]] == null)
			terrainChunk[coord[0]][coord[1]][coord[2]] = new TerrainChunk(coord[0] * CHUNK_SIZE, coord[1] * CHUNK_SIZE, coord[2] * CHUNK_SIZE, CHUNK_SIZE);
		if (!terrainChunk[coord[0]][coord[1]][coord[2]].drawDirty)
			dirtyDrawChunk = dirtyDrawChunk.add(terrainChunk[coord[0]][coord[1]][coord[2]]);
		terrainChunk[coord[0]][coord[1]][coord[2]].add(coord[3], coord[4], coord[5], terrainModule);
	}
	
	public void expand(int x, int y, int z, World world) {
		Timer.EXPAND.timeStart();
		dirtyDrawChunk = new LList<>();
		int[] coord = getChunkCoord(x, y, z);
		for (int xi = -CHUNK_BUFFER; xi <= CHUNK_BUFFER; xi++)
			for (int yi = -CHUNK_BUFFER; yi <= CHUNK_BUFFER; yi++)
				for (int zi = -CHUNK_BUFFER; zi <= CHUNK_BUFFER; zi++)
					expandChunk(coord[0] + xi, coord[1] + yi, coord[2] + zi, world);
		addToWorld(world);
		Timer.EXPAND.timeEnd();
	}
	
	private void expandChunk(int cx, int cy, int cz, World world) {
		if (cx < 0 || cx >= terrainChunk.length || cy < 0 || cy >= terrainChunk[0].length || cz < 0 || cz >= terrainChunk[0][0].length)
			return;
		if (terrainChunk[cx][cy][cz] == null)
			terrainChunk[cx][cy][cz] = new TerrainChunk(cx * CHUNK_SIZE, cy * CHUNK_SIZE, cz * CHUNK_SIZE, CHUNK_SIZE);
		if (!terrainChunk[cx][cy][cz].generated) {
			terrainChunk[cx][cy][cz].generated = true;
			generator.generate(cx, cy, cz);
		}
	}
	
	private void addToWorld(World world) {
		Timer.ADD_TO_WORLD.timeStart();
		int i = 0;
		for (LList<TerrainChunk> c : dirtyDrawChunk) {
			c.node.addToWorld(world);
			i++;
		}
		Timer.ADD_TO_WORLD.timeEnd();
	}
	
	public double[] findIntersection(double[] orig, double[] dir, boolean allowSlide, boolean limitDistance, boolean allowCollideWithEdge, int buffer) {
		return intersectionFinder.find(orig, dir, allowSlide, limitDistance, allowCollideWithEdge, buffer);
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
	
	boolean isInBounds(int x, int y, int z, int buffer) {
		if (x >= 1 + buffer && y >= 1 + buffer && z >= 1 + buffer && x < width - 2 - buffer && y < length - 2 - buffer && z < height - 2 - buffer) {
			int[] coord = getChunkCoord(x, y, z);
			if (terrainChunk[coord[0]][coord[1]][coord[2]] != null)
				return true;
		}
		return false;
	}
}
