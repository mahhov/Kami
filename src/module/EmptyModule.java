package module;

import engine.Math3D;
import shapes.Shape;
import ships.Ship;

public class EmptyModule extends Module {
	public EmptyModule() {
		mass = 0;
		block = new int[6];
		for (int i = 0; i < block.length; i++)
			block[i] = BLOCK_NONE;
	}
	
	public Shape getShape(double xc, double yc, double zc, Math3D.Angle angle, Math3D.Angle angleZ, Math3D.Angle angleTilt, double[] rightUp, int[] block, Ship ship) {
		return null;
	}
}
