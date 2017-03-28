package particle;

import engine.Math3D;
import shapes.CubeFrame;
import shapes.ShapeParent;
import world.World;

import java.awt.*;

public class Particle implements ShapeParent {
	private static final double FRICTION = 1, GRAVITY = 0;
	private double x, y, z;
	private double vx, vy, vz;
	private double width, height, thick;
	private Color color;
	private Math3D.Angle angle, angleZ, angleTilt;
	private double[] norm, rightUp;
	private long drawCounter;
	
	public Particle(double x, double y, double z, double width, double height, double thick, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double vx, double vy, double vz, Color color, int time) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.thick = thick;
		this.angle = angle;
		this.angleZ = angleZ;
		this.angleTilt = angleTilt;
		computeAxis();
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.color = color;
		drawCounter = time;
	}
	
	private void computeAxis() {
		norm = Math3D.norm(angle, angleZ, 1);
		rightUp = Math3D.axisVectorsTilt(norm, 1, angleZ, angleTilt);
	}
	
	public boolean update(World world) {
		if (drawCounter-- == 0)
			return true;
		move(world);
		addToWorld(world);
		return false;
	}
	
	private void move(World world) {
		vx *= FRICTION;
		vy *= FRICTION;
		vz = (vz + GRAVITY) * FRICTION;
		
		x += vx;
		y += vy;
		z += vz;
		
		double[] xyz = Math3D.bound(x, y, z, world.width, world.length, world.height);
		x = xyz[0];
		y = xyz[1];
		z = xyz[2];
	}
	
	private void addToWorld(World world) {
		world.addShape((int) x, (int) y, (int) z, new CubeFrame(x, y, z, angle, angleZ, angleTilt, .1, this));
	}
	
	public long getDrawCounter() {
		return drawCounter;
	}
}