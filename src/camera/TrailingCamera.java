package camera;

import control.Controller;
import engine.Math3D;

public class TrailingCamera extends Camera {
	private final static double MIN_TRAIL = 5, MAX_TRAIL = 60;
	private final static double TRAIL_SPEED = 2.5, ANGLE_SPEED = .1, MOTION_AVG = .2, MOUSE_DAMP_SPEED = .01;
	private double trailDistance;
	private Math3D.Angle trailAngle, trailAngleZ; // todo: camera tilt
	Follow follow;
	private double followUp;
	
	public TrailingCamera() {
		trailDistance = MIN_TRAIL + (MAX_TRAIL - MIN_TRAIL) * .5;
		trailAngle = new Math3D.Angle(0);
		trailAngleZ = new Math3D.Angle(0);
	}
	
	public void setFollow(Follow follow) {
		this.follow = follow;
	}
	
	public void move(Controller c) {
		if (c.isKeyDown(Controller.KEY_R))
			followUp = Math3D.min(followUp + 1, 50);
		if (c.isKeyDown(Controller.KEY_F))
			followUp = Math3D.max(followUp - 1, 0);
		
		// distance
		if (c.isKeyDown(Controller.KEY_X))
			trailDistance = Math3D.min(trailDistance + TRAIL_SPEED, MAX_TRAIL);
		if (c.isKeyDown(Controller.KEY_Z))
			trailDistance = Math3D.max(trailDistance - TRAIL_SPEED, MIN_TRAIL);
		
		// trail angle
		int[] mouse = c.getMouseMovement();
		double angle = trailAngle.get() - mouse[0] * MOUSE_DAMP_SPEED;
		double angleZ = trailAngleZ.get() + mouse[1] * MOUSE_DAMP_SPEED;
		
		trailAngle.set(angle);
		trailAngleZ.set(angleZ);
		trailAngleZ.bound();
		
		// position
		double fz = follow.getZ() + followUp;
		double goalx = follow.getX() + trailAngle.cos() * trailAngleZ.cos() * trailDistance;
		double goaly = follow.getY() + trailAngle.sin() * trailAngleZ.cos() * trailDistance;
		double goalz = fz + trailAngleZ.sin() * trailDistance;
		moveTo(goalx, goaly, goalz, MOTION_AVG);
		
		// camera angle
		lookAt(follow.getX(), follow.getY(), fz);
	}
	
	public interface Follow {
		public double getX();
		
		public double getY();
		
		public double getZ();
		
		public double getAngle();
		
		public double getAngleZ();
	}
}
