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
		int frame = 800, image = frame;
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
		WorldCreator wc = new WorldCreator(worldSize / World.CHUNK_SIZE, worldSize / World.CHUNK_SIZE, worldSize / World.CHUNK_SIZE);
		world = wc.world;
		hook = new Character(5, 5, 5);
		((TrailingCamera) camera).setFollow(hook);
		world.addElement(hook);
		world.initWorldElements();
		terrain.addToWorld(world);
	}
	
	void begin() {
		int frame = 0;
		long beginTime = 0, endTime;
		while (true) {
			while (pause) {
				checkPause();
				sleep(30);
			}
			painter.clear();
			camera.move(controller);
			camera.update(world.width, world.length, world.height);
			controller.setView(camera.angle, camera.orig(), camera.normal);
			world.update(terrain, controller);
			world.draw(painter, camera);
			painter.paint();
			checkPause();
			sleep(10);
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
	
	static void sleep(int howLong) {
		try {
			Thread.sleep(howLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void printInstructions() {
		System.out.println("Kami - Java 3D voxel platformer . jar 3");
		System.out.println("W A S D to move");
		System.out.println("R F to move camera up / down");
		System.out.println("Z X to move camera farther / nearer");
		System.out.println("Space tap to jump");
		System.out.println("Space press to jet pack");
		System.out.println("Space press while running into a wall to climb the wall");
		System.out.println("Move mouse to move camera and direction");
		System.out.println("Press shift or press mouse button to throw grappling hook");
	}
	
	public static void main(String args[]) {
		RaceEngine.printInstructions();
		new RaceEngine().begin();
	}
}