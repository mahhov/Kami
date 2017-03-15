package shapes;

import engine.Math3D;

import java.awt.*;

public class Bar extends Cube {
	private Math3D.Angle angleInner, angleZInner, angleTiltInner;
	private double innerSize;
	private boolean[] innerSide;
	
	public Bar(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, Math3D.Angle angleInner, Math3D.Angle angleZInner, Math3D.Angle angleTiltInner, double size, double innerSize, boolean[] side, boolean[] innerSide, Color[] color, ShapeParent shapeParent) {
		super(x, y, z, angle, angleZ, angleTilt, size, side, color, shapeParent);
		this.angleInner = angleInner == null ? angle : angleInner;
		this.angleZInner = angleZInner == null ? angleZ : angleZInner;
		this.angleTiltInner = angleTiltInner == null ? angleTilt : angleTiltInner;
		this.innerSize = innerSize;
		this.innerSide = innerSide;
	}
	
	void initSurfaces() {
		Surface[] clipSurface = initSurfacesGeom(angle, angleZ, angleTilt, size, size, size, side, false, false);
		Surface[] backSurface = initSurfacesGeom(angle, angleZ, angleTilt, size, size, size, side, true, true);
		Surface[] barSurface = initSurfacesGeom(angleInner, angleZInner, angleTiltInner, size, size, innerSize, innerSide, false, false);
		for (int i = 0; i < barSurface.length; i++) {
			if (barSurface[i] != null) {
				barSurface[i].setColor(color[i]);
				barSurface[i].setLight(1);
			}
			if (backSurface[i] != null) {
				backSurface[i].setColor(Cube.PRIMARY_COLOR);
				backSurface[i].setLight(1);
			}
		}
		
		if (backSurface[0] == null)
			backSurface[0] = new Surface.NullSurface();
		backSurface[0].setClip(Surface.CLIP_SET);
		if (barSurface[5] == null)
			barSurface[5] = new Surface.NullSurface();
		barSurface[5].setClip(Surface.CLIP_RESET);
		
		surface = new Surface[18];
		for (int i = 0; i < 6; i++) {
			if (clipSurface[i] != null)
				clipSurface[i].setClip(Surface.CLIP_ADD);
			surface[i] = clipSurface[i];
			surface[i + 6] = backSurface[i];
			surface[i + 12] = barSurface[i];
		}
	}
	
}
