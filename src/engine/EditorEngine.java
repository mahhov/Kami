package engine;

import control.InputControllerJava;
import editor.EditorScreen;
import paint.painter.Painter;
import paint.painter.PainterJava;
import paint.painterelement.PainterQueue;

class EditorEngine implements Runnable {
	private static final int FRAME = 800, IMAGE = FRAME;
	
	private EditorScreen editorScreen;
	private InputControllerJava controller;
	private Painter painter;
	
	private EditorEngine() {
		Math3D.loadTrig(1000);
		editorScreen = new EditorScreen(0, 0, 1, 1);
		controller = new InputControllerJava(FRAME, FRAME);
		painter = new PainterJava(FRAME, IMAGE, controller);
	}
	
	private void begin() {
		System.out.println("Begin Editor Engine");
		new Thread(this).start();
		painter.run();
	}
	
	public void run() {
		int frame = 0, engineFrame = 0;
		long beginTime = 0, endTime;
		while (true) {
			if (painter.isPainterQueueDone()) {
				PainterQueue painterQueue = new PainterQueue();
				editorScreen.draw(painterQueue);
				painterQueue.drawReady = true;
				painter.setPainterQueue(painterQueue);
				frame++;
			}
			editorScreen.update(controller);
			engineFrame++;
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
	
	public static void main(String args[]) {
		new EditorEngine().begin();
	}
}