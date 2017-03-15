package module;

import engine.Math3D;
import projectiles.Projectile;
import ships.Ship;
import world.World;

import java.awt.*;

public class Blaster extends Module {
	private static final int LOAD_TIME = 100;
	private int load;
	private double[] direction;
	private double[] location;
	private Color[] color;
	Ship ship;
	
	public Blaster(Ship ship) {
		color = new Color[6];
		this.ship = ship;
	}
	
	public void set(int dir, double[] location) {
		this.location = location;
		for (int i = 0; i < color.length; i++)
			color[i] = Color.PINK;
	}
	
	public void react(World world, Math3D.Force force, boolean[] control) {
		if (load > 0)
			load--;
		if (load == 0) {
			load = LOAD_TIME;
			world.addProjectile(new Projectile(ship.x, ship.y, ship.z, ship.angle, ship.angleZ, ship.angleTilt, ship.norm[0], ship.norm[1], ship.norm[2]));
		}
	}
	
	public Color[] getColors() {
		return color;
	}
}

// todo : visual indicator of load and aim direciton