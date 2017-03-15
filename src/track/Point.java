package track;

import engine.Math3D;

class Point {
	double x, y, z;
	
	Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	Point subtract(Point p) {
		return new Point(x - p.x, y - p.y, z - p.z);
	}
	
	Point add(Point p) {
		return new Point(x + p.x, y + p.y, z + p.z);
	}
	
	Point scale(double scale) {
		scale /= Math3D.magnitude(toArray());
		return new Point(x * scale, y * scale, z * scale);
	}
	
	Point move(Point direction, double distance) {
		direction = direction.scale(distance);
		return new Point(x + direction.x, y + direction.y, z + direction.z);
	}
	
	Point average(Point p) {
		return new Point((p.x + x) / 2, (p.y + y) / 2, (p.z + z) / 2);
	}
	
	double[] toArray() {
		return new double[] {x, y, z};
	}
}