package engine;

import control.Controller;

public class Client {
	Engine engine;
	EditorEngine eengine;
	Painter painter;
	Controller controller;
	
	Client() {
		int frame = 800, image = frame;
		Math3D.loadTrig(1000);
		controller = new Controller(frame, frame);
		painter = new Painter(frame, image, controller);
		engine = new Engine(controller, painter);
		eengine = new EditorEngine(controller, painter);
	}
	
	void begin() {
		while (true) {
			eengine.begin();
			engine.begin();
		}
	}
	
	public static void main(String[] args) {
		new Client().begin();
	}
}
