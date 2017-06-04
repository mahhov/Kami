package editor;

import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

class ScreenImageContainer extends ScreenItem {
	private ImageProvider imageProvider;
	private boolean frame;
	
	ScreenImageContainer(ImageProvider imageProvider, boolean frame) {
		this.imageProvider = imageProvider;
		this.frame = frame;
	}
	
	void draw(PainterQueue painterQueue) {
		if (frame) {
			painterQueue.add(new PainterRectangle(left, top, width, height, Color.WHITE));
			painterQueue.add(new PainterRectangle(left, top, width, height, Color.BLACK, false));
		}
		imageProvider.provideImage(painterQueue, left, top, width, height, true);
	}
}
