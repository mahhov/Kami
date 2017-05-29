package editor;

import list.LList;
import paint.painterelement.PainterQueue;

public class EditorScreen extends ScreenItem {
	LList<ScreenItem> item;
	
	public EditorScreen(double left, double top, double width, double height) {
		super(left, top, width, height);
		item = new LList<>();
		item = item.add(new ScreenButton(.25, .25, .1, .03, "HELLO BRO"));
	}
	
	public void handleMouseClick(double screenX, double screenY) {
	}
	
	public void draw(PainterQueue painterQueue) {
		for (LList<ScreenItem> i : item)
			i.node.draw(painterQueue);
	}
	
}
