package shapes;

import java.awt.*;

public class Flat extends Shape {
	private double x, y, z;
	private Surface surface;
	
	public Flat(double x, double y, double z, Color color) {
		this(x, y, z, color, .5);
	}
	
	public Flat(double x, double y, double z, Color color, double size) {
		super(null);
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (color == null)
			color = Color.LIGHT_GRAY;
		
		initSurface(color, size);
	}
	
	private void initSurface(Color color, double size) {
		// dimensions
		double leftX = x - size;
		double rightX = x + size;
		double frontY = y - size;
		double backY = y + size;
		
		// side coordinates
		double[] xs = new double[] {leftX, rightX, rightX, leftX};
		double[] ys = new double[] {backY, backY, frontY, frontY};
		
		// from back/left -> back/right -> front/right -> front/left
		surface = new Surface(xs, ys, z, true);
		surface.setColor(color);
		surface.setLight(.8);
	}
	
	Surface[] getSurfaces(int xSide, int ySide, int zSide) {
		return new Surface[] {surface};
	}
}

// todo: make more efficeint in hiding surfaces until world modified to make them visible