package editor;

import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

class ScreenImageContainer extends ScreenItem {
	private ImageProvider imageProvider;
	
	ScreenImageContainer(ImageProvider imageProvider) {
		this.imageProvider = imageProvider;
	}
	
	void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.WHITE));
		imageProvider.provideImage(painterQueue, left, top, width, height, true);
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.BLACK, false));
	}
}
