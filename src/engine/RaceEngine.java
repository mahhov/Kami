package engine;

import camera.Camera;
import camera.FreeCamera;
import control.Controller;
import ships.FileShip;
import ships.Ship;
import track.Track;
import world.World;
import world.WorldCreator;

class RaceEngine {
	private Camera camera;
	private Controller controller;
	private Painter painter;
	private World world;
	private Track track;
	private Ship ship;
	private boolean pause;
	
	RaceEngine() {
		int frame = 800, image = frame;
		Math3D.loadTrig(1000);
		controller = new Controller(frame, frame);
		painter = new Painter(frame, image, controller);
		//		camera = new TrailingCamera();
		camera = new FreeCamera();
		track = new Track();
		createWorld();
	}
	
	private void createWorld() {
		int worldSize = 750;
		int eachChunkSize = 5;
		int numChunks = worldSize / eachChunkSize;
		WorldCreator wc = new WorldCreator(numChunks, numChunks, numChunks, eachChunkSize);
		world = wc.world;
		ship = new FileShip(32, 32, 100, 0, 0, 0, world);
		//		((TrailingCamera) camera).setFollowShip(ship);
		world.addShip(ship);
		track.addToWorld(world);
	}
	
	void begin() {
		int frame = 0;
		long beginTime = 0, endTime;
		while (true) {
			while (pause) {
				checkPause();
				wait(30);
			}
			painter.clear();
			camera.move(controller);
			camera.update(world.width, world.length, world.height);
			world.update(controller);
			world.drawChunks(painter, camera);
			painter.repaint();
			checkPause();
			wait(30);
			endTime = System.nanoTime() + 1;
			if (endTime - beginTime > 1000000000L) {
				Painter.debugString[0] = "fps: " + frame + " ; paint surfaceCount: " + painter.surfaceCount + " ; paint drawCount: " + painter.drawCount;
				frame = 0;
				beginTime = endTime;
			}
			frame++;
		}
	}
	
	private void checkPause() {
		if (controller.isKeyPressed(Controller.KEY_P))
			pause = !pause;
	}
	
	static void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new RaceEngine().begin();
	}
}