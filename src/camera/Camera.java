package camera;

import control.Controller;
import engine.Math3D;

public class Camera {
	public static final double FOG = 0.987;
	public static final double MIN_LIGHT = .12;
	public static final double[] LIGHT_SOURCE = Math3D.normalize(new double[] {1, 2, 4});
	
	public double x, y, z;
	public Math3D.Angle angle, angleZ;
	public double[] normal;
	int maxCull, maxCullSqrd;
	
	boolean dirtyNorm;
	
	public Camera() {
		maxCull = (int) (Math.log(MIN_LIGHT) / Math.log(FOG)) + 1;
		System.out.println("maxCull set to " + maxCull);
		if (maxCull > 200)
			maxCull = 200;
		maxCullSqrd = maxCull * maxCull;
		
		normal = new double[3];
		
		x = 0;
		y = 0;
		z = 25;
		
		angle = new Math3D.Angle(Math.PI / 4);
		angleZ = new Math3D.Angle(0);
		computeNorm();
	}
	
	public void move(Controller c) {
	}
	
	void moveRelative(double dx, double dy, double dz, double scale) {
		x += dy * angle.cos() * scale + dx * angle.sin() * scale;
		y += dy * angle.sin() * scale - dx * angle.cos() * scale;
		z += dz * scale;
	}
	
	void moveTo(double toX, double toY, double toZ, double weight) {
		x += (toX - x) * weight;
		y += (toY - y) * weight;
		z += (toZ - z) * weight;
	}
	
	void rotate(double dangle, double dangleZ, double scale) {
		angle.set(angle.get() + dangle * scale);
		angleZ.set(angleZ.get() + dangleZ * scale);
		dirtyNorm = true;
	}
	
	void lookAt(double lookX, double lookY, double lookZ) {
		double dy = lookY - y;
		double dx = lookX - x;
		double dz = lookZ - z;
		angle.set(Math.atan2(dy, dx));
		angleZ.set(Math.atan2(dz, Math3D.magnitude(dx, dy)));
		dirtyNorm = true;
	}
	
	public void update(int width, int length, int height) {
		bound(width, length, height);
		if (dirtyNorm) {
			dirtyNorm = false;
			computeNorm();
		}
	}
	
	private void bound(int width, int length, int height) {
		double[] xyz = Math3D.bound(x, y, z, width, length, height);
		x = xyz[0];
		y = xyz[1];
		z = xyz[2];
		angleZ.bound();
	}
	
	private void computeNorm() {
		normal = Math3D.norm(angle, angleZ);
	}
	
	public boolean facingTowards(double[] normal, double[] position) {
		return (Math3D.dotProductUnormalized(x - position[0], y - position[1], z - position[2], normal[0], normal[1], normal[2])) > 0;
	}
	
	public boolean inView(double[][] coord) {
		double dx, dy, dz;
		for (double[] c : coord) {
			dx = c[0] - x;
			dy = c[1] - y;
			dz = c[2] - z;
			if (Math3D.magnitudeSqrd(dx, dy, dz) < maxCullSqrd && Math3D.dotProduct(dx, dy, dz, normal[0], normal[1], normal[2]) > Math3D.sqrt2Div3)
				return true;
		}
		return false;
	}
	
	public int[] cullBoundaries() {
		// right & up axis vectors
		double[] axis = Math3D.halfAxisVectors(normal);
		
		// top & bottom vectors
		double[] topVector = new double[] {normal[0] + axis[3], normal[1] + axis[4], normal[2] + axis[5]};
		double[] bottomVector = new double[] {normal[0] - axis[3], normal[1] - axis[4], normal[2] - axis[5]};
		
		// corner points
		double[][] points = new double[4][3]; // top right, top left, bottom right, bottom left;
		points[0] = new double[] {topVector[0] + axis[0], topVector[1] + axis[1], topVector[2] + axis[2]};
		points[1] = new double[] {topVector[0] - axis[0], topVector[1] - axis[1], topVector[2] - axis[2]};
		points[2] = new double[] {bottomVector[0] + axis[0], bottomVector[1] + axis[1], bottomVector[2] + axis[2]};
		points[3] = new double[] {bottomVector[0] - axis[0], bottomVector[1] - axis[1], bottomVector[2] - axis[2]};
		
		// find min & max
		double left = 0, right = 0, front = 0, back = 0, bottom = 0, top = 0;
		for (double[] p : points) {
			if (p[0] < left)
				left = p[0];
			else if (p[0] > right)
				right = p[0];
			if (p[1] < front)
				front = p[1];
			else if (p[1] > back)
				back = p[1];
			if (p[2] < bottom)
				bottom = p[2];
			else if (p[2] > top)
				top = p[2];
		}
		
		return new int[] {(int) (left * maxCull + x), (int) (right * maxCull + x), (int) (front * maxCull + y), (int) (back * maxCull + y), (int) (bottom * maxCull + z), (int) (top * maxCull + z)};
	}
	
	public double[] orig() {
		return new double[] {x, y, z};
	}
}
