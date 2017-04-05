package engine;

import control.Controller;
import shapes.Surface;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import static camera.Camera.MIN_LIGHT;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class Painter extends JFrame {
	public static String[] debugString = new String[] {"", "", "", "", "", ""};
	public static String[] outputString = new String[] {"", "", "", "", "", ""};
	
	private final int FRAME_SIZE, IMAGE_SIZE;
	private static int borderSize = 0;
	private BufferedImage canvas;
	Graphics2D brush;
	private Graphics frameBrush;
	int surfaceCount, drawCount;
	Area clip;
	
	Painter(int frameSize, int imageSize, Controller controller) {
		FRAME_SIZE = frameSize;
		IMAGE_SIZE = imageSize;
		canvas = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, TYPE_INT_RGB);
		brush = (Graphics2D) canvas.getGraphics();
		getContentPane().setSize(FRAME_SIZE, FRAME_SIZE);
		
		System.out.println(this.getGraphics());
		pack();
		System.out.println(this.getGraphics());
		
		borderSize = getHeight();
		setSize(FRAME_SIZE, FRAME_SIZE + borderSize);
		setLocationRelativeTo(null);
		addMouseListener(controller);
		addKeyListener(controller);
		addMouseMotionListener(controller);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIgnoreRepaint(true);
		setVisible(true);
		frameBrush = this.getGraphics();
	}
	
	void clear() {
		surfaceCount = 0;
		drawCount = 0;
		//		 brush.setColor(new Color(0f, 0f, 0f, .2f));
		brush.setColor(Color.BLACK);
		brush.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
	}
	
	public void paint() {
		brush.setColor(Color.WHITE);
		for (int i = 0; i < debugString.length; i++)
			brush.drawString(debugString[i], 25, 25 + 25 * i);
		for (int i = 0; i < outputString.length; i++)
			brush.drawString(outputString[i], 25, 650 + 25 * i);
		frameBrush.drawImage(canvas, 0, borderSize, null);
	}
	
	private void setColor(double light, Color color) {
		if (light < MIN_LIGHT)
			brush.setColor(Color.black);
		else {
			light = Math3D.min(1, light);
			brush.setColor(new Color((int) (light * color.getRed()), (int) (light * color.getGreen()), (int) (light * color.getBlue())));
		}
	}
	
	public void polygon(double[][] xy, double light, Color color, boolean frame) {
		if (xy != null) {
			surfaceCount++;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5 && xy[1][i] > -.5) {
					drawCount++;
					int[][] xyScaled = Math3D.transform(xy, IMAGE_SIZE, IMAGE_SIZE / 2);
					if (frame) {
						brush.setColor(Color.cyan);
						brush.drawPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
					} else {
						setColor(light, color);
						brush.fillPolygon(xyScaled[0], xyScaled[1], xyScaled[0].length);
					}
					return;
				}
		}
	}
	
	public void clipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
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
			polygon(xy, light, color, frame);
			if (clipState == Surface.CLIP_RESET) {
				clip = new Area();
				brush.setClip(null);
			}
		}
	}
	
	public void rectangle(double x, double y, double width, double height, Color color) {
		int xywh[] = Math3D.transform(new double[] {x, y, width, height}, IMAGE_SIZE);
		brush.setColor(color);
		brush.fillRect(xywh[0], xywh[1], xywh[2], xywh[3]);
	}
}
