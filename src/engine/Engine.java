package engine;

import camera.Camera;
import camera.FreeCamera;
import camera.TrailingCamera;
import control.Controller;
import ships.FileShip;
import ships.Ship;
import world.World;
import world.WorldCreator;

class Engine {
	Camera camera;
	Controller controller;
	Painter painter;
	World world;
	Ship ship;
	private boolean pause;
	
	Engine() {
		int frame = 800, image = frame;
		Math3D.loadTrig(1000);
		controller = new Controller(frame, frame);
		painter = new Painter(frame, image, controller);
		camera = createCamera();
	}
	
	Engine(Controller controller, Painter painter) {
		this.controller = controller;
		this.painter = painter;
		camera = createCamera();
	}
	
	Ship createShip(int x, int y, int z) {
		return new FileShip(x, y, z, 0, 0, 0, world);
	}
	
	Camera createCamera() {
		return new TrailingCamera();
	}
	
	private void createWorld() {
		int worldSize = 750;
		int eachChunkSize = 5;
		int numChunks = worldSize / eachChunkSize;
		WorldCreator wc = new WorldCreator(numChunks, numChunks, numChunks, eachChunkSize);
		wc.heightMap(65, 25);
		world = wc.world;
		ship = createShip(32, 32, 100);
		((TrailingCamera) camera).setFollowShip(ship);
		world.addShip(ship);
	}
	
	void begin() {
		createWorld();
		int frame = 0;
		long beginTime = 0, endTime;
		while (checkContinue()) {
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
	
	boolean checkContinue() {
		return !controller.isKeyPressed(Controller.KEY_M);
	}
	
	static void wait(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new Engine().begin();
	}
}

// TODO : don't keep throwing away old buffered image and graphics2d