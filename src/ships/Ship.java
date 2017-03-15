package ships;

import control.Controller;
import engine.Math3D;
import engine.Painter;
import module.*;
import shapes.Shape;
import shapes.ShapeParent;
import shapes.ShipTrigger;
import world.World;

import static ships.Blueprint.*;

public abstract class Ship implements ShapeParent {
	private final double FRICTION = .96, FORCE = 10, ANGLE_FORCE = .75, GRAVITY = -.003;
	
	public boolean visible;
	long drawCounter;
	public double x, y, z; // mass center
	public Math3D.Angle angle, angleZ, angleTilt;
	public double[] norm, rightUp;
	private double vx, vy, vz, vAngleFlat, vAngleUp, vAngleTilt;
	
	double mass, massX, massY, massZ; // in relative axis system, offset from corner to mass center
	private double inertiaFlat, inertiaUp, inertiaTilt;
	Blueprint blueprint;
	Module part[][][];
	public int width, length, height;
	private int safeZone;
	
	public Ship(double x, double y, double z, double angle, double angleZ, double angleTilt, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = new Math3D.Angle(angle);
		this.angleZ = new Math3D.Angle(angleZ);
		this.angleTilt = new Math3D.Angle(angleTilt);
		computeAxis();
		visible = false;
		generateBlueprint();
		generateParts();
		computeMass();
		computeInertia();
		computeSafeZone();
		setParts();
	}
	
	void generateBlueprint() {
		blueprint = Blueprint.defaultBlueprint();
	}
	
	void generateParts() {
		width = blueprint.width;
		length = blueprint.length;
		height = blueprint.height;
		part = new Module[width][length][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				for (int z = 0; z < height; z++)
					generatePart(x, y, z);
	}
	
	void generatePart(int x, int y, int z) {
		Module module;
		switch (blueprint.blueprint[x][y][z][0]) {
			case MODULE_EMPTY_MODULE:
				module = new EmptyModule();
				break;
			case MODULE_HULL:
				module = new Hull();
				break;
			case MODULE_ROTOR:
				module = new Rotor();
				break;
			case MODULE_HELIUM:
				module = new Helium(this);
				break;
			case MODULE_FORW_BLADE:
				module = new ForwBlade(this);
				break;
			case MODULE_GUN:
				module = new Blaster(this);
				break;
			default:
				module = new Hull();
		}
		part[x][y][z] = module;
	}
	
	void setParts() {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				for (int z = 0; z < height; z++)
					part[x][y][z].set(blueprint.blueprint[x][y][z][1], new double[] {x - massX, y - massY, z - massZ});
	}
	
	private void computeMass() {
		double tmass;
		mass = 0;
		massX = 0;
		massY = 0;
		massZ = 0;
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++) {
					tmass = part[x][y][z].mass;
					mass += tmass;
					massX += x * tmass;
					massY += y * tmass;
					massZ += z * tmass;
				}
		massX /= mass;
		massY /= mass;
		massZ /= mass;
	}
	
	private void computeInertia() {
		inertiaFlat = 0; // todo : fix these initial value
		inertiaUp = 0;
		inertiaTilt = 0;
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++) {
					inertiaFlat += Math3D.magnitudeSqrd(x - massX, y - massY) * part[x][y][z].mass;
					inertiaUp += Math3D.magnitudeSqrd(y - massY, z - massZ) * part[x][y][z].mass;
					inertiaTilt += Math3D.magnitudeSqrd(x - massX, z - massZ) * part[x][y][z].mass;
				}
	}
	
	private void computeAxis() {
		norm = Math3D.norm(angle, angleZ, 1);
		rightUp = Math3D.axisVectorsTilt(norm, 1, angleZ, angleTilt);
		Painter.debugString[2] = "(norm) " + Math3D.doubles2Str(norm, 0) + "(right) " + Math3D.doubles2Str(rightUp, 0) + "(up) " + Math3D.doubles2Str(rightUp, 3);
	}
	
	private void computeSafeZone() {
		safeZone = Math3D.max(part.length, part[0].length, part[0][0].length) / 2 + 1;
	}
	
	public double getVForward() {
		return Math3D.dotProductUnormalized(norm, new double[] {vx, vy, vz});
	}
	
	private void addTriggerToWorld(World world) {
		ShipTrigger trigger = new ShipTrigger(this);
		world.addShape((int) x, (int) y, (int) z, trigger);
	}
	
	void addToWorld(World world) {
		Shape shape;
		double xc, yc, zc;
		double dx, dy, dz;
		for (int xi = 0; xi < part.length; xi++)
			for (int yi = 0; yi < part[xi].length; yi++)
				for (int zi = 0; zi < part[xi][yi].length; zi++) {
					dx = xi - massX;
					dy = yi - massY;
					dz = zi - massZ;
					
					xc = x + dx * rightUp[0] + dy * norm[0] + dz * rightUp[3];
					yc = y + dx * rightUp[1] + dy * norm[1] + dz * rightUp[4];
					zc = z + dx * rightUp[2] + dy * norm[2] + dz * rightUp[5];
					
					int[] block = getBlock(xi, yi, zi);
					
					shape = part[xi][yi][zi].getShape(xc, yc, zc, angle, angleZ, angleTilt, rightUp, block, this);
					if (shape != null)
						world.addShape((int) xc, (int) yc, (int) zc, shape);
				}
	}
	
	public void update(World world, Controller controller) {
		drawCounter++; // todo : only if moved
		doControl(world, controller);
		move(world);
		addTriggerToWorld(world);
		if (visible) {
			addToWorld(world); // todo : only if moved
			visible = false;
		}
	}
	
	private void doControl(World world, Controller controller) { // todo: rename
		// controls
		boolean[] control = new boolean[6];
		if (controller.isKeyDown(Controller.KEY_W))
			control[Math3D.FRONT] = true;
		if (controller.isKeyDown(Controller.KEY_S))
			control[Math3D.BACK] = true;
		if (controller.isKeyDown(Controller.KEY_A))
			control[Math3D.LEFT] = true;
		if (controller.isKeyDown(Controller.KEY_D))
			control[Math3D.RIGHT] = true;
		if (controller.isKeyDown(Controller.KEY_SPACE))
			control[Math3D.TOP] = true;
		if (controller.isKeyDown(Controller.KEY_SHIFT))
			control[Math3D.BOTTOM] = true;
		
		// react
		Math3D.Force force = new Math3D.Force();
		for (Module[][] ppp : part)
			for (Module[] pp : ppp)
				for (Module p : pp)
					p.react(world, force, control); // todo: support aim direction + aim click
		
		// apply force
		applyForce(force);
	}
	
	void doControlAuto(Controller controller) {
		if (controller.isKeyDown(Controller.KEY_W)) {
			vx += norm[0] * FORCE;
			vy += norm[1] * FORCE;
			vz += norm[2] * FORCE;
		}
		if (controller.isKeyDown(Controller.KEY_S)) {
			vx -= norm[0] * FORCE;
			vy -= norm[1] * FORCE;
			vz -= norm[2] * FORCE;
		}
		if (controller.isKeyDown(Controller.KEY_A))
			vAngleTilt += ANGLE_FORCE;
		if (controller.isKeyDown(Controller.KEY_D))
			vAngleTilt -= ANGLE_FORCE;
		if (controller.isKeyDown(Controller.KEY_SPACE))
			vAngleUp += ANGLE_FORCE;
		if (controller.isKeyDown(Controller.KEY_SHIFT))
			vAngleUp -= ANGLE_FORCE;
		if (controller.isKeyDown(Controller.KEY_Q))
			vAngleFlat += ANGLE_FORCE;
		if (controller.isKeyDown(Controller.KEY_E))
			vAngleFlat -= ANGLE_FORCE;
	}
	
	private void move(World world) {
		vx *= FRICTION;
		vy *= FRICTION;
		vz = (vz + GRAVITY) * FRICTION;
		vAngleFlat *= FRICTION;
		vAngleUp *= FRICTION;
		vAngleTilt *= FRICTION;
		
		x += vx;
		y += vy;
		z += vz;
		
		// vAngleFlat & vAngleUp
		Math3D.Angle.rotate(angle, angleZ, angleTilt, rightUp, vAngleFlat, vAngleUp);
		// vAngleTilt
		angleTilt.set(angleTilt.get() + vAngleTilt);
		
		computeAxis();
		
		double[] xyz = Math3D.bound(x - safeZone, y - safeZone, z - safeZone, world.width - safeZone * 2, world.length - safeZone * 2, world.height - safeZone * 2); // todo : *better* safe zone bound all sides
		x = xyz[0] + safeZone;
		y = xyz[1] + safeZone;
		z = xyz[2] + safeZone;
	}
	
	private void applyForce(Math3D.Force f) {
		f.prepare(norm, rightUp);
		double force = FORCE / mass;
		vx += f.x * force;
		vy += f.y * force;
		vz += f.z * force;
		vAngleFlat += f.angleFlat * FORCE / inertiaFlat;
		vAngleUp += f.angleUp * FORCE / inertiaUp;
		vAngleTilt += f.angleTilt * FORCE / inertiaTilt;
	}
	
	int[] getBlock(int x, int y, int z) {
		int[] block = new int[6];
		if (x > 0)
			block[Math3D.LEFT] = part[x - 1][y][z].block[Math3D.RIGHT];
		if (x < part.length - 1)
			block[Math3D.RIGHT] = part[x + 1][y][z].block[Math3D.LEFT];
		if (y > 0)
			block[Math3D.FRONT] = part[x][y - 1][z].block[Math3D.BACK];
		if (y < part[0].length - 1)
			block[Math3D.BACK] = part[x][y + 1][z].block[Math3D.FRONT];
		if (z > 0)
			block[Math3D.BOTTOM] = part[x][y][z - 1].block[Math3D.TOP];
		if (z < part[0][0].length - 1)
			block[Math3D.TOP] = part[x][y][z + 1].block[Math3D.BOTTOM];
		return block;
	}
	
	public long getDrawCounter() {
		return drawCounter;
	}
}

// todo : angleZ invisible bug
// todo : smaller than cube drawing