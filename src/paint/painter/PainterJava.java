package paint.painter;

import control.Controller;
import control.ControllerJavaListener;
import engine.Math3D;
import engine.Timer;
import paint.painterelement.PainterQueue;
import shapes.drawelement.Surface;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import static camera.Camera.MIN_LIGHT;

public class PainterJava implements Painter {
	// paint mode
	private static final String[] wireString = new String[] {"NORMAL", "WIRE", "NORMAL + WIRE"};
	private static final int WIRE_ONLY = 1, WIRE_AND = 2;
	private int wireMode;
	private static final AlphaComposite BLUR_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f);
	private static final String[] blurString = new String[] {"OFF", "ON ALWAYS", "ON DYNAMIC"};
	private static final int BLUR_OFF = 0, BLUR_FULL = 1, BLUR_DYNAMIC = 2;
	private int blurMode;
	
	private final JFrame jframe;
	private final int FRAME_SIZE, IMAGE_SIZE;
	private static int borderSize = 0;
	private BufferedImage canvas;
	private Graphics2D brush;
	private Graphics2D frameBrush;
	private FontMetrics fontMetrics;
	private int surfaceCount, drawCount;
	private Area clip;
	private BufferedImage backgroundImage;
	
	private PainterQueue painterQueue;
	
	public PainterJava(int frameSize, int imageSize, ControllerJavaListener controller) {
		jframe = new JFrame();
		setPaintModeString();
		FRAME_SIZE = frameSize;
		IMAGE_SIZE = imageSize;
		canvas = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		brush = (Graphics2D) canvas.getGraphics();
		fontMetrics = brush.getFontMetrics();
		jframe.setUndecorated(true);
		jframe.getContentPane().setSize(FRAME_SIZE, FRAME_SIZE);
		jframe.pack();
		borderSize = jframe.getHeight();
		jframe.setSize(FRAME_SIZE, FRAME_SIZE + borderSize);
		jframe.setLocationRelativeTo(null);
		jframe.addMouseListener(controller);
		jframe.addKeyListener(controller);
		jframe.addMouseMotionListener(controller);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setIgnoreRepaint(true);
		jframe.setVisible(true);
		frameBrush = (Graphics2D) jframe.getGraphics();
		painterQueue = new PainterQueue();
	}
	
	public void paint() {
		brush.setColor(Color.WHITE);
		for (int i = 0; i < DEBUG_STRING.length; i++)
			brush.drawString(DEBUG_STRING[i], 25, 25 + 25 * i);
		for (int i = 0; i < OUTPUT_STRING.length; i++)
			brush.drawString(OUTPUT_STRING[i], 25, 650 + 25 * i);
		frameBrush.drawImage(canvas, 0, borderSize, null);
		frameBrush.setComposite(BLUR_COMPOSITE);
		
		//		frameBrush.drawImage(canvas, 0, borderSize, FRAME_SIZE, borderSize + FRAME_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
	}
	
	private void setColor(double light, Color color) {
		if (light < MIN_LIGHT)
			brush.setColor(Color.black);
		else {
			light = Math3D.min(1, light);
			brush.setColor(new Color((int) (light * color.getRed()), (int) (light * color.getGreen()), (int) (light * color.getBlue()), color.getAlpha()));
		}
	}
	
	public void setBackgroundImage(BufferedImage image) {
		backgroundImage = image;
	}
	
	public void drawBackgroundImage(int shift, int shiftVert) {
		brush.drawImage(backgroundImage, 0, 0, 800, 800, shift, shiftVert, shift + 800, shiftVert + 800, null);
	}
	
	public void drawImage(BufferedImage image, double dstx, double dsty, double width, double height) {
		brush.drawImage(image, (int) (dstx * FRAME_SIZE), (int) (dsty * FRAME_SIZE), (int) ((dstx + width) * FRAME_SIZE), (int) ((dsty + height) * FRAME_SIZE), 0, 0, image.getWidth(), image.getHeight(), null);
	}
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame) {
		if (xy != null) {
			surfaceCount++;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5 && xy[1][i] > -.5) {
					drawCount++;
					int[][] xyScaled = Math3D.transform(xy, IMAGE_SIZE, IMAGE_SIZE / 2);
					brush.setStroke(new BasicStroke(1));
					if (frame) {
						brush.setColor(Color.CYAN);
						brush.drawPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
					} else {
						setColor(light, color);
						brush.fillPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
					}
					return;
				}
		}
	}
	
	public void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
		if (clipState == Surface.CLIP_ADD) {
			if (clip == null)
				clip = new Area();
			if (xy != null) {
				int[][] xyScaled = Math3D.transform(xy, IMAGE_SIZE, IMAGE_SIZE / 2);
				clip.add(new Area(new Polygon(xyScaled[0], xyScaled[1], xyScaled[0].length)));
			}
		} else {
			if (clipState == Surface.CLIP_SET)
				brush.setClip(clip);
			drawPolygon(xy, light, color, frame || wireMode == WIRE_ONLY);
			if (wireMode == WIRE_AND && !frame)
				drawPolygon(xy, light, color, true);
			if (clipState == Surface.CLIP_RESET) {
				clip = new Area();
				brush.setClip(null);
			}
		}
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, double light, Color color) {
		surfaceCount++;
		if ((x1 > -.5 && x1 < .5 && y1 < .5 && y1 > -.5) || (x2 > -.5 && x2 < .5 && y2 < .5 && y2 > -.5)) {
			drawCount++;
			int[] xyScaled = Math3D.transform(new double[] {x1, y1, x2, y2}, IMAGE_SIZE, IMAGE_SIZE / 2);
			setColor(light, color);
			brush.setStroke(new BasicStroke(10));
			brush.drawLine(xyScaled[0], xyScaled[1], xyScaled[2], xyScaled[3]);
		}
	}
	
	public void drawRectangle(double x, double y, double width, double height, Color color) {
		int xywh[] = Math3D.transform(new double[] {x, y, width, height}, IMAGE_SIZE);
		brush.setColor(color);
		brush.fillRect(xywh[0], xywh[1], xywh[2], xywh[3]);
	}
	
	public void drawRectangleFrame(double x, double y, double width, double height, Color color) {
		int xywh[] = Math3D.transform(new double[] {x, y, width, height}, IMAGE_SIZE);
		brush.setColor(color);
		brush.drawRect(xywh[0], xywh[1], xywh[2], xywh[3]);
	}
	
	public void drawBlur(double blur) {
		if (blurMode == BLUR_DYNAMIC) {
			blur = Math3D.maxMin(blur, 1, .1);
			//			frameBrush.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) blur));
		}
	}
	
	public void drawText(double x, double y, Color color, String text) {
		int xy[] = Math3D.transform(new double[] {x, y}, IMAGE_SIZE);
		brush.setColor(color);
		brush.drawString(text, xy[0] + 10, xy[1] + 20);
	}
	
	public void drawTextCentered(double x, double y, double width, double height, Color color, String text) {
		int xywh[] = Math3D.transform(new double[] {x, y, width, height}, IMAGE_SIZE);
		brush.setColor(color);
		int textWidth = fontMetrics.stringWidth(text);
		brush.drawString(text, xywh[0] + (xywh[2] - textWidth) / 2, xywh[1] + 5 + xywh[3] / 2);
	}
	
	public void updateMode(Controller controller) {
		if (controller.isKeyPressed(Controller.KEY_SLASH)) {
			if (++wireMode == 3)
				wireMode = 0;
			setPaintModeString();
		}
		if (controller.isKeyPressed(Controller.KEY_RIGHT_CAROT)) {
			if (++blurMode == 3)
				blurMode = 0;
			//			if (blurMode == BLUR_FULL)
			//				frameBrush.setComposite(BLUR_COMPOSITE);
			//			else if (blurMode == BLUR_OFF)
			//				frameBrush.setComposite(AlphaComposite.Src);
			setPaintModeString();
		}
	}
	
	private void setPaintModeString() {
		DEBUG_STRING[4] = "wire mode " + wireString[wireMode] + " (press / to toggle)  :  blur " + blurString[blurMode] + " (press . to toggle)";
	}
	
	public boolean isPainterQueueDone() {
		return !painterQueue.drawReady;
	}
	
	public void setPainterQueue(PainterQueue painterQueue) {
		this.painterQueue = painterQueue;
	}
	
	public void run() {
		while (true) {
			if (painterQueue.drawReady) {
				surfaceCount = drawCount = 0;
				Timer.PAINTER_QUEUE_PAINT.timeStart();
				painterQueue.draw(this);
				Timer.PAINTER_QUEUE_PAINT.timeEnd();
				painterQueue.drawReady = false;
				DEBUG_STRING[5] = "paint surfaceCount: " + surfaceCount + " ; paint drawCount: " + drawCount;
				Timer.PAINT.timeStart();
				paint();
				Timer.PAINT.timeEnd();
			}
			Math3D.sleep(10);
		}
	}
}