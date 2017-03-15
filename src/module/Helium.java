package module;

import engine.Math3D;
import ships.Ship;
import world.World;

import java.awt.*;

import static engine.Math3D.TOP_VECTOR;

public class Helium extends Module {
	private static final double force = .0005;
	private double[] location;
	private Color[] color;
	Ship ship;
	
	public Helium(Ship ship) {
		color = new Color[6];
		this.ship = ship;
	}
	
	public void set(int dir, double[] location) {
		this.location = location;
		for (int i = 0; i < color.length; i++)
			color[i] = Color.CYAN;
	}
	
	public void react(World world, Math3D.Force force, boolean[] control) {
		double[] dir = new double[3];
		
		double[] absDir = TOP_VECTOR;
		double[] up = new double[] {ship.rightUp[3], ship.rightUp[4], ship.rightUp[5]};
		dir[0] = Math3D.dotProductUnormalized(absDir, ship.rightUp);
		dir[1] = Math3D.dotProductUnormalized(absDir, ship.norm);
		dir[2] = Math3D.dotProductUnormalized(absDir, up);
		
		force.add(this.force, dir, location);
	}
	
	public Color[] getColors() {
		return color;
	}
}
