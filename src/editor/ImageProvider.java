package editor;

import paint.painterelement.PainterQueue;

interface ImageProvider {
	void provideImage(PainterQueue painterQueue, double left, double top, double width, double height, boolean all);
}