package shapes.drawelement;

import camera.Camera;

class DrawElement {
	public double[][] coord;
	
	public double[][] toCamera(Camera camera) {
		double[] x = new double[coord.length], y = new double[coord.length];
		double ox, oy = 0, oz;
		for (int i = 0; i < coord.length; i++) {
			// set origin at camera
			ox = coord[i][0] - camera.x;
			oy = coord[i][1] - camera.y;
			oz = coord[i][2] - camera.z;
			
			// camera angle
			double ox2 = ox * camera.angle.sin() - oy * camera.angle.cos();
			oy = ox * camera.angle.cos() + oy * camera.angle.sin();
			ox = ox2;
			
			// camera z angle
			double oz2 = -oz * camera.angleZ.cos() + oy * camera.angleZ.sin();
			oy = oz * camera.angleZ.sin() + oy * camera.angleZ.cos();
			oz = oz2;
			
			// projection
			x[i] = ox / oy;
			y[i] = oz / oy;
		}
		setDistanceLight(oy);
		return new double[][] {x, y};
	}
	
	public void setDistanceLight(double distance) {
	}
}
