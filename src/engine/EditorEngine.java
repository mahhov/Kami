package engine;

import camera.Camera;
import camera.FreeCamera;
import control.Controller;
import ships.ModelShip;
import world.World;

class EditorEngine extends Engine {
	ModelShip modelShip;
	
	EditorEngine() {
		super();
		createWorld();
	}
	
	EditorEngine(Controller controller, Painter painter) {
		super(controller, painter);
		createWorld();
	}
	
	ModelShip createShip(int x, int y, int z) {
		return new ModelShip(world);
	}
	
	Camera createCamera() {
		FreeCamera camera = new FreeCamera();
		return camera;
	}
	
	private void createWorld() {
		world = new World(1, 1, 1, 100);
		modelShip = createShip(0, 0, 0);
		world.addShip(modelShip);
	}
	
	void begin() {
		while (checkContinue()) {
			painter.clear();
			camera.move(controller);
			camera.update(modelShip.fullWidth, modelShip.fullLength, modelShip.fullHeight);
			controller.setView(camera.orig(), camera.normal);
			world.update(controller);
			world.drawChunks(painter, camera);
			painter.repaint();
			Painter.debugString[0] = "paint surfaceCount: " + painter.surfaceCount + " ; paint drawCount: " + painter.drawCount;
			wait(30);
		}
	}
	
	public static void main(String args[]) {
		new EditorEngine().begin();
	}
}