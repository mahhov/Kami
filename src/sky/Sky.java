package sky;

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
	private int shift;
	
	public Sky(int size) {
		WIDTH = size * 7; // 2 * pi / (atan(.5) * 2) = 6.776
		makeBackgroundImage(size);
	}
	
	private void makeBackgroundImage(int size) {
		// init
		backgroundImage = new BufferedImage(WIDTH * 2, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D brush = (Graphics2D) backgroundImage.getGraphics();
		// random stars
		int colorMax = 150;
		for (int i = 0; i < 100; i++) {
			brush.setStroke(new BasicStroke((int) (Math.random() * 10)));
			brush.setColor(new Color((int) (Math.random() * colorMax), (int) (Math.random() * colorMax), (int) (Math.random() * colorMax)));
			int r = Math3D.rand(10);
			brush.drawArc(Math3D.rand(WIDTH), Math3D.rand(size), r, r, 0, 360);
		}
		// big stars
		brush.setStroke(new BasicStroke(5));
		brush.setColor(new Color(255, 255, 255));
		brush.drawArc(WIDTH / 2 + 5, size / 2 - 200, 200, 200, 0, 360);
		brush.setStroke(new BasicStroke(5));
		brush.setColor(new Color(255, 0, 0));
		brush.drawArc(5, size / 2 - 200, 200, 200, 0, 360);
		
		// loop around
		brush.drawImage(backgroundImage, WIDTH, 0, WIDTH * 2, size, 0, 0, WIDTH,
				size, null);
	}
	
	private void paintBackground(Controller c) {
		double anglePercent = -(c.viewAngle.get() / Math.PI / 2) % 1;
		if (anglePercent < 0)
			anglePercent++;
		shift = (int) (anglePercent * WIDTH);
	}
	
	public void init(World world) {
		world.addBackgroundElement(this);
	}
	
	public void update(World world, Terrain terrain, Controller controller) {
		paintBackground(controller);
	}
	
	public void draw(Painter painter) {
		painter.drawImage(backgroundImage, shift);
	}
}
