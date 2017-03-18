package hook;

import camera.TrailingCamera;
import control.Controller;
import engine.Math3D;
import shapes.Cube;
import shapes.ShapeParent;
import world.World;
import world.WorldElement;

public class Hook implements WorldElement, TrailingCamera.Follow, ShapeParent {
	private double x, y, z;
	private Math3D.Angle angle, angleZ, angleTilt;
	private long drawCounter;
	
	public Hook(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		angle = new Math3D.Angle(0);
		angleZ = new Math3D.Angle(0);
		angleTilt = new Math3D.Angle(0);
	}
	
	public void update(World world, Controller controller) {
		drawCounter++;
		this.x += .1;
		Cube shape = new Cube(x, y, z, angle, angleZ, angleTilt, 1, this);
		world.addShape((int) x, (int) y, (int) z, shape);
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
	
	public long getDrawCounter() {
		return drawCounter;
	}
}
