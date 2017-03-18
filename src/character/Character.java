package character;

import camera.TrailingCamera;
import control.Controller;
import engine.Math3D;
import shapes.Cube;
import shapes.ShapeParent;
import world.World;
import world.WorldElement;

public class Character implements WorldElement, TrailingCamera.Follow, ShapeParent {
	private static final double JUMP_ACC = .7, RUN_ACC = .1, AIR_RUN_ACC = .015, JET_ACC = .045, CLIMB_ACC = .05, HOOK_ACC = .05, HOOK_SPRING_ACC = .5, JUMP_MULT = 1.5;
	
	private static final int JUMP_MAX = 1;
	private int jumpRemain;
	
	private static final int STATE_GROUND = 0, STATE_CLIMB = 1, STATE_JET = 2, STATE_AIR = 3;
	private int state;
	
	private static final int HOOK_NONE = 0, HOOK_THROWING = 1, HOOK_ATTACHED = 2;
	private int hookState;
	private double hookx, hooky;
	
	private double x, y, z;
	private double vx, vy, vz;
	private Math3D.Angle angle, angleZ, angleTilt;
	private double vAngleFlat;
	private double[] norm, rightUp;
	
	private long drawCounter;
	
	public Character(double x, double y, double z) {
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
		
		angle = new Math3D.Angle(controller.viewAngle.get());
		
		if (controller.isKeyDown(Controller.KEY_SPACE)) {
			vx += rightUp[3] * movementMult;
			vy += rightUp[4] * movementMult;
			vz += rightUp[5] * movementMult;
		}
		
		if (controller.isKeyDown(Controller.KEY_SHIFT)) {
			// grappling character
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
	
			// if hook button pressed
				// initiate hook x,y,z,vx,vy,vz
			// if hook button release
				// destroy hook
			// if hooked
				// vx, vy, vz += direction to Hook * hookAcc
			
			// grounded ? runAcc : airAcc
			// vx/vy += acc :: based on wasd
	
			// if up key press && jumpRemain-- > 0
			// then vz += jumpAcc, vx/vy *= jumpMult
	
			// jetting = false
			// if up key down
			//	if climbing then vz += climbAcc
			//  else jetting = true, vz += jetAcc
	
			// vz-=gravity
			// if grounded, , vx/vy/vz *= friction
			// else if hook, *= hookFriction
			// else if air, *= airFriction
	
			// climbnig = false, grounded = false
	
			// x,y,z += vx,vy,vz
			// avaoid collisions -> set grounded (horizontal collision) and climbing (vertical collision)
	
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
