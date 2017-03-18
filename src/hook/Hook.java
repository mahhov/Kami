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
	private double vx, vy, vz;
	private Math3D.Angle angle, angleZ, angleTilt;
	private double vAngleFlat;
	public double[] norm, rightUp;
	private long drawCounter;
	
	public Hook(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		angle = new Math3D.Angle(0);
		angleZ = new Math3D.Angle(0);
		angleTilt = new Math3D.Angle(0);
		computeAxis();
	}
	
	private void computeAxis() {
		norm = Math3D.norm(angle, angleZ, 1.);
		rightUp = Math3D.axisVectorsTilt(norm, 1., angleZ, angleTilt);
	}
	
	public void update(World world, Controller controller) {
		drawCounter++;
		doControl(controller);
		move(world);
		addToWorld(world);
	}
	
	void doControl(Controller controller) {
		double movementMult = .03, angleMult = .008;
		
		if (controller.isKeyDown(Controller.KEY_W)) {
			vx += norm[0] * movementMult;
			vy += norm[1] * movementMult;
			vz += norm[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_S)) {
			vx -= norm[0] * movementMult;
			vy -= norm[1] * movementMult;
			vz -= norm[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_A)) {
			vx -= rightUp[0] * movementMult;
			vy -= rightUp[1] * movementMult;
			vz -= rightUp[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_D)) {
			vx += rightUp[0] * movementMult;
			vy += rightUp[1] * movementMult;
			vz += rightUp[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_SPACE)) {
			vx += rightUp[3] * movementMult;
			vy += rightUp[4] * movementMult;
			vz += rightUp[5] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_Q)) {
			vAngleFlat += angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_E)) {
			vAngleFlat -= angleMult;
		}
	}
	
	private void move(World world) {
		int safeZone = 2;
		double friction = .9, gravity = -.01;
		vx *= friction;
		vy *= friction;
		vz = (vz + gravity) * friction;
		vAngleFlat *= friction;
		x += vx;
		y += vy;
		z += vz;
		angle.set(angle.get() + vAngleFlat);
		computeAxis();
		double[] xyz = Math3D.bound(x - safeZone, y - safeZone, z - safeZone, world.width - safeZone * 2, world.length - safeZone * 2, world.height - safeZone * 2);
		x = xyz[0] + safeZone;
		y = xyz[1] + safeZone;
		z = xyz[2] + safeZone;
	}
	
	void addToWorld(World world) {
		Cube shape = new Cube(x + .5, y + .5, z + .5, angle, angleZ, angleTilt, .5, this);
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
