package particle;

import engine.Math3D;

import java.awt.*;

public class SmokeParticle extends Particle {
	
	public SmokeParticle(double x, double y, double z) {
		super(x, y, z, 1, 1, 1, new Math3D.Angle(0), new Math3D.Angle(0), new Math3D.Angle(0), 0, 0, 0, Color.WHITE, 1000);
	}
}
