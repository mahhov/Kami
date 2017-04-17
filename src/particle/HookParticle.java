package particle;

import engine.Math3D;

import java.awt.*;

public class HookParticle extends Particle {
	private static final double VELOCITY_SPREAD = .01, POS_SPREAD = .3;
	private static final Color COLOR = new Color(120,160,250);
	
	public HookParticle(double x, double y, double z) {
		super(x + Math3D.rand(-POS_SPREAD, POS_SPREAD), y + Math3D.rand(-POS_SPREAD, POS_SPREAD), z + Math3D.rand(-POS_SPREAD, POS_SPREAD), .1, Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), Math3D.rand(-VELOCITY_SPREAD, VELOCITY_SPREAD), COLOR, 300);
	}
}
