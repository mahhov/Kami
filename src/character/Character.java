package character;

import camera.TrailingCamera;
import control.Controller;
import engine.Math3D;
import shapes.Cube;
import shapes.ShapeParent;
import terrain.Terrain;
import world.World;
import world.WorldElement;

public class Character implements WorldElement, TrailingCamera.Follow, ShapeParent {
	private static final double FRICTION = 0.9, AIR_FRICTION = 0.99, CLIMB_FRICTION = 0.99, GRAVITY = .05;
	private static final double JUMP_ACC = .7, RUN_ACC = .1, AIR_RUN_ACC = .015, JET_ACC = .045, CLIMB_ACC = .05, HOOK_ACC = .05, JUMP_MULT = 1.5;
	
	private static final int JUMP_MAX = 1;
	private int jumpRemain;
	
	private static final int STATE_GROUND = 0, STATE_CLIMB = 1, STATE_AIR = 2;
	private int state;
	private boolean jetting;
	
	private static final int HOOK_NONE = 0, HOOK_THROWING = 1, HOOK_ATTACHED = 2;
	private int hookState;
	private double hookx, hooky, hookz;
	private double hookvx, hookvy, hookvz;
	
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
	
	public void update(World world, Terrain terrain, Controller controller) {
		drawCounter++;
		movement(terrain, controller);
		addToWorld(world);
	}
	
	private void movement(Terrain terrain, Controller controller) {
		// view angle
		angle = new Math3D.Angle(controller.viewAngle.get());
		computeAxis();
		
		// hook
		if (controller.isKeyDown(Controller.KEY_SHIFT))
			if (hookState == HOOK_ATTACHED)
				moveTowardsHook();
			else if (hookState == HOOK_THROWING) {
				updateThrowHook();
				if (checkThrowHookAttach(terrain))
					hookState = HOOK_ATTACHED;
			} else {
				hookState = HOOK_THROWING;
				initiateThrowHook();
			}
		else
			hookState = HOOK_NONE;
		
		// running
		runningDirection(controller);
		runningMove(controller);
		
		
		// jumping
		if (controller.isKeyPressed(Controller.KEY_SPACE))
			jump();
		
		// climbing  or jetting
		if (controller.isKeyDown(Controller.KEY_SPACE))
			upwardMove();
		else
			jetting = false;
		
		// gravity
		vz -= GRAVITY;
		
		// friction
		doFriction();
		
		// move x,y,z & avoid collisions & set state
		applyVelocity(terrain);
	}
	
	private void moveTowardsHook() {
		double[] dir = Math3D.normalize(new double[] {hookx - x, hooky - y, hookz - z});
		vx += dir[0] * HOOK_ACC;
		vy += dir[1] * HOOK_ACC;
		vz += dir[2] * HOOK_ACC;
	}
	
	private void updateThrowHook() {
		hookvx *= AIR_FRICTION;
		hookvy *= AIR_FRICTION;
		hookvz = hookvz * AIR_FRICTION - GRAVITY;
		hookx += hookvx;
		hooky += hookvy;
		hookz += hookvz;
	}
	
	private boolean checkThrowHookAttach(Terrain terrain) {
		// do this
		return false;
	}
	
	private void initiateThrowHook() {
		hookx = x;
		hooky = y;
		hookz = z;
		hookvx = 0; // mouse direction * hook throw speed constant
		hookvy = 0;
		hookvz = 0;
	}
	
	private void runningDirection(Controller controller) {
		angle = new Math3D.Angle(controller.viewAngle.get());
	}
	
	private void runningMove(Controller controller) {
		double acc = state == STATE_GROUND ? RUN_ACC : AIR_RUN_ACC;
		if (controller.isKeyDown(Controller.KEY_W)) {
			vx += norm[0] * acc;
			vy += norm[1] * acc;
			vz += norm[2] * acc;
		}
		
		if (controller.isKeyDown(Controller.KEY_S)) {
			vx -= norm[0] * acc;
			vy -= norm[1] * acc;
			vz -= norm[2] * acc;
		}
		
		if (controller.isKeyDown(Controller.KEY_A)) {
			vx -= rightUp[0] * acc;
			vy -= rightUp[1] * acc;
			vz -= rightUp[2] * acc;
		}
		
		if (controller.isKeyDown(Controller.KEY_D)) {
			vx += rightUp[0] * acc;
			vy += rightUp[1] * acc;
			vz += rightUp[2] * acc;
		}
	}
	
	private void jump() {
		if (jumpRemain == 0)
			return;
		jumpRemain--;
		vz += JUMP_ACC;
		vx *= JUMP_MULT;
		vy *= JUMP_MULT;
	}
	
	private void upwardMove() {
		if (state == STATE_CLIMB) {
			vz += CLIMB_ACC;
			jetting = false;
		} else {
			vz += JET_ACC;
			jetting = true;
		}
	}
	
	private void doFriction() {
		double friction;
		if (state == STATE_AIR)
			friction = AIR_FRICTION;
		else if (state == STATE_CLIMB)
			friction = CLIMB_FRICTION;
		else
			friction = FRICTION;
		vx *= friction;
		vy *= friction;
		vz = vz * friction;
	}
	
	private void applyVelocity(Terrain terrain) {
		double newx = x + vx;
		double newy = y + vy;
		double newz = z + vz;
		
		state = STATE_AIR;
		
		if (terrain.checkCollide(newx, newy, z)) {
			state = STATE_CLIMB;
		} else {
			x = newx;
			y = newy;
		}
		
		if (terrain.checkCollide(x, y, newz)) {
			if (vz < 0) {
				state = STATE_GROUND;
				jumpRemain = JUMP_MAX;
			}
		} else
			z = newz;
	}
	
	private void addToWorld(World world) {
		if (jetting)
			; // add jet particles
		if (hookState != HOOK_NONE)
			; // add hook and rope
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
