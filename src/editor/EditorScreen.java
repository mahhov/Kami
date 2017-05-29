package editor;

import control.InputControllerJava;
import list.LList;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

public class EditorScreen extends ScreenItem {
	LList<ScreenItem> item;
	
	public EditorScreen(double left, double top, double width, double height) {
		super(left, top, width, height);
		item = new LList<>();
		item = item.add(new ScreenButton(.25, .25, .1, .03, "HELLO BRO"));
	}
	
	public void handleMouseInput(InputControllerJava controller) {
		handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState());
	}
	
	void handleMouseInput(double screenX, double screenY, int mouseState) {
		for (LList<ScreenItem> i : item)
			i.node.handleMouseInput(screenX, screenY, mouseState);
	}
	
	public void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(0, 0, 1, 1, Color.WHITE));
		for (LList<ScreenItem> i : item)
			i.node.draw(painterQueue);
	}
	
}
