package particle;

import engine.Math3D;

import java.awt.*;

public class SmokeParticle extends Particle {
	private final static double v = .1;
	
	public SmokeParticle(double x, double y, double z) {
		super(x, y, z, .1, new Math3D.Angle(0), new Math3D.Angle(0), new Math3D.Angle(0), Math3D.rand(-v, v), Math3D.rand(-v, v), Math3D.rand(-v, v), Color.WHITE, 100);
	}
}
