package engine;

import ambient.Blur;
import ambient.Sky;
import camera.TrailingCamera;
import character.Character;
import control.Controller;
import control.ControllerJava;
import control.ControllerLwjgl;
import paint.painter.PainterLwjgl;
import paint.painter.Painter;
import paint.painterelement.PainterQueue;
import terrain.Terrain;
import world.World;
import world.WorldCreator;

class KamiEngine {
	private static final int FRAME = 800, IMAGE = FRAME;
	
	private TrailingCamera camera;
	private ControllerLwjgl controller;
	private PainterLwjgl painter;
	private World world;
	private Terrain terrain;
	private Character character;
	private boolean pause;
	
	private KamiEngine() {
		Math3D.loadTrig(1000);
		controller = new ControllerLwjgl(FRAME, FRAME);
		painter = new PainterLwjgl(FRAME, IMAGE, controller);
		camera = new TrailingCamera();
		terrain = new Terrain();
		createWorld();
	}
	
	private void createWorld() {
		WorldCreator wc = new WorldCreator(terrain.width / World.CHUNK_SIZE, terrain.length / World.CHUNK_SIZE, terrain.height / World.CHUNK_SIZE);
		world = wc.world;
		character = new Character(5, 5, 5);
		camera.setFollow(character);
		world.addElement(character);
		world.addElement(new Sky(FRAME));
		world.addElement(new Blur(character));
		world.initWorldElements();
	}
	
	private void begin() {
		System.out.println("Begin");
		//		Music.BGMUSIC.play();
		int frame = 0, engineFrame = 0;
		long beginTime = 0, endTime;
		//		new Thread(painter).start();
		while (painter.running) {
			checkPause();
			while (pause) {
				checkPause();
				Math3D.sleep(30);
			}
			Timer.LOOP_1.timeStart();
			camera.move(controller);
			camera.update(world.width, world.length, world.height);
			controller.setView(camera.angle, camera.angleZ, camera.orig(), camera.normal);
			world.update(terrain, controller);
			Timer.LOOP_1.timeEnd();
			terrain.expand((int) character.getX(), (int) character.getY(), (int) character.getZ(), world);
			painter.updateMode(controller);
			
			if (painter.isPainterQueueDone()) {
				PainterQueue painterQueue = new PainterQueue();
				Timer.WORLD_DRAW.timeStart();
				world.draw(painterQueue, camera);
				Timer.WORLD_DRAW.timeEnd();
				painterQueue.drawReady = true;
				painter.setPainterQueue(painterQueue);
				painter.run();
				frame++;
			}
			engineFrame++;
			checkWriteTimer();
			Math3D.sleep(10);
			endTime = System.nanoTime() + 1;
			if (endTime - beginTime > 1000000000L) {
				Painter.DEBUG_STRING[0] = "draw fps: " + frame + " ; engine fps: " + engineFrame;
				frame = 0;
				engineFrame = 0;
				beginTime = endTime;
			}
		}
	}
	
	private void checkPause() {
		if (controller.isKeyPressed(Controller.KEY_P))
			pause = !pause;
	}
	
	private void checkWriteTimer() {
		if (controller.isKeyPressed(Controller.KEY_ENTER))
			Timer.writeFile();
	}
	
	private static void printInstructions() {
		System.out.println("Kami - Java 3D voxel platformer . jar 4");
		System.out.println("W A S D to move");
		System.out.println("R F to move camera up / down");
		System.out.println("Z X to move camera farther / nearer");
		System.out.println("Press / to toggle wire mode");
		System.out.println("Press . to toggle blur mode");
		System.out.println("Space tap to jump");
		System.out.println("Space press to jet pack");
		System.out.println("Space press while running into a wall to climb the wall");
		System.out.println("Move mouse to move camera and direction");
		System.out.println("Press shift or press mouse button to throw grappling hook");
		
		System.out.println("temp - 1 to switch extra paint mode");
		System.out.println("temp - enter to write out timer log");
	}
	
	public static void main(String args[]) {
		KamiEngine.printInstructions();
		new KamiEngine().begin();
	}
}

// todo : graphics
// todo : shooting
// todo : survival
// todo : environment (e.g. clouds, zone types, world generation)
// todo : true boundless terrain/world
// todo : border drawing
// todo : max range to hook, and check intersection finder for slow down bug
// todo : toggle controls for bg music and sound affects
// todo : multi cube character collision
// todo : flying sound affect
// todo : moving character animation
// todo : cell shading
// todo : spawn center terrain
// todo : flying hiss sound
// todo : skate state
// todo : race mode