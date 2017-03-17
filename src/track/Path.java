package track;

import shapes.Flat;
import world.World;

import java.awt.*;

class Path {
	private Line bone;
	private double backWidth, frontWidth;
	Point perpendicularVector;
	
	Path(Point back, Point front, double backWidth, double frontWidth) {
		bone = new Line(back, front);
		perpendicularVector = bone.getPerpendicularVector();
		this.backWidth = backWidth;
		this.frontWidth = frontWidth;
	}
	
	double getHeight(double x, double y) {
		return bone.getHeight(x, y);
	}
	
	void addToWorld(World world) {
		double progress, width;
		Point perpendicularVector = bone.getPerpendicularVector();
		for (int i = 0; i < bone.getDistance(); i += 1) {
			progress = i / bone.getDistance();
			width = weightInterpolate(progress);
			for (int j = (int) -width; j < width; j += 1) {
				Point p = bone.interpolate(progress);
				p = p.move(perpendicularVector, j);
				world.addShape((int) p.x, (int) p.y, (int) p.z, new Flat(p.x, p.y, p.z, bone.getDelta().toArray(), .25, Color.RED));
			}
		}
	}
	
	double weightInterpolate(double weight) {
		return backWidth + (frontWidth - backWidth) * weight;
	}
}
