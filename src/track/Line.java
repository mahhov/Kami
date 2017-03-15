package track;

import engine.Math3D;

class Line {
	private Point p1, p2;
	private Point delta;
	private double distance;
	
	Line(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
		delta = p2.subtract(p1);
		distance = Math3D.magnitude(this.delta.toArray());
	}
	
	double getHeight(double x, double y) {
		Point delta = p1.subtract(new Point(x, y, 0));
		double weight = Math3D.dotProduct(delta.toArray(), this.delta.toArray());
		return p1.z * weight + p2.z * (1 - weight);
	}
	
	Point getPerpendicularVector() {
		return new Point(delta.y, -delta.x, 0);
	}
}
