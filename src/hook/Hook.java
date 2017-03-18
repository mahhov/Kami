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
	private double vAngleFlat, vAngleUp, vAngleTilt;
	public double[] norm, rightUp;
	private long drawCounter;
	
	public Hook(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		angle = new Math3D.Angle(0);
		angleZ = new Math3D.Angle(0);
		angleTilt = new Math3D.Angle(0);
		this.computeAxis();
	}
	
	private void computeAxis() {
		this.norm = Math3D.norm(this.angle, this.angleZ, 1.);
		this.rightUp = Math3D.axisVectorsTilt(this.norm, 1., this.angleZ, this.angleTilt);
	}
	
	public void update(World world, Controller controller) {
		drawCounter++;
		doControl(controller);
		move(world);
		addToWorld(world);
	}
	
	void doControl(Controller controller) {
		double movementMult = .01, angleMult = .01;
		
		if (controller.isKeyDown(Controller.KEY_W)) {
			this.vx += this.norm[0] * movementMult;
			this.vy += this.norm[1] * movementMult;
			this.vz += this.norm[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_S)) {
			this.vx -= this.norm[0] * movementMult;
			this.vy -= this.norm[1] * movementMult;
			this.vz -= this.norm[2] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_A)) {
			this.vAngleTilt += angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_D)) {
			this.vAngleTilt -= angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_SPACE)) {
			this.vAngleUp += angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_SHIFT)) {
			this.vAngleUp -= angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_Q)) {
			this.vAngleFlat += angleMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_R)) {
			this.vAngleFlat -= angleMult;
		}
	}
	
	private void move(World world) {
		int safeZone = 2;
		double friction = .95, gravity = -.005;
		vx *= friction;
		vy *= friction;
		vz = (vz + gravity) * friction;
		vAngleFlat *= friction;
		vAngleUp *= friction;
		vAngleTilt *= friction;
		x += vx;
		y += vy;
		z += vz;
		Math3D.Angle.rotate(angle, angleZ, angleTilt, rightUp, vAngleFlat, vAngleUp);
		angleTilt.set(angleTilt.get() + vAngleTilt);
		computeAxis();
		double[] xyz = Math3D.bound(x - safeZone, y - safeZone, z - safeZone, world.width - safeZone * 2, world.length - safeZone * 2, world.height - safeZone * 2);
		x = xyz[0] + safeZone;
		y = xyz[1] + safeZone;
		z = xyz[2] + safeZone;
	}
	
	void addToWorld(World world) {
		Cube shape = new Cube(x, y, z, angle, angleZ, angleTilt, .5, this);
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
