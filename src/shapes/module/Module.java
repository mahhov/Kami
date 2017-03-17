package shapes.module;

import engine.Math3D;
import shapes.Cube;
import shapes.Shape;
import ships.Ship;
import world.World;

import java.awt.*;

import static engine.Math3D.BLOCK_FULL;
import static engine.Math3D.BLOCK_NONE;

public abstract class Module {
	public int mass;
	public int[] block;
	
	Module() {
		mass = 1;
		block = new int[6];
		for (int i = 0; i < block.length; i++)
			block[i] = BLOCK_FULL;
	}
	
	public void set(int dir, double[] location) {
	}
	
	public void react(World world, Math3D.Force force, boolean[] control) {
	}
	
	public Color[] getColors() {
		return null;
	}
	
	public Shape getShape(double xc, double yc, double zc, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double[] rightUp, int[] block, Ship ship) {
		boolean[] side = new boolean[6];
		for (int i = 0; i < side.length; i++)
			side[i] = block[i] == BLOCK_NONE;
		return new Cube(xc, yc, zc, angle, angleZ, angleTilt, .5, side, getColors(), ship);
	}
}
