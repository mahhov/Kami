package engine;


public class Math3D {
	public static final double EPSILON = 0.00001;
	public static final int LEFT = 0, RIGHT = 1, FRONT = 2, BACK = 3, BOTTOM = 4, TOP = 5, NONE = -1;
	public static final double[] LEFT_VECTOR = new double[] {-1, 0, 0}, RIGHT_VECTOR = new double[] {1, 0, 0}, FRONT_VECTOR = new double[] {0, 1, 0}, BACK_VECTOR = new double[] {0, -1, 0}, TOP_VECTOR = new double[] {0, 0, 1}, BOTTOM_VECTOR = new double[] {0, 0, -1}, ZERO_VECTOR = new double[] {0, 0, 0};
	public static final double sqrt2 = Math.sqrt(2), sqrt2Div3 = Math.sqrt(2.0 / 3), sqrt1Div2 = 1 / sqrt2, sqrt1Div5 = 1 / Math.sqrt(5), sqrt3 = Math.sqrt(3);
	
	public static int flipDirection(int direction) {
		switch (direction) {
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			case FRONT:
				return BACK;
			case BACK:
				return FRONT;
			case BOTTOM:
				return TOP;
			case TOP:
				return BOTTOM;
			default:
				return NONE;
		}
	}
	
	public static double magnitude(double[] c) {
		double mag = 0;
		for (double cc : c)
			mag += cc * cc;
		return Math.sqrt(mag);
	}
	
	public static double magnitude(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double magnitude(double dx, double dy) {
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public static double magnitudeSqrd(double x, double y, double z) {
		return x * x + y * y + z * z;
	}
	
	public static double magnitudeSqrd(double dx, double dy) {
		return dx * dx + dy * dy;
	}
	
	public static double[] normalize(double[] v) {
		double mag = magnitude(v);
		for (int i = 0; i < v.length; i++)
			v[i] /= mag;
		return v;
	}
	
	public static double[] setMagnitude(double[] v, double mag) {
		double mult = mag / magnitude(v);
		return new double[] {v[0] * mult, v[1] * mult, v[2] * mult};
	}
	
	public static double dotProduct(double x, double y, double z, double x2, double y2, double z2) {
		double mag1 = magnitude(x, y, z);
		double mag2 = magnitude(x2, y2, z2);
		return dotProductUnormalized(x, y, z, x2, y2, z2) / mag1 / mag2;
	}
	
	public static double dotProductUnormalized(double x, double y, double z, double x2, double y2, double z2) {
		return x * x2 + y * y2 + z * z2;
	}
	
	public static double dotProductUnormalized(double[] v1, double[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}
	
	public static double[] crossProductNormalized(double[] v1, double[] v2) {
		double[] cross = crossProductUnormalized(v1, v2);
		double mag = magnitude(cross[0], cross[1], cross[2]);
		cross[0] /= mag;
		cross[1] /= mag;
		cross[2] /= mag;
		return cross;
	}
	
	public static double[] crossProductNormalized(double[] v1, double[] v2, boolean flipped) {
		int mult = flipped ? -1 : 1;
		double[] cross = crossProductUnormalized(v1, v2);
		double mag = magnitude(cross[0], cross[1], cross[2]) * mult;
		cross[0] /= mag;
		cross[1] /= mag;
		cross[2] /= mag;
		return cross;
	}
	
	public static double[] crossProductUnormalized(double[] v1, double[] v2) {
		return new double[] {v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0]};
	}
	
	public static double[] halfAxisVectors(double[] norm) {
		return axisVectors(norm, .5);
	}
	
	public static double[] axisVectors(double[] norm, double mag) {
		if (norm[0] == 0 && norm[1] == 0)
			norm[0] = EPSILON;
		
		double rightMag = magnitude(norm[1], -norm[0], 0);
		double rightx = norm[1] / rightMag * mag;
		double righty = -norm[0] / rightMag * mag;
		
		double upx = -norm[2] * norm[0];
		double upy = -norm[2] * norm[1];
		double upz = (norm[0] * norm[0] + norm[1] * norm[1]);
		double upMag = magnitude(upx, upy, upz);
		
		return new double[] {rightx, righty, 0, upx / upMag * mag, upy / upMag * mag, upz / upMag * mag};
	}
	
	public static double[] axisVectorsTilt(double[] norm, double mag, Angle angleZ, Angle tilt) {
		return axisVectorsTilt(norm, mag, mag, angleZ, tilt);
	}
	
	public static double[] axisVectorsTilt(double[] norm, double magRight, double magUp, Angle angleZ, Angle tilt) {
		double normMag = magnitude(norm);
		double[] zTilt = new double[] {-norm[1] / normMag * tilt.sin(), norm[0] / normMag * tilt.sin(), tilt.cos()};
		double[] right = crossProductNormalized(norm, zTilt, angleZ.cos() < 0);
		double[] up = crossProductNormalized(norm, right);
		double rightScale = magRight / magnitude(right);
		double upScale = -magUp / magnitude(up);
		return new double[] {right[0] * rightScale, right[1] * rightScale, right[2] * rightScale, up[0] * upScale, up[1] * upScale, up[2] * upScale};
	}
	
	public static double[] upVector(double x, double y, double z) {
		double upx = -z * x;
		double upy = -z * y;
		double upz = (x * x + y * y);
		double mag = magnitude(upx, upy, upz);
		return new double[] {upx / mag, upy / mag, upz / mag};
	}
	
	public static double[] norm(Angle angle, Angle angleZ) {
		double[] normal = new double[3];
		normal[0] = angle.cos() * angleZ.cos();
		normal[1] = angle.sin() * angleZ.cos();
		normal[2] = angleZ.sin();
		return normal;
	}
	
	public static double[] norm(Angle angle, Angle angleZ, double size) {
		double[] normal = new double[3];
		normal[0] = angle.cos() * angleZ.cos() * size;
		normal[1] = angle.sin() * angleZ.cos() * size;
		normal[2] = angleZ.sin() * size;
		return normal;
	}
	
	// rotates point x,y,z around axis u,v,w by angle cos,sin and by angle cos,-sin
	public static double[] rotation(double[] xyz, double[] uvw, double cos, double sin) {
		double temp1 = uvw[0] * xyz[0] + uvw[1] * xyz[1] + uvw[2] * xyz[2];
		double oneCos = 1 - cos;
		
		double rotxBase = uvw[0] * temp1 * oneCos + xyz[0] * cos;
		double rotyBase = uvw[1] * temp1 * oneCos + xyz[1] * cos;
		double rotzBase = uvw[2] * temp1 * oneCos + xyz[2] * cos;
		
		double rotxDelta = (uvw[2] * xyz[1] + uvw[1] * xyz[2]) * sin;
		double rotyDelta = (uvw[2] * xyz[0] + uvw[0] * xyz[2]) * sin;
		double rotzDelta = (uvw[1] * xyz[0] + uvw[0] * xyz[1]) * sin;
		
		return new double[] {rotxBase + rotxDelta, rotyBase + rotyDelta, rotzBase + rotzDelta, rotxBase - rotxDelta, rotyBase - rotyDelta, rotzBase - rotzDelta};
	}
	
	public static double maxMin(double value, double max, double min) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}
	
	public static int min(int val, int min) {
		if (val > min)
			return min;
		return val;
	}
	
	public static double min(double val, double min) {
		if (val > min)
			return min;
		return val;
	}
	
	public static int min(int val, int val2, int val3) {
		int min;
		if (val2 < val)
			min = val2;
		else
			min = val;
		if (min < val3)
			return min;
		return val3;
	}
	
	public static double min(double val, double val2, double val3) {
		double min;
		if (val2 < val)
			min = val2;
		else
			min = val;
		if (min < val3)
			return min;
		return val3;
	}
	
	public static int max(int val, int max) {
		if (val < max)
			return max;
		return val;
	}
	
	public static double max(double val, double max) {
		if (val < max)
			return max;
		return val;
	}
	
	public static int max(int val, int val2, int val3) {
		int max;
		if (val2 > val)
			max = val2;
		else
			max = val;
		if (max > val3)
			return max;
		return val3;
	}
	
	public static boolean isZero(double value) {
		return value < Math3D.EPSILON && value > -Math3D.EPSILON;
	}
	
	public static double notZero(double value) {
		if (value < Math3D.EPSILON && value > -Math3D.EPSILON)
			if (value > 0)
				return EPSILON;
			else
				return -EPSILON;
		return value;
	}
	
	public static double notZero(double value, double defaultZero) {
		if (value < Math3D.EPSILON && value > -Math3D.EPSILON)
			return defaultZero;
		return value;
	}
	
	private static double sinTable[];
	private static int trigAccuracy;
	
	static void loadTrig(int accuracy) {
		trigAccuracy = accuracy;
		sinTable = new double[accuracy];
		for (int i = 0; i < accuracy; i++)
			sinTable[i] = Math.sin(Math.PI / 2 * i / accuracy);
	}
	
	public static double xsin(double xd) {
		double x = xd / Math.PI * 2;
		
		int sign;
		if (x < 0) {
			sign = -1;
			x = -x;
		} else
			sign = 1;
		
		x = x - ((int) (x / 4)) * 4;
		if (x >= 2) {
			sign *= -1;
			x -= 2;
		}
		
		if (x > 1)
			x = 2 - x;
		
		if (x == 1)
			return sign;
		
		if ((int) (x * trigAccuracy) == -2147483648)
			System.out.println("WOW");
		
		return sign * sinTable[(int) (x * trigAccuracy)];
		
	}
	
	public static double xcos(double x) {
		return xsin(x + Math.PI / 2);
	}
	
	public static double sin2(double sin1, double cos1, double sin2, double cos2) {
		// sin(a+b)
		return sin1 * cos2 + cos1 * sin2;
	}
	
	public static double cos2(double sin1, double cos1, double sin2, double cos2) {
		// cos(a+b)
		return cos1 * cos2 - sin1 * sin2;
	}
	
	public static double tan2(double sin1, double cos1, double sin2, double cos2) {
		// tan(a+b)
		double tan1 = sin1 / cos1;
		double tan2 = sin2 / cos2;
		return (tan1 + tan2) / (1 - tan1 * tan2);
	}
	
	public static double[] transform(double[] a, int scale, int shift) {
		double[] aa = new double[a.length];
		for (int i = 0; i < a.length; i++)
			aa[i] = a[i] * scale + shift;
		return aa;
	}
	
	public static int[][] transform(double[][] a, int scale, int shift) {
		int[][] aa = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[i].length; j++)
				aa[i][j] = (int) (a[i][j] * scale + shift);
		return aa;
	}
	
	public static double pow(double base, int p) {
		double result = 1;
		while (p > 0) {
			if ((p & 1) != 0)
				result *= base;
			base *= base;
			p = p >>> 1;
		}
		return result;
	}
	
	public static class Angle {
		private double angle;
		private double angleCos, angleSin;
		private boolean updated;
		
		public Angle(double angle) {
			this.angle = angle;
		}
		
		private void update() {
			angleCos = xcos(angle);
			angleSin = xsin(angle);
			updated = true;
		}
		
		public double get() {
			return angle;
		}
		
		public void set(double angle) {
			this.angle = angle;
			updated = false;
		}
		
		public double sin() {
			if (!updated)
				update();
			return angleSin;
		}
		
		public double cos() {
			if (!updated)
				update();
			return angleCos;
		}
		
		public void bound() {
			if (angle < -Math.PI / 2)
				set(-Math.PI / 2);
			if (angle > Math.PI / 2)
				set(Math.PI / 2);
		}
		
		public static void rotate(Angle angle, Angle angleZ, Angle angleTilt, double[] rightUp, double flat, double up) {
			// rotating
			double angleZCos = notZero(angleZ.cos());
			
			angle.set(angle.get() + (angleTilt.cos() * flat + angleTilt.sin() * up) / angleZCos);
			angleZ.set(angleZ.get() + (-angleTilt.sin() * flat + angleTilt.cos() * up));
			
			// restoring tilt
			double newRightSin = -angle.cos();
			double newRightCos = angle.sin();
			double dot = Math3D.dotProduct(rightUp[0], rightUp[1], rightUp[2], newRightCos, newRightSin, 0);
			double newTilt = Math.acos(dot);
			if (rightUp[2] < 0)
				newTilt = -newTilt;
			angleTilt.set(newTilt);
		}
	}
	
	public static double[] bound(double x, double y, double z, int width, int length, int height) {
		if (x < 0)
			x = 0;
		if (x > width - Math3D.EPSILON)
			x = width - Math3D.EPSILON;
		if (y < 0)
			y = 0;
		if (y > length - Math3D.EPSILON)
			y = length - Math3D.EPSILON;
		if (z < 0)
			z = 0;
		if (z > height - Math3D.EPSILON)
			z = height - Math3D.EPSILON;
		return new double[] {x, y, z};
	}
	
	public static class Force {
		public double x, y, z; // absolute
		public double angleFlat, angleUp, angleTilt;
		double[] netTorque;
		private double rx, ry, rz; // relative
		
		public Force() {
			netTorque = new double[3];
		}
		
		public void add(double force, double[] direction, double[] location) {
			double dmag = magnitude(direction);
			force /= dmag;
			rx += direction[0] * force;
			ry += direction[1] * force;
			rz += direction[2] * force;
			double[] torque = crossProductUnormalized(location, direction);
			netTorque[0] += torque[0] * force;
			netTorque[1] += torque[1] * force;
			netTorque[2] += torque[2] * force;
		}
		
		public void prepare(double[] norm, double[] rightUp) {
			x = ry * norm[0] + rx * rightUp[0] + rz * rightUp[3];
			y = ry * norm[1] + rx * rightUp[1] + rz * rightUp[4];
			z = ry * norm[2] + rx * rightUp[2] + rz * rightUp[5];
			angleFlat = netTorque[2]; // netTorque . <0,0,1>
			angleUp = netTorque[0]; // netTorque . <1,0,0>
			angleTilt = -netTorque[1]; // netTorque . <0,-1,0>
		}
	}
	
	public static String doubles2Str(double[] d, int start) {
		String str = "";
		for (int i = start; i < start + 3; i++)
			str += (int) (d[i] * 1000) / 1000.0 + " ";
		return str;
	}
	
	public static String doubles2Str(double[] d) {
		String str = "";
		for (double dd : d)
			str += (int) (dd * 1000) / 1000.0 + " ";
		return str;
	}
	
	public static int rand(int max) {
		return (int) (Math.random() * max);
	}
}
