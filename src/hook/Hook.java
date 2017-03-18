package hook;

import camera.TrailingCamera;
import engine.Math3D;

public class Hook implements TrailingCamera.Follow {
	private double x, y, z;
	private Math3D.Angle angle, angleZ;
	
	public Hook(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		angle = new Math3D.Angle(0);
		angleZ = new Math3D.Angle(0);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getAngle() {
		return angle.get();
	}
	
	public double getAngleZ() {
		return angleZ.get();
	}
}
