package module;

import engine.Math3D;
import shapes.Bar;
import shapes.Shape;
import ships.Ship;
import world.World;

import java.awt.*;

import static engine.Math3D.TOP_VECTOR;

public class ForwBlade extends Module {
	private static final double FORCE = .002;
	private double[] location;
	private boolean[] controlUp, controlDown;
	private static final int STATE_INACTIVE = 0, STATE_UP = 1, STATE_DOWN = 2;
	private int state;
	private Color[] color;
	private Ship ship;
	
	public ForwBlade(Ship ship) {
		for (int i = 0; i < block.length; i++)
			block[i] = BLOCK_PARTIAL;
		controlUp = new boolean[6];
		controlDown = new boolean[6];
		color = new Color[6];
		this.ship = ship;
	}
	
	public void set(int dir, double[] location) {
		this.location = location;
		
		if (location[0] < 0) {
			controlUp[Math3D.RIGHT] = true;
			controlDown[Math3D.LEFT] = true;
		} else if (location[0] > 0) {
			controlUp[Math3D.LEFT] = true;
			controlDown[Math3D.RIGHT] = true;
		}
		
		if (location[1] > 0) {
			controlUp[Math3D.TOP] = true;
			controlDown[Math3D.BOTTOM] = true;
		} else if (location[1] < 0) {
			controlUp[Math3D.BOTTOM] = true;
			controlDown[Math3D.TOP] = true;
		}
		
		for (int i = 0; i < color.length; i++)
			color[i] = Color.YELLOW;
	}
	
	public void react(World world, Math3D.Force force, boolean[] control) {
		state = STATE_INACTIVE;
		for (int i = 0; i < this.controlUp.length; i++)
			if (control[i])
				if (this.controlUp[i]) {
					state = STATE_UP;
					break;
				} else if (this.controlDown[i]) {
					state = STATE_DOWN;
					break;
				}
		
		double forwVelocity = ship.getVForward();
		double f = FORCE * forwVelocity * forwVelocity;
		if (forwVelocity < 0)
			f = -f;
		if (state == STATE_UP)
			force.add(f, TOP_VECTOR, location);
		else if (state == STATE_DOWN)
			force.add(-f, TOP_VECTOR, location);
	}
	
	public Color[] getColors() {
		return color;
	}
	
	public Shape getShape(double xc, double yc, double zc, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double[] rightUp, int[] block, Ship ship) {
		boolean[] side = new boolean[6], innerSide = new boolean[6];
		for (int i = 0; i < side.length; i++) {
			side[i] = block[i] != BLOCK_FULL;
			innerSide[i] = block[i] == BLOCK_NONE;
		}
		innerSide[Math3D.TOP] = true;
		innerSide[Math3D.BOTTOM] = true;
		
		Math3D.Angle angleInner = angle, angleZInner = angleZ, angleTiltInner = angleTilt;
		if (state != STATE_INACTIVE) {
			angleInner = new Math3D.Angle(angle.get());
			angleZInner = new Math3D.Angle(angleZ.get());
			angleTiltInner = new Math3D.Angle(angleTilt.get());
			if (state == STATE_UP)
				Math3D.Angle.rotate(angleInner, angleZInner, angleTiltInner, rightUp, 0, Math.PI / 10);
			else
				Math3D.Angle.rotate(angleInner, angleZInner, angleTiltInner, rightUp, 0, -Math.PI / 10);
		}
		return new Bar(xc, yc, zc, angle, angleZ, angleTilt, angleInner, angleZInner, angleTiltInner, .5, .2, side, innerSide, getColors(), ship);
	}
}
