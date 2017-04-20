package character;

import ambient.Music;
import camera.TrailingCamera;
import control.Controller;
import engine.Math3D;
import engine.Painter;
import particle.HookParticle;
import particle.SmokeParticle;
import shapes.Cube;
import shapes.Cuboid;
import shapes.ShapeParent;
import terrain.Terrain;
import world.World;
import world.WorldElement;
import world.interfaceelement.Bar;

public class Character implements WorldElement, TrailingCamera.Follow, ShapeParent {
	private static final double FRICTION = 0.9, AIR_FRICTION = 0.97, CLIMB_FRICTION = .99, GRAVITY = .05, COLLISION_DAMPER = .1;
	private static final double JUMP_ACC = .2, RUN_ACC = .1, AIR_RUN_ACC = .04, JET_ACC = .04, CLIMB_ACC = .055, JUMP_MULT = 1.5;
	private static final double HOOK_SPEED = .2, HOOK_FRICTION = 1, HOOK_GRAVITY = 0, HOOK_ACC = .05;
	
	private static final int JUMP_MAX = 1;
	private int jumpRemain;
	
	private static final String[] STATE_STRING = new String[] {"GROUND", "CLIMB", "AIR"};
	private static final int STATE_GROUND = 0, STATE_CLIMB = 1, STATE_AIR = 2;
	private int state;
	private boolean jetting;
	
	private static final int HOOK_NONE = 0, HOOK_THROWING = 1, HOOK_ATTACHED = 2, HOOK_ACTIVATED = 3;
	private int hookState;
	private double hookx, hooky, hookz;
	private double hookvx, hookvy, hookvz;
	
	private double x, y, z;
	private double vx, vy, vz, vsq;
	private Math3D.Angle angle, angleZ, angleTilt;
	private double[] norm, rightUp;
	
	private static final int HEALTH_MAX = 2000;
	private int health;
	private Bar healthBar;
	
	private long drawCounter;
	
	public Character(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		angle = new Math3D.Angle(0);
		angleZ = new Math3D.Angle(0);
		angleTilt = new Math3D.Angle(0);
		computeAxis();
		health = HEALTH_MAX;
	}
	
	private void computeAxis() {
		norm = Math3D.norm(angle, angleZ, 1.);
		rightUp = Math3D.axisVectorsTilt(norm, 1., angleZ, angleTilt);
	}
	
	public void init(World world) {
		healthBar = new Bar(.02, .96, .48, .02);
		world.addInterfaceElement(healthBar);
	}
	
	public void update(World world, Terrain terrain, Controller controller) {
		drawCounter++;
		interaction(terrain);
		movement(terrain, controller);
		addToWorld(world);
	}
	
	private void interaction(Terrain terrain) {
		if (z < 1.1)
			health--;
		healthBar.setFill(1.0 * health / HEALTH_MAX);
	}
	
	private void movement(Terrain terrain, Controller controller) {
		// view angle
		angle = new Math3D.Angle(controller.viewAngle.get());
		computeAxis();
		
		// hook
		boolean hookPress = controller.isKeyPressed(Controller.KEY_SHIFT) || controller.isMousePressed();
		if (hookPress || controller.isKeyDown(Controller.KEY_SHIFT) || controller.isMouseDown()) {
			if (hookState == HOOK_ATTACHED) {
				hookState = HOOK_ACTIVATED;
			} else if (hookState == HOOK_ACTIVATED)
				moveTowardsHook();
			else if (hookState == HOOK_THROWING) {
				boolean[] collide = updateThrowHook(terrain);
				if (collide[0]) { // collide with terrain
					Music.HOOK.play();
					hookState = HOOK_ATTACHED;
				} else if (collide[1])
					hookState = HOOK_NONE; // collide with boundary
			} else if (hookPress) {
				hookState = HOOK_THROWING;
				initiateThrowHook(terrain, controller);
			}
		} else
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
		
		// woosh sound
		vsq = Math3D.magnitudeSqrd(vx, vy, vz);
		boolean vfast = vsq > 2;
		if (vfast)
			Music.WOOSH.play();
		
		Painter.debugString[2] = STATE_STRING[state];
	}
	
	private void moveTowardsHook() {
		double[] dir = Math3D.normalize(new double[] {hookx - x, hooky - y, hookz - z});
		vx += dir[0] * HOOK_ACC;
		vy += dir[1] * HOOK_ACC;
		vz += dir[2] * HOOK_ACC;
	}
	
	private boolean[] updateThrowHook(Terrain terrain) {
		hookvx *= HOOK_FRICTION;
		hookvy *= HOOK_FRICTION;
		hookvz = (hookvz - HOOK_GRAVITY) * HOOK_FRICTION;
		
		double newxyz[] = terrain.findIntersection(new double[] {hookx, hooky, hookz}, new double[] {hookvx, hookvy, hookvz}, false, true, false, 0);
		hookx = newxyz[0];
		hooky = newxyz[1];
		hookz = newxyz[2];
		
		return terrain.getIntersectionCollide();
	}
	
	private void initiateThrowHook(Terrain terrain, Controller controller) {
		Music.ZIP.play();
		hookx = x;
		hooky = y;
		hookz = z;
		
		double[] xyz = terrain.findIntersection(controller.viewOrig, controller.viewDir, false, false, true, 0);
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
		double[] newxyz = terrain.findIntersection(new double[] {x, y, z}, new double[] {vx, vy, vz}, true, true, true, 0);
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
		if (hookState == HOOK_ATTACHED)
			for (int i = 0; i < 10; i++)
				world.addParticle(new HookParticle(hookx, hooky, hookz));
		
		if (jetting)
			world.addParticle(new SmokeParticle(x, y, z));
		
		if (hookState != HOOK_NONE) { // add hook and rope
			Cube hook = new Cube(hookx, hooky, hookz, angle, angleZ, angleTilt, .2, this);
			world.addShape((int) hookx, (int) hooky, (int) hookz, hook);
			double xx, yy, zz;
			Cube hookRope;
			int n = 20;
			for (double i = 0; i < n; i++) {
				xx = Math3D.avg(x, hookx, i / n);
				yy = Math3D.avg(y, hooky, i / n);
				zz = Math3D.avg(z, hookz, i / n);
				hookRope = new Cube(xx, yy, zz, angle, angleZ, angleTilt, .05, this);
				world.addShape((int) xx, (int) yy, (int) zz, hookRope);
			}
		}
		
		//		Cube shape = new Cube(x, y, z + .5, angle, angleZ, angleTilt, .5, this);
		//		world.addShape((int) x, (int) y, (int) (z + .5), shape);
		
		double[] leg = new double[] {.5, .5, .5};
		double legGap = 1;
		double[] body = new double[] {1.5, 1.5, 1.5};
		double[] head = new double[] {1, 1, 1};
		double[] shoulder = new double[] {.5, .5, .5};
		double shoulderDownOffset = 1;
		double shoulderGap = body[0] + shoulder[0] + .5;
		
		double stackz = z + leg[2];
		addBodyPieceToWorld(x + rightUp[0] * legGap, y + rightUp[1] * legGap, stackz + rightUp[2] * legGap, leg[0], leg[1], leg[2], world);
		addBodyPieceToWorld(x + rightUp[0] * -legGap, y + rightUp[1] * -legGap, stackz + rightUp[2] * -legGap, leg[0], leg[1], leg[2], world);
		stackz += leg[2] + body[2] + .5;
		addBodyPieceToWorld(x, y, stackz, body[0], body[1], body[2], world);
		stackz += body[2] + head[2] + .5;
		addBodyPieceToWorld(x, y, stackz, head[0], head[1], head[2], world);
		stackz += -head[2] - shoulderDownOffset;
		addBodyPieceToWorld(x + rightUp[0] * shoulderGap, y + rightUp[1] * shoulderGap, stackz + rightUp[2] * shoulderGap, shoulder[0], shoulder[1], shoulder[2], world);
		addBodyPieceToWorld(x + rightUp[0] * -shoulderGap, y + rightUp[1] * -shoulderGap, stackz + rightUp[2] * -shoulderGap, shoulder[0], shoulder[1], shoulder[2], world);
	}
	
	private void addBodyPieceToWorld(double x, double y, double z, double w, double l, double h, World world) {
		world.addShape((int) x, (int) y, (int) z, new Cuboid(x, y, z, angle, angleZ, angleTilt, w, l, h, null, null, this));
	}
	
	public double getVsq() {
		return vsq;
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