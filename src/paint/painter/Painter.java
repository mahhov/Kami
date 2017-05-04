package paint.painter;

import control.Controller;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Painter extends Runnable {
	public String[] DEBUG_STRING = new String[] {"", "", "", "", "", ""};
	public String[] OUTPUT_STRING = new String[] {"", "", "", "", "", ""};
	
	public void drawImage(BufferedImage image, int shift, int shiftVert);
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame);
	
	public void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame);
	
	public void drawLine(double x1, double y1, double x2, double y2, double light, Color color);
	
	public void drawRectangle(double x, double y, double width, double height, Color color);
	
	public void drawBlur(double blur);
	
	public void updateMode(Controller controller);
	
	public boolean isPainterQueueDone();
	
	public void setPainterQueue(PainterQueue painterQueue);
}
