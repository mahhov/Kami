package particle;

import engine.Math3D;

import java.awt.*;

public class SmokeParticle extends Particle {
	private static final double VELOCITY_SPREAD = .03, VZ_OFFSET = -.03, POS_SPREAD = .3;
	
	public SmokeParticle(double x, double y, double z) {
		super(x + Math3D.rand(-POS_SPREAD, POS_SPREAD), y + Math3D.rand(-POS_SPREAD, POS_SPREAD), z + Math3D.rand(-POS_SPREAD, POS_SPREAD), .1, Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), VZ_OFFSET + Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Color.LIGHT_GRAY, 600);
	}
}
