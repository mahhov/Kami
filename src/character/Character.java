package character;

import camera.TrailingCamera;
import control.Controller;
import engine.Math3D;
import engine.Painter;
import particle.SmokeParticle;
import shapes.Cube;
import shapes.ShapeParent;
import terrain.Terrain;
import world.World;
import world.WorldElement;

public class Character implements WorldElement, TrailingCamera.Follow, ShapeParent {
	private static final double FRICTION = 0.9, AIR_FRICTION = 0.98, CLIMB_FRICTION = .99, GRAVITY = .05, COLLISION_DAMPER = .1;
	private static final double JUMP_ACC = .2, RUN_ACC = .1, AIR_RUN_ACC = .02, JET_ACC = .045, CLIMB_ACC = .055, JUMP_MULT = 1.5;
	private static final double HOOK_SPEED = 20, HOOK_FRICTION = 1, HOOK_GRAVITY = 0, HOOK_ACC = .05;
	
	private static final int JUMP_MAX = 1;
	private int jumpRemain;
	
	private static final int STATE_GROUND = 0, STATE_CLIMB = 1, STATE_AIR = 2;
	private int state;
	private boolean jetting;
	
	private static final int HOOK_NONE = 0, HOOK_THROWING = 1, HOOK_ATTACHED = 2, HOOK_ACTIVATED = 3;
	private int hookState;
	private double hookx, hooky, hookz;
	private double hookvx, hookvy, hookvz;
	
	private double x, y, z;
	private double vx, vy, vz;
	private Math3D.Angle angle, angleZ, angleTilt;
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
		if (controller.isKeyDown(Controller.KEY_SHIFT) || controller.isMouseDown())
			if (hookState == HOOK_ATTACHED) {
				if (controller.isKeyDown(Controller.KEY_SPACE))
					hookState = HOOK_ACTIVATED;
			} else if (hookState == HOOK_ACTIVATED)
				moveTowardsHook();
			else if (hookState == HOOK_THROWING) {
				if (updateThrowHook(terrain))
					hookState = HOOK_ATTACHED;
			} else {
				hookState = HOOK_THROWING;
				initiateThrowHook(terrain, controller);
			}
		else
			hookState = HOOK_NONE;
		
		// running
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
		
		String[] stateString = new String[] {"GROUND", "CLIMB", "AIR"};
		Painter.debugString[2] = stateString[state];
	}
	
	private void moveTowardsHook() {
		double[] dir = Math3D.normalize(new double[] {hookx - x, hooky - y, hookz - z});
		vx += dir[0] * HOOK_ACC;
		vy += dir[1] * HOOK_ACC;
		vz += dir[2] * HOOK_ACC;
	}
	
	private boolean updateThrowHook(Terrain terrain) {
		hookvx *= HOOK_FRICTION;
		hookvy *= HOOK_FRICTION;
		hookvz = (hookvz - HOOK_GRAVITY) * HOOK_FRICTION;
		
		double newxyz[] = terrain.findIntersection(new double[] {hookx, hooky, hookz}, new double[] {hookvx, hookvy, hookvz}, false, true);
		hookx = newxyz[0];
		hooky = newxyz[1];
		hookz = newxyz[2];
		
		boolean collide[] = terrain.getIntersectionCollide();
		return collide[0];
	}
	
	private void initiateThrowHook(Terrain terrain, Controller controller) {
		hookx = x;
		hooky = y;
		hookz = z;
		
		double[] xyz = terrain.findIntersection(controller.viewOrig, controller.viewDir, false, false);
		xyz[0] -= x;
		xyz[1] -= y;
		xyz[2] -= z;
		Math3D.scale(xyz, HOOK_SPEED);
		hookvx = xyz[0] + vx;
		hookvy = xyz[1] + vy;
		hookvz = xyz[2] + vz;
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
		if (state == STATE_CLIMB) {
			vx *= COLLISION_DAMPER;
			vy *= COLLISION_DAMPER;
		}
		
		double[] newxyz = terrain.findIntersection(new double[] {x, y, z}, new double[] {vx, vy, vz}, true, true);
		x = newxyz[0];
		y = newxyz[1];
		z = newxyz[2];
		
		state = STATE_AIR;
		boolean collide[] = terrain.getIntersectionCollide();
		if (collide[2]) {
			vz *= COLLISION_DAMPER;
			state = STATE_GROUND;
			jumpRemain = JUMP_MAX;
		} else if (collide[0]) {
			vx *= COLLISION_DAMPER;
			state = STATE_CLIMB;
		} else if (collide[1]) {
			vy *= COLLISION_DAMPER;
			state = STATE_CLIMB;
		}
	}
	
	private void addToWorld(World world) {
		if (jetting)
			world.addParticle(new SmokeParticle(x, y, z));
		
		if (hookState != HOOK_NONE) { // add hook and rope
			Cube shape = new Cube(hookx, hooky, hookz, angle, angleZ, angleTilt, .2, this);
			world.addShape((int) hookx, (int) hooky, (int) hookz, shape);
		}
		
		Cube shape = new Cube(x, y, z + .5, angle, angleZ, angleTilt, .5, this);
		world.addShape((int) x, (int) y, (int) (z + .5), shape);
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

// todo : hook - why is aim below click
// todo : graphics
// todo : shooting
// todo : survival