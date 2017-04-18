package shapes;

import engine.Math3D;

import java.awt.*;

public class Cuboid extends Cube {
	double width, length, height;
	
	public Cuboid(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double width, double length, double height, boolean[] side, Color[] color, ShapeParent shapeParent) {
		super(x, y, z, angle, angleZ, angleTilt, 0, side, color, shapeParent);
		this.width = width;
		this.length = length;
		this.height = height;
	}
	
	void initSurfaces() {
		surface = initSurfacesGeom(angle, angleZ, angleTilt, length, width, height, side, false, false);
		for (int i = 0; i < surface.length; i++)
			if (surface[i] != null) {
				surface[i].setColor(color[i]);
				surface[i].setLight(1);
			}
	}
}