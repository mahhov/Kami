package engine;

import camera.TrailingCamera;
import character.Character;
import control.Controller;
import terrain.Terrain;
import world.World;
import world.WorldCreator;

class RaceEngine {
	private TrailingCamera camera;
	private Controller controller;
	private Painter painter;
	private World world;
	//	private Track track;
	private Terrain terrain;
	private Character hook;
	private boolean pause;
	
	RaceEngine() {
		int frame = 400, image = frame;
		Math3D.loadTrig(1000);
		controller = new Controller(frame, frame);
		painter = new Painter(frame, image, controller);
		camera = new TrailingCamera();
		// track = new Track();
		terrain = new Terrain();
		createWorld();
	}
	
	private void createWorld() {
		int worldSize = 750;
		int eachChunkSize = 5;
		int numChunks = worldSize / eachChunkSize;
		WorldCreator wc = new WorldCreator(numChunks, numChunks, numChunks, eachChunkSize);
		world = wc.world;
		hook = new Character(5, 5, 5);
		((TrailingCamera) camera).setFollow(hook);
		world.addElement(hook);
		terrain.addToWorld(world);
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
			controller.setView(camera.angle, camera.orig(), camera.normal);
			world.update(terrain, controller);
			world.drawChunks(painter, camera);
			painter.repaint();
			checkPause();
			wait(10);
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