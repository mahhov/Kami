package ambient;

import control.Controller;
import engine.Math3D;
import engine.Painter;
import terrain.Terrain;
import world.World;
import world.WorldElement;
import world.interfaceelement.InterfaceElement;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sky implements WorldElement, InterfaceElement {
	private final int WIDTH;
	private BufferedImage backgroundImage;
	private int shift, shiftVert;
	
	public Sky(int size) {
		WIDTH = size * 7; // 2 * pi / (atan(.5) * 2) = 6.776
		makeBackgroundImage();
	}
	
	private void makeBackgroundImage() {
		// init
		backgroundImage = new BufferedImage(WIDTH * 2, WIDTH * 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D brush = (Graphics2D) backgroundImage.getGraphics();
		// background
		brush.setColor(new Color(30, 0, 50));
		brush.fillRect(0, 0, WIDTH, WIDTH);
		// random stars
		int colorMax = 150;
		for (int i = 0; i < 1000; i++) {
			brush.setStroke(new BasicStroke((int) (Math.random() * 10)));
			brush.setColor(new Color((int) (Math.random() * colorMax), (int) (Math.random() * colorMax), (int) (Math.random() * colorMax)));
			int r = Math3D.rand(10);
			brush.drawArc(Math3D.rand(WIDTH), Math3D.rand(WIDTH), r, r, 0, 360);
		}
		// big stars
		int r = 200;
		brush.setStroke(new BasicStroke(5));
		brush.setColor(Color.WHITE);
		brush.drawArc(WIDTH / 2 + 5, 5, r, r, 0, 360);
		brush.setStroke(new BasicStroke(5));
		brush.setColor(Color.RED);
		brush.drawArc(5, 5, r, r, 0, 360);
		
		// loop around
		brush.drawImage(backgroundImage, WIDTH, 0, WIDTH * 2, WIDTH, 0, 0, WIDTH, WIDTH, null); // right
		brush.drawImage(backgroundImage, 0, WIDTH, WIDTH, WIDTH * 2, 0, 0, WIDTH, WIDTH, null); // bottom
		brush.drawImage(backgroundImage, WIDTH, WIDTH, WIDTH * 2, WIDTH * 2, 0, 0, WIDTH, WIDTH, null); // right + bottom
	}
	
	private void paintBackground(Controller c) {
		double anglePercent = -(c.viewAngle.get() / Math.PI / 2) % 1;
		if (anglePercent < 0)
			anglePercent++;
		shift = (int) (anglePercent * WIDTH);
		
		anglePercent = -(c.viewAngleZ.get() / Math.PI / 2) % 1;
		if (anglePercent < 0)
			anglePercent++;
		shiftVert = (int) (anglePercent * WIDTH);
	}
	
	public void init(World world) {
		world.addBackgroundElement(this);
	}
	
	public void update(World world, Terrain terrain, Controller controller) {
		paintBackground(controller);
	}
	
	public void draw(Painter painter) {
		painter.drawImage(backgroundImage, shift, shiftVert);
	}
}
