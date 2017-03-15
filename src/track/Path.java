package track;

class Path {
	private Point[] corner; // order: backLeft, backRight, frontLeft, frontRight
	private Line bone;
	
	Path(Point back, Point front, double backWidth, double frontWidth) {
		bone = new Line(back, front);
		Point perpindicularVector = bone.getPerpendicularVector();
		corner = new Point[] {back.move(perpindicularVector, -backWidth / 2), back.move(perpindicularVector, backWidth / 2), front.move(perpindicularVector, -frontWidth / 2), front.move(perpindicularVector, frontWidth / 2)};
	}
	
	Path(Point backLeft, Point backRight, Point frontLeft, Point frontRight) {
		bone = new Line(backLeft.average(backRight), frontLeft.average(frontRight));
		corner = new Point[] {backLeft, backRight, frontLeft, frontRight};
	}
	
	double getHeight(double x, double y) {
		return bone.getHeight(x, y);
	}
}
