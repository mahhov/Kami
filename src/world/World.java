package world;

import camera.Camera;
import control.Controller;
import engine.Math3D;
import engine.Timer;
import list.LList;
import paint.painter.Painter;
import paint.painterelement.PainterQueue;
import particle.Particle;
import shapes.Shape;
import terrain.Terrain;
import world.interfaceelement.InterfaceElement;

public class World {
	public static final int CHUNK_SIZE = 10;
	public final int width, length, height;
	WorldChunk[][][] chunk;
	
	private LList<WorldElement> element;
	private LList<Particle> particle;
	private LList<InterfaceElement> backgroundElement;
	private LList<InterfaceElement> interfaceElement;
	
	public World(int chunkWidth, int chunkLength, int chunkHeight) {
		Timer.WORLD_CONSTRUCTOR.timeStart();
		width = chunkWidth * CHUNK_SIZE;
		length = chunkLength * CHUNK_SIZE;
		height = chunkHeight * CHUNK_SIZE;
		chunk = new WorldChunk[chunkWidth][chunkLength][chunkHeight];
		element = new LList<>();
		particle = new LList<>();
		backgroundElement = new LList<>();
		interfaceElement = new LList<>();
		Timer.WORLD_CONSTRUCTOR.timeEnd();
		System.out.println("world chunk size: " + chunkWidth + " " + chunkLength + " " + chunkHeight);
	}
	
	public void addShape(int x, int y, int z, Shape shape) {
		x = Math3D.maxMin(x, width - 1, 0);
		y = Math3D.maxMin(y, length - 1, 0);
		z = Math3D.maxMin(z, height - 1, 0);
		int cx = x / CHUNK_SIZE;
		int cy = y / CHUNK_SIZE;
		int cz = z / CHUNK_SIZE;
		int sx = x - cx * CHUNK_SIZE;
		int sy = y - cy * CHUNK_SIZE;
		int sz = z - cz * CHUNK_SIZE;
		if (chunk[cx][cy][cz] == null)
			chunk[cx][cy][cz] = new WorldChunk(CHUNK_SIZE);
		chunk[cx][cy][cz].add(sx, sy, sz, shape);
	}
	
	public void addElement(WorldElement element) {
		this.element = this.element.add(element);
	}
	
	public void addBackgroundElement(InterfaceElement element) {
		backgroundElement = backgroundElement.add(element);
	}
	
	public void addInterfaceElement(InterfaceElement element) {
		interfaceElement = interfaceElement.add(element);
	}
	
	public void addParticle(Particle particle) {
		this.particle = this.particle.add(particle);
	}
	
	// DRAWING
	
	private WorldChunkDrawer[] drawers;
	private int currentDrawer;
	
	public void draw(PainterQueue painterQueue, Camera c) {
		drawers = new WorldChunkDrawer[8];
		currentDrawer = 0;
		for (int i = 0; i < drawers.length; i++)
			drawers[i] = new WorldChunkDrawer();
		
		Timer.CELL_DRAW_AGGREGATED.timeStart();
		Timer.CELL_DRAW_AGGREGATED.timePause();
		Timer.TO_CAMERA_AGGREGATED.timeStart();
		Timer.TO_CAMERA_AGGREGATED.timePause();
		
		Timer.BACKGROUND.timeStart();
		drawBackground(painterQueue);
		Timer.BACKGROUND.timeEnd();
		
		Timer.CHUNKS.timeStart();
		drawChunks(painterQueue, c);
		Timer.CHUNKS.timeEnd();
		
		Timer.INTERFACE.timeStart();
		drawInterface(painterQueue);
		Timer.INTERFACE.timeEnd();
		
		Timer.CELL_DRAW_AGGREGATED.timePause();
		Timer.CELL_DRAW_AGGREGATED.timeEnd();
		Timer.TO_CAMERA_AGGREGATED.timePause();
		Timer.TO_CAMERA_AGGREGATED.timeEnd();
		
		for (WorldChunkDrawer drawer : drawers)
			new Thread(drawer).start();
		
		for (WorldChunkDrawer drawer : drawers)
			drawer.emptyAll(painterQueue);
	}
	
	private void drawBackground(PainterQueue painterQueue) {
		for (LList<InterfaceElement> e : backgroundElement)
			e.node.draw(painterQueue);
	}
	
	private void drawInterface(PainterQueue painterQueue) {
		for (LList<InterfaceElement> e : interfaceElement)
			e.node.draw(painterQueue);
	}
	
	private void drawChunks(PainterQueue painterQueue, Camera c) {
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
		
		Painter.DEBUG_STRING[1] = "(unit 100,000) volume raw " + volumeRaw + " ; (unit 100,000) volume bound " + volumeBound + " ; volume chunk " + volumeChunk;
		
		for (int x = fromChunkCoord[0]; x < cameraChunkCoord[0]; x++)
			drawChunksRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, Math3D.RIGHT);
		for (int x = toChunkCoord[0]; x > cameraChunkCoord[0]; x--)
			drawChunksRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, Math3D.LEFT);
		drawChunksRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cameraChunkCoord[0], Math3D.NONE);
	}
	
	private void drawChunksRow(PainterQueue painterQueue, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int x, int xSide) {
		for (int y = fromChunkCoord[1]; y < cameraChunkCoord[1]; y++)
			drawChunksColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, xSide, Math3D.BACK);
		for (int y = toChunkCoord[1]; y > cameraChunkCoord[1]; y--)
			drawChunksColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, xSide, Math3D.FRONT);
		drawChunksColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, cameraChunkCoord[1], xSide, Math3D.NONE);
	}
	
	private void drawChunksColumn(PainterQueue painterQueue, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int x, int y, int xSide, int ySide) {
		for (int z = fromChunkCoord[2]; z < cameraChunkCoord[2]; z++)
			drawChunkMulti(c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.TOP);
		for (int z = toChunkCoord[2]; z > cameraChunkCoord[2]; z--)
			drawChunkMulti(c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.BOTTOM);
		drawChunkMulti(c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, cameraChunkCoord[2], xSide, ySide, Math3D.NONE);
		
		//		for (int z = fromChunkCoord[2]; z < cameraChunkCoord[2]; z++)
		//			drawChunk(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.TOP);
		//		for (int z = toChunkCoord[2]; z > cameraChunkCoord[2]; z--)
		//			drawChunk(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, z, xSide, ySide, Math3D.BOTTOM);
		//		drawChunk(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, x, y, cameraChunkCoord[2], xSide, ySide, Math3D.NONE);
	}
	
	private void drawChunkMulti(Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide) {
		WorldChunkDrawerSetup setup = new WorldChunkDrawerSetup(c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide);
		drawers[currentDrawer].addChunk(setup);
		if (++currentDrawer == drawers.length)
			currentDrawer = 0;
	}
	
	private void drawChunk(PainterQueue painterQueue, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide) {
		if (chunk[cx][cy][cz] == null || chunk[cx][cy][cz].isEmpty())
			return;
		if (xSide == Math3D.RIGHT) {
			int startx = cx == fromChunkCoord[0] ? fromChunkCoord[3] : 0;
			for (int x = startx; x < CHUNK_SIZE; x++)
				drawRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x);
		} else if (xSide == Math3D.LEFT) {
			int endx = cx == toChunkCoord[0] ? toChunkCoord[3] : CHUNK_SIZE - 1;
			for (int x = endx; x >= 0; x--)
				drawRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x);
		} else {
			int startx = cx == fromChunkCoord[0] ? fromChunkCoord[3] : 0;
			int endx = cx == toChunkCoord[0] ? toChunkCoord[3] : CHUNK_SIZE - 1;
			for (int x = startx; x < cameraChunkCoord[3]; x++)
				drawRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, Math3D.RIGHT, ySide, zSide, x);
			for (int x = endx; x > cameraChunkCoord[3]; x--)
				drawRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, Math3D.LEFT, ySide, zSide, x);
			drawRow(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, cameraChunkCoord[3]);
		}
	}
	
	private void drawRow(PainterQueue painterQueue, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x) {
		if (ySide == Math3D.BACK) {
			int starty = cy == fromChunkCoord[1] ? fromChunkCoord[4] : 0;
			for (int y = starty; y < CHUNK_SIZE; y++)
				drawColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, y);
		} else if (ySide == Math3D.FRONT) {
			int endy = cy == toChunkCoord[1] ? toChunkCoord[4] : CHUNK_SIZE - 1;
			for (int y = endy; y >= 0; y--)
				drawColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, y);
		} else {
			int starty = cy == fromChunkCoord[1] ? fromChunkCoord[4] : 0;
			int endy = cy == toChunkCoord[1] ? toChunkCoord[4] : CHUNK_SIZE - 1;
			for (int y = starty; y < cameraChunkCoord[4]; y++)
				drawColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, Math3D.BACK, zSide, x, y);
			for (int y = endy; y > cameraChunkCoord[4]; y--)
				drawColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, Math3D.FRONT, zSide, x, y);
			drawColumn(painterQueue, c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide, x, cameraChunkCoord[4]);
		}
	}
	
	private void drawColumn(PainterQueue painterQueue, Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x, int y) {
		if (zSide == Math3D.TOP) {
			int startz = cz == fromChunkCoord[2] ? fromChunkCoord[5] : 0;
			for (int z = startz; z < CHUNK_SIZE; z++)
				drawCell(painterQueue, c, cx, cy, cz, xSide, ySide, zSide, x, y, z);
		} else if (zSide == Math3D.BOTTOM) {
			int endz = cz == toChunkCoord[2] ? toChunkCoord[5] : CHUNK_SIZE - 1;
			for (int z = endz; z >= 0; z--)
				drawCell(painterQueue, c, cx, cy, cz, xSide, ySide, zSide, x, y, z);
		} else {
			int startz = cz == fromChunkCoord[2] ? fromChunkCoord[5] : 0;
			int endz = cz == toChunkCoord[2] ? toChunkCoord[5] : CHUNK_SIZE - 1;
			for (int z = startz; z < cameraChunkCoord[5]; z++)
				drawCell(painterQueue, c, cx, cy, cz, xSide, ySide, Math3D.TOP, x, y, z);
			for (int z = endz; z > cameraChunkCoord[5]; z--)
				drawCell(painterQueue, c, cx, cy, cz, xSide, ySide, Math3D.BOTTOM, x, y, z);
			drawCell(painterQueue, c, cx, cy, cz, xSide, ySide, zSide, x, y, cameraChunkCoord[5]);
		}
	}
	
	private void drawCell(PainterQueue painterQueue, Camera c, int cx, int cy, int cz, int xSide, int ySide, int zSide, int x, int y, int z) {
		chunk[cx][cy][cz].draw(x, y, z, painterQueue, c, xSide, ySide, zSide);
	}
	
	// UPDATE
	
	public void initWorldElements() {
		for (LList<WorldElement> e : element)
			e.node.init(this);
	}
	
	public void update(Terrain terrain, Controller controller) {
		for (LList<WorldElement> e : element)
			e.node.update(this, terrain, controller);
		for (LList<Particle> p : particle)
			if (p.node.update(this))
				particle = particle.remove(p);
	}
	
	// UITL
	
	private int[] getChunkCoord(int x, int y, int z) {
		int cx = x / CHUNK_SIZE;
		int cy = y / CHUNK_SIZE;
		int cz = z / CHUNK_SIZE;
		x -= cx * CHUNK_SIZE;
		y -= cy * CHUNK_SIZE;
		z -= cz * CHUNK_SIZE;
		return new int[] {cx, cy, cz, x, y, z};
	}
	
	
	// todo: 1 painter queue per drawer instead of per setup
	private class WorldChunkDrawer implements Runnable {
		private LList<WorldChunkDrawerSetup> setup, setupTail;
		
		private WorldChunkDrawer() {
			setup = setupTail = new LList<>();
		}
		
		private void addChunk(WorldChunkDrawerSetup worldChunkDrawerSetup) {
			setup = setup.add(worldChunkDrawerSetup);
		}
		
		private void emptyAll(PainterQueue painterQueue) {
			for (LList<WorldChunkDrawerSetup> drawer : setupTail.reverseIterator()) {
				while (!drawer.node.painterQueue.drawReady)
					Math3D.sleep(5);
				painterQueue.add(drawer.node.painterQueue);
			}
		}
		
		public void run() {
			for (LList<WorldChunkDrawerSetup> drawer : setupTail.reverseIterator()) {
				drawChunk(drawer.node.painterQueue, drawer.node.c, drawer.node.fromChunkCoord, drawer.node.toChunkCoord, drawer.node.cameraChunkCoord, drawer.node.cx, drawer.node.cy, drawer.node.cz, drawer.node.xSide, drawer.node.ySide, drawer.node.zSide);
				drawer.node.painterQueue.drawReady = true;
			}
		}
	}
	
	private static class WorldChunkDrawerSetup {
		private PainterQueue painterQueue;
		private Camera c;
		private int[] fromChunkCoord, toChunkCoord, cameraChunkCoord;
		private int cx, cy, cz, xSide, ySide, zSide;
		
		private WorldChunkDrawerSetup(Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide) {
			painterQueue = new PainterQueue();
			this.c = c;
			this.fromChunkCoord = fromChunkCoord;
			this.toChunkCoord = toChunkCoord;
			this.cameraChunkCoord = cameraChunkCoord;
			this.cx = cx;
			this.cy = cy;
			this.cz = cz;
			this.xSide = xSide;
			this.ySide = ySide;
			this.zSide = zSide;
		}
	}
}
