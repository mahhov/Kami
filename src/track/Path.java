package track;

import shapes.Flat;
import world.World;

import java.awt.*;

class Path {
	private Point[] corner; // order: backLeft, backRight, frontLeft, frontRight
	private Line bone;
	
	Path(Point back, Point front, double backWidth, double frontWidth) {
		bone = new Line(back, front);
		Point perpendicularVector = bone.getPerpendicularVector();
		corner = new Point[] {back.move(perpendicularVector, -backWidth / 2), back.move(perpendicularVector, backWidth / 2), front.move(perpendicularVector, -frontWidth / 2), front.move(perpendicularVector, frontWidth / 2)};
	}
	
	Path(Point backLeft, Point backRight, Point frontLeft, Point frontRight) {
		bone = new Line(backLeft.average(backRight), frontLeft.average(frontRight));
		corner = new Point[] {backLeft, backRight, frontLeft, frontRight};
	}
	
	double getHeight(double x, double y) {
		return bone.getHeight(x, y);
	}
	
	void addToWorld(World world) {
		for (int i = 0; i < bone.distance; i += 1) {
			Point p = bone.interpolate(i / bone.distance);
			world.addShape((int) p.x, (int) p.y, (int) p.z, new Flat(p.x, p.y, p.z, Color.RED));
		}
	}
}
