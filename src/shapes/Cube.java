package shapes;

import engine.Math3D;
import ships.Ship;

import java.awt.*;

import static engine.Math3D.axisVectorsTilt;

public class Cube extends Shape {
	//			public static final Color PRIMARY_COLOR = new Color(120, 100, 20), SECONDARY_COLOR = new Color(240, 200, 40), TERNARY_COLOR = new Color(250, 0, 0);
	public static final Color PRIMARY_COLOR = Color.WHITE, SECONDARY_COLOR = Color.GREEN, TERNARY_COLOR = Color.RED;
	
	private double x, y, z;
	Math3D.Angle angle, angleZ, angleTilt;
	double size;
	private boolean surfacesDirty;
	Surface[] surface;
	boolean[] side;
	Color[] color;
	
	public Cube(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double size, ShapeParent shapeParent) {
		this(x, y, z, angle, angleZ, angleTilt, size, null, null, shapeParent);
	}
	
	public Cube(double x, double y, double z, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double size, boolean[] side, Color[] color, ShapeParent shapeParent) {
		super(shapeParent);
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
		this.angleZ = angleZ;
		this.angleTilt = angleTilt;
		this.size = size;
		surfacesDirty = true;
		this.side = side != null ? side : new boolean[] {true, true, true, true, true, true};
		this.color = color != null ? color : new Color[] {PRIMARY_COLOR, PRIMARY_COLOR, PRIMARY_COLOR, PRIMARY_COLOR, PRIMARY_COLOR, PRIMARY_COLOR};
	}
	
	Surface[] initSurfacesGeom(Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double normSize, double rightSize, double upSize, boolean[] side, boolean flipNormal, boolean flipSide) {
		Surface top = null, bottom = null, left = null, right = null, front = null, back = null;
		
		// axis  vectors
		double[] norm = Math3D.norm(angle, angleZ, normSize);
		double[] rightUp = axisVectorsTilt(norm, rightSize, upSize, angleZ, angleTilt);
		
		// corner coordinates
		double[] leftFrontBottom = new double[] {x - norm[0] - rightUp[0] - rightUp[3], y - norm[1] - rightUp[1] - rightUp[4], z - norm[2] - rightUp[2] - rightUp[5]};
		double[] rightFrontBottom = new double[] {x - norm[0] + rightUp[0] - rightUp[3], y - norm[1] + rightUp[1] - rightUp[4], z - norm[2] + rightUp[2] - rightUp[5]};
		double[] leftBackBottom = new double[] {x + norm[0] - rightUp[0] - rightUp[3], y + norm[1] - rightUp[1] - rightUp[4], z + norm[2] - rightUp[2] - rightUp[5]};
		double[] rightBackBottom = new double[] {x + norm[0] + rightUp[0] - rightUp[3], y + norm[1] + rightUp[1] - rightUp[4], z + norm[2] + rightUp[2] - rightUp[5]};
		double[] leftFrontTop = new double[] {x - norm[0] - rightUp[0] + rightUp[3], y - norm[1] - rightUp[1] + rightUp[4], z - norm[2] - rightUp[2] + rightUp[5]};
		double[] rightFrontTop = new double[] {x - norm[0] + rightUp[0] + rightUp[3], y - norm[1] + rightUp[1] + rightUp[4], z - norm[2] + rightUp[2] + rightUp[5]};
		double[] leftBackTop = new double[] {x + norm[0] - rightUp[0] + rightUp[3], y + norm[1] - rightUp[1] + rightUp[4], z + norm[2] - rightUp[2] + rightUp[5]};
		double[] rightBackTop = new double[] {x + norm[0] + rightUp[0] + rightUp[3], y + norm[1] + rightUp[1] + rightUp[4], z + norm[2] + rightUp[2] + rightUp[5]};
		
		// from back/left -> back/right -> front/right -> front/left
		if (side[Math3D.TOP] ^ flipSide)
			top = new Surface(leftBackTop, rightBackTop, rightFrontTop, leftFrontTop, true ^ flipNormal);
		
		// from back/left -> back/right -> front/right -> front/left
		if (side[Math3D.BOTTOM] ^ flipSide)
			bottom = new Surface(leftBackBottom, rightBackBottom, rightFrontBottom, leftFrontBottom, false ^ flipNormal);
		
		// from bottom/back -> top/back -> top/front -> bottom/front
		if (side[Math3D.LEFT] ^ flipSide)
			left = new Surface(leftBackBottom, leftBackTop, leftFrontTop, leftFrontBottom, true ^ flipNormal);
		
		// from bottom/back -> top/back -> top/front -> bottom/front
		if (side[Math3D.RIGHT] ^ flipSide)
			right = new Surface(rightBackBottom, rightBackTop, rightFrontTop, rightFrontBottom, false ^ flipNormal);
		
		// from left/bottom -> left/top -> right/top -> right/bottom
		if (side[Math3D.FRONT] ^ flipSide)
			front = new Surface(leftFrontBottom, leftFrontTop, rightFrontTop, rightFrontBottom, true ^ flipNormal);
		
		// from left/bottom -> left/top -> right/top -> right/bottom
		if (side[Math3D.BACK] ^ flipSide)
			back = new Surface(leftBackBottom, leftBackTop, rightBackTop, rightBackBottom, false ^ flipNormal);
		
		return new Surface[] {left, right, back, front, bottom, top};
	}
	
	void initSurfaces() {
		surface = initSurfacesGeom(angle, angleZ, angleTilt, size, size, size, side, false, false);
		for (int i = 0; i < surface.length; i++)
			if (surface[i] != null) {
				surface[i].setColor(color[i]);
				surface[i].setLight(1);
			}
	}
	
	Surface[] getSurfaces(int xSide, int ySide, int zSide) {
		if (surfacesDirty) {
			initSurfaces();
			surfacesDirty = false;
		}
		return surface;
	}
	
}
