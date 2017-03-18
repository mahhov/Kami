package shapes;

import engine.Math3D;

import java.awt.*;

public class CubeFrame extends Cube {
	public CubeFrame(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double size, ShapeParent shapeParent) {
		super(x, y, z, angle, angleZ, angleTilt, size, shapeParent);
	}
	
	public CubeFrame(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double size, boolean[] side, Color[] color, ShapeParent shapeParent) {
		super(x, y, z, angle, angleZ, angleTilt, size, side, color, shapeParent);
	}
	
	void initSurfaces() {
		super.initSurfaces();
		for (Surface s : surface)
			if (s != null)
				s.frame = true;
	}
}
