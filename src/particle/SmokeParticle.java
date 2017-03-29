package particle;

import engine.Math3D;

import java.awt.*;

public class SmokeParticle extends Particle {
	private final static double VELOCITY_SPREAD = .03, VZ_OFFSET = -.03;
	
	public SmokeParticle(double x, double y, double z) {
		super(x, y, z, .1, Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), VZ_OFFSET + Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Color.WHITE, 300);
	}
}
