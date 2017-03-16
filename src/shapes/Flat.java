package shapes;

import engine.Math3D;

import java.awt.*;

public class Flat extends Shape {
	private double x, y, z;
	private Surface surface;
	
	public Flat(double x, double y, double z, double[] norm, double size, Color color) {
		super(null);
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (color == null)
			color = Color.LIGHT_GRAY;
		
		initSurface(norm, size, color);
	}
	
	private void initSurface(double[] norm, double size, Color color) {
		// axis  vectors
		norm = Math3D.setMagnitude(norm, size);
		double[] right = Math3D.setMagnitude(new double[] {norm[1], -norm[0], 0}, size);
		
		// coordinates
		double[] leftFront = new double[] {x - norm[0] - right[0], y - norm[1] - right[1], z - norm[2] - right[2]};
		double[] rightFront = new double[] {x - norm[0] + right[0], y - norm[1] + right[1], z - norm[2] + right[2]};
		double[] leftBack = new double[] {x + norm[0] - right[0], y + norm[1] - right[1], z + norm[2] - right[2]};
		double[] rightBack = new double[] {x + norm[0] + right[0], y + norm[1] + right[1], z + norm[2] + right[2]};
		
		// surface
		surface = new Surface(leftBack, rightBack, rightFront, leftFront, true);
		surface.setColor(color);
		surface.setLight(.8);
	}
	
	Surface[] getSurfaces(int xSide, int ySide, int zSide) {
		return new Surface[] {surface};
	}
}

// todo: make more efficeint in hiding surfaces until world modified to make them visible