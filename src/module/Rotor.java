package module;

import engine.Math3D;
import shapes.Cube;
import world.World;

import java.awt.*;

import static engine.Math3D.*;

public class Rotor extends Module {
	private static final double force = .03;
	private double[] direction, location;
	private int opDir; // opposite direction
	private boolean[] control;
	private boolean active;
	private Color[] color;
	
	public Rotor() {
		control = new boolean[6];
		color = new Color[6];
	}
	
	public void set(int dir, double[] location) {
		opDir = Math3D.flipDirection(dir);
		this.location = location;
		
		switch (dir) {
			case Math3D.LEFT:
				if (location[1] < 0)
					control[Math3D.RIGHT] = true;
				else
					control[Math3D.LEFT] = true;
				direction = LEFT_VECTOR;
				break;
			case Math3D.RIGHT:
				if (location[1] < 0)
					control[Math3D.LEFT] = true;
				else
					control[Math3D.RIGHT] = true;
				direction = RIGHT_VECTOR;
				break;
			case Math3D.FRONT:
				control[Math3D.FRONT] = true;
				direction = FRONT_VECTOR;
				break;
			case Math3D.BACK:
				control[Math3D.BACK] = true;
				direction = BACK_VECTOR;
				break;
			case Math3D.TOP:
				if (location[1] < 0)
					control[Math3D.BOTTOM] = true;
				else
					control[Math3D.TOP] = true;
				direction = TOP_VECTOR;
				break;
			case Math3D.BOTTOM:
				if (location[1] < 0)
					control[Math3D.TOP] = true;
				else
					control[Math3D.BOTTOM] = true;
				direction = BOTTOM_VECTOR;
				break;
			default:
				direction = ZERO_VECTOR;
		}
		
		for (int i = 0; i < color.length; i++)
			if (i == opDir)
				color[i] = Cube.SECONDARY_COLOR;
			else
				color[i] = Color.DARK_GRAY;
	}
	
	public void react(World world, Force force, boolean[] control) {
		active = false;
		for (int i = 0; i < this.control.length; i++)
			if (this.control[i] && control[i]) {
				active = true;
				break;
			}
		
		if (active)
			force.add(this.force, direction, location);
	}
	
	public Color[] getColors() {
		if (active)
			color[opDir] = Cube.TERNARY_COLOR;
		else
			color[opDir] = Cube.SECONDARY_COLOR;
		return color;
	}
	
	
}
