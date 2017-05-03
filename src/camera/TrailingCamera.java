package camera;

import control.Controller;
import engine.Math3D;
import paint.Painter;

public class TrailingCamera extends Camera {
	private static final double MIN_TRAIL = 5, MAX_TRAIL = 60, TRAIL_SPEED = 2.5;
	private static final double MIN_FOLLOW_UP = 0, MAX_FOLLOW_UP = 25, FOLLOW_UP_SPEED = 1;
	private static final double ANGLE_SPEED = .1, MOTION_AVG = .1, MOUSE_DAMP_SPEED = .01;
	private double trailDistance;
	private double followUp;
	private Math3D.Angle trailAngle, trailAngleZ; // todo: camera tilt
	Follow follow;
	
	public TrailingCamera() {
		trailDistance = 17.5;
		followUp = 8.5;
		trailAngle = new Math3D.Angle(0);
		trailAngleZ = new Math3D.Angle(0);
	}
	
	public void setFollow(Follow follow) {
		this.follow = follow;
	}
	
	public void move(Controller c) {
		if (c.isKeyDown(Controller.KEY_R))
			followUp = Math3D.min(followUp + FOLLOW_UP_SPEED, MAX_FOLLOW_UP);
		if (c.isKeyDown(Controller.KEY_F))
			followUp = Math3D.max(followUp - FOLLOW_UP_SPEED, MIN_FOLLOW_UP);
		
		// distance
		if (c.isKeyDown(Controller.KEY_X))
			trailDistance = Math3D.min(trailDistance + TRAIL_SPEED, MAX_TRAIL);
		if (c.isKeyDown(Controller.KEY_Z))
			trailDistance = Math3D.max(trailDistance - TRAIL_SPEED, MIN_TRAIL);
		
		Painter.debugString[3] = "trail distance (X & Z): " + trailDistance + " . follow up distance (R and F): " + followUp;
		
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
