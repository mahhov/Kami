package paint.painter;

import control.Controller;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Painter extends Runnable {
	String[] DEBUG_STRING = new String[] {"", "", "", "", "", ""};
	String[] OUTPUT_STRING = new String[] {"", "", "", "", "", ""};
	
	void setBackgroundImage(BufferedImage image);
	
	void drawBackgroundImage(int shift, int shiftVert);
	
	void drawImage(BufferedImage image, int shift, int shiftVert);
	
	void drawPolygon(double[][] xy, double light, Color color, boolean frame);
	
	void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame);
	
	void drawLine(double x1, double y1, double x2, double y2, double light, Color color);
	
	void drawRectangle(double x, double y, double width, double height, Color color);
	
	void drawBlur(double blur);
	
	void updateMode(Controller controller);
	
	boolean isPainterQueueDone();
	
	void setPainterQueue(PainterQueue painterQueue);
}
