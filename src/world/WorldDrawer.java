package world;

import camera.Camera;
import list.LList;
import paint.painterelement.PainterQueue;

import java.util.Iterator;

class WorldDrawer {
	private World world;
	private WorldChunkDrawer[] drawers;
	private int currentDrawer;
	
	WorldDrawer(World world) {
		this.world = world;
	}
	
	void prepareChunkQueue() {
		drawers = new WorldChunkDrawer[8]; // todo: test what number of threads is optimal
		currentDrawer = 0;
		for (int i = 0; i < drawers.length; i++)
			drawers[i] = new WorldChunkDrawer();
	}
	
	void queueChunk(Camera c, int[] fromChunkCoord, int[] toChunkCoord, int[] cameraChunkCoord, int cx, int cy, int cz, int xSide, int ySide, int zSide) {
		WorldChunkDrawerSetup setup = new WorldChunkDrawerSetup(c, fromChunkCoord, toChunkCoord, cameraChunkCoord, cx, cy, cz, xSide, ySide, zSide);
		drawers[currentDrawer].addChunk(setup);
		if (++currentDrawer == drawers.length)
			currentDrawer = 0;
	}
	
	void flushChunkQueue(PainterQueue painterQueue) {
		for (WorldChunkDrawer drawer : drawers)
			drawer.thread.start();
		
		for (WorldChunkDrawer drawer : drawers)
			try {
				drawer.thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		boolean complete;
		do {
			complete = true;
			for (WorldChunkDrawer drawer : drawers)
				if (!drawer.doneEmptying) {
					complete = false;
					drawer.emptyNextChunkPainterQueue(painterQueue);
				}
		} while (!complete);
	}
	
	private class WorldChunkDrawer implements Runnable {
		private LList<WorldChunkDrawerSetup> setup, setupTail;
		private Iterator<LList<WorldChunkDrawerSetup>> steupTailIterator;
		private boolean doneEmptying;
		private Thread thread;
		
		private WorldChunkDrawer() {
			setup = setupTail = new LList<>();
			doneEmptying = false;
			thread = new Thread(this);
		}
		
		private void addChunk(WorldChunkDrawerSetup worldChunkDrawerSetup) {
			setup = setup.add(worldChunkDrawerSetup);
		}
		
		private void emptyNextChunkPainterQueue(PainterQueue painterQueue) {
			if (!steupTailIterator.hasNext())
				doneEmptying = true;
			else
				painterQueue.add(steupTailIterator.next().node.painterQueue);
		}
		
		public void run() {
			for (LList<WorldChunkDrawerSetup> drawer : setup) {
				world.drawChunk(drawer.node.painterQueue, drawer.node.c, drawer.node.fromChunkCoord, drawer.node.toChunkCoord, drawer.node.cameraChunkCoord, drawer.node.cx, drawer.node.cy, drawer.node.cz, drawer.node.xSide, drawer.node.ySide, drawer.node.zSide);
				drawer.node.painterQueue.drawReady = true;
			}
			steupTailIterator = setupTail.reverseIterator().iterator();
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
