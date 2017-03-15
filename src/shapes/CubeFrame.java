package shapes;

import engine.Math3D;
import ships.Ship;

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
		for (int i = 0; i < surface.length; i++)
			if (surface[i] != null)
				surface[i].frame = true;
	}
}
