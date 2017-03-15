package world;

import camera.Camera;
import control.Controller;
import engine.Math3D;
import engine.Painter;
import list.LList;
import particles.Particle;
import projectiles.Projectile;
import shapes.Shape;
import shapes.StaticCube;
import ships.Ship;

public class World {
	public final int width, length, height, chunkSize;
	Chunk[][][] chunk;
	
	private LList<Ship> ship;
	private LList<Projectile> projectile;
	private Particle[] particle;
	
	public World(int chunkWidth, int chunkLength, int chunkHeight, int chunkSize) {
		width = chunkWidth * chunkSize;
		height = chunkLength * chunkSize;
		length = chunkHeight * chunkSize;
		this.chunkSize = chunkSize;
		chunk = new Chunk[chunkWidth][chunkLength][chunkHeight];
		for (int x = 0; x < chunkWidth; x++)
			for (int y = 0; y < chunkLength; y++)
				for (int z = 0; z < chunkHeight; z++)
					chunk[x][y][z] = new Chunk();
		ship = new LList<>();
		projectile = new LList<>();
	}
	
	void addStaticCube(int x, int y, int z, boolean[] side) {
		addShape(x, y, z, new StaticCube(x + 0.5, y + 0.5, z + 0.5, null, side));
	}
	
	public void addShape(int x, int y, int z, Shape shape) {
		int cx = x / chunkSize;
		int cy = y / chunkSize;
		int cz = z / chunkSize;
		int sx = x - cx * chunkSize;
		int sy = y - cy * chunkSize;
		int sz = z - cz * chunkSize;
		chunk[cx][cy][cz].safeInit(chunkSize);
		chunk[cx][cy][cz].add(sx, sy, sz, shape);
	}
	
	public void addShip(Ship ship) {
		this.ship = this.ship.add(ship);
	}
	
	public void addProjectile(Projectile projectile) {
		this.projectile = this.projectile.add(projectile);
	}
	
	// DRAWING
	
	public void drawChunks(Painter painter, Camera c) {
		int boundaries[] = c.cullBoundaries();
		int volumeRaw = (boundaries[1] - boundaries[0]) * (boundaries[3] - boundaries[2]) * (boundaries[5] - boundaries[4]) / 100000;
		
		boundaries[0] = Math3D.max(boundaries[0], 0);
		boundaries[1] = Math3D.min(boundaries[1], width - 1);
		boundaries[2] = Math3D.max(boundaries[2], 0);
		boundaries[3] = Math3D.min(boundaries[3], length - 1);
		boundaries[4] = Math3D.max(boundaries[4], 0);
		boundaries[5] = Math3D.min(boundaries[5], height - 1);
		int volumeBound = (boundaries[1] - boundaries[0]) * (boundaries[3] - boundaries[2]) * (boundaries[5] - boundaries[4]) / 100000;
		
		
		int[] fromChunkCoord = getChunkCoord(boundaries[0], boundaries[2], boundaries[4]);
		int[] toChunkCoord = getChunkCoord(boundaries[1], boundaries[3], boundaries[5]);
		int[] cameraChunkCoord = getChunkCoord((int) c.x, (int) c.y, (int) c.z);
		int volumeChunk = (fromChunkCoord[0] - toChunkCoord[0]) * (fromChunkCoord[1] - toChunkCoord[1]) * (fromChunkCoord[2] - toChunkCoord[2]) / -1;
		
		Painter.debugString[1] = "(unit 100,000) volume raw " + volumeRaw + " ; (unit 100,000) volume bound " + volumeBound + " ; volume chunk " + volumeChunk;
		
		for (int x = fromChunkCoord[0]; x < cameraChunkCoord[0]; x++)
			drawChunksRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, Math3D.RIGHT);
		for (int x = toChunkCoord[0]; x > cameraChunkCoord[0]; x--)
			drawChunksRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, Math3D.LEFT);
		drawChunksRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cameraChunkCoord[0], Math3D.NONE);
	}
	
	private void drawChunksRow(Painter painter, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int x, int xSide) {
		for (int y = fromChunkCoord[1]; y < cameraChunkCoord[1]; y++)
			drawChunksColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, xSide, Math3D.BACK);
		for (int y = toChunkCoord[1]; y > cameraChunkCoord[1]; y--)
			drawChunksColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, xSide, Math3D.FRONT);
		drawChunksColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, cameraChunkCoord[1], xSide, Math3D.NONE);
	}
	
	private void drawChunksColumn(Painter painter, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int x, int y, int xSide, int ySide) {
		for (int z = fromChunkCoord[2]; z < cameraChunkCoord[2]; z++)
			drawChunk(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.TOP);
		for (int z = toChunkCoord[2]; z > cameraChunkCoord[2]; z--)
			drawChunk(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.BOTTOM);
		drawChunk(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, cameraChunkCoord[2], xSide, ySide, Math3D.NONE);
	}
	
	private void drawChunk(Painter painter, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide) {
		if (chunk[cx][cy][cz].isEmpty())
			return;
		if (xSide == Math3D.RIGHT) {
			int startx = cx == fromChunkCoord[0] ? fromChunkCoord[3] : 0;
			for (int x = startx; x < chunkSize; x++)
				drawRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x);
		} else if (xSide == Math3D.LEFT) {
			int endx = cx == toChunkCoord[0] ? toChunkCoord[3] : chunkSize - 1;
			for (int x = endx; x >= 0; x--)
				drawRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x);
		} else {
			int startx = cx == fromChunkCoord[0] ? fromChunkCoord[3] : 0;
			int endx = cx == toChunkCoord[0] ? toChunkCoord[3] : chunkSize - 1;
			for (int x = startx; x < cameraChunkCoord[3]; x++)
				drawRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, Math3D.RIGHT, ySide, zSide, x);
			for (int x = endx; x > cameraChunkCoord[3]; x--)
				drawRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, Math3D.LEFT, ySide, zSide, x);
			drawRow(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, cameraChunkCoord[3]);
		}
	}
	
	private void drawRow(Painter painter, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x) {
		if (ySide == Math3D.BACK) {
			int starty = cy == fromChunkCoord[1] ? fromChunkCoord[4] : 0;
			for (int y = starty; y < chunkSize; y++)
				drawColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, y);
		} else if (ySide == Math3D.FRONT) {
			int endy = cy == toChunkCoord[1] ? toChunkCoord[4] : chunkSize - 1;
			for (int y = endy; y >= 0; y--)
				drawColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, y);
		} else {
			int starty = cy == fromChunkCoord[1] ? fromChunkCoord[4] : 0;
			int endy = cy == toChunkCoord[1] ? toChunkCoord[4] : chunkSize - 1;
			for (int y = starty; y < cameraChunkCoord[4]; y++)
				drawColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, Math3D.BACK, zSide, x, y);
			for (int y = endy; y > cameraChunkCoord[4]; y--)
				drawColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, Math3D.FRONT, zSide, x, y);
			drawColumn(painter, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, cameraChunkCoord[4]);
		}
	}
	
	private void drawColumn(Painter painter, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x, int y) {
		if (zSide == Math3D.TOP) {
			int startz = cz == fromChunkCoord[2] ? fromChunkCoord[5] : 0;
			for (int z = startz; z < chunkSize; z++)
				drawCell(painter, c, cx, cy, cz, xSide, ySide, zSide, x, y, z);
		} else if (zSide == Math3D.BOTTOM) {
			int endz = cz == toChunkCoord[2] ? toChunkCoord[5] : chunkSize - 1;
			for (int z = endz; z >= 0; z--)
				drawCell(painter, c, cx, cy, cz, xSide, ySide, zSide, x, y, z);
		} else {
			int startz = cz == fromChunkCoord[2] ? fromChunkCoord[5] : 0;
			int endz = cz == toChunkCoord[2] ? toChunkCoord[5] : chunkSize - 1;
			for (int z = startz; z < cameraChunkCoord[5]; z++)
				drawCell(painter, c, cx, cy, cz, xSide, ySide, Math3D.TOP, x, y, z);
			for (int z = endz; z > cameraChunkCoord[5]; z--)
				drawCell(painter, c, cx, cy, cz, xSide, ySide, Math3D.BOTTOM, x, y, z);
			drawCell(painter, c, cx, cy, cz, xSide, ySide, zSide, x, y, cameraChunkCoord[5]);
		}
	}
	
	private void drawCell(Painter painter, Camera c, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x, int y, int z) {
		chunk[cx][cy][cz].draw(x, y, z, painter, c, xSide, ySide, zSide);
	}
	
	// UPDATE
	
	public void update(Controller controller) {
		for (LList<Ship> s : ship)
			s.node.update(this, controller);
		for (LList<Projectile> p : projectile)
			p.node.update(this);
	}
	
	// UITL
	
	private int[] getChunkCoord(int x, int y, int z) {
		int cx = x / chunkSize;
		int cy = y / chunkSize;
		int cz = z / chunkSize;
		x -= cx * chunkSize;
		y -= cy * chunkSize;
		z -= cz * chunkSize;
		return new int[] {cx, cy, cz, x, y, z};
	}
	
	public boolean isCellEmpty(int x, int y, int z) {
		int[] c = getChunkCoord(x, y, z);
		return chunk[c[0]][c[1]][c[2]].isCellEmpty(c[3], c[4], c[5]);
	}
	
	public boolean inBounds(double x, double y, double z) {
		return x > 0 && y > 0 && z > 0 && x < width - 1 && y < length - 1 && z < height - 1;
	}
}
