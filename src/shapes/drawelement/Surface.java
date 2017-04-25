package shapes.drawelement;

import camera.Camera;
import engine.Math3D;

import java.awt.*;

public class Surface extends DrawElement {
	private double[] normal;
	private double light;
	public double tempDistanceLight;
	public Color color;
	public boolean frame;
	
	public int clipState;
	public static final int CLIP_NONE = 0, CLIP_ADD = 1, CLIP_SET = 2, CLIP_RESET = 3;
	
	public Surface(double x, double[] y, double[] z, boolean flipNormal) {
		int n = Math3D.min(y.length, z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] {x, y[i], z[i]};
		setNormal(flipNormal);
	}
	
	public Surface(double[] x, double y, double[] z, boolean flipNormal) {
		int n = Math3D.min(x.length, z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] {x[i], y, z[i]};
		setNormal(flipNormal);
	}
	
	public Surface(double[] x, double[] y, double z, boolean flipNormal) {
		int n = Math3D.min(x.length, y.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] {x[i], y[i], z};
		setNormal(flipNormal);
	}
	
	public Surface(double[] x, double[] y, double[] z, boolean flipNormal) {
		int n = Math3D.min(x.length, y.length, z.length);
		coord = new double[n][];
		for (int i = 0; i < n; i++)
			coord[i] = new double[] {x[i], y[i], z[i]};
		setNormal(flipNormal);
	}
	
	public Surface(double[] p1, double[] p2, double[] p3, double[] p4, boolean flipNormal) {
		coord = new double[][] {p1, p2, p3, p4};
		setNormal(flipNormal);
	}
	
	private Surface() {
	}
	
	private void setNormal(boolean flipNormal) {
		// uses first 3 coordinates
		// when flipNormal is false, normal points toward right hand rule
		normal = new double[3];
		
		// set coord 0 to origin
		double[] d1 = new double[] {coord[1][0] - coord[0][0], coord[1][1] - coord[0][1], coord[1][2] - coord[0][2]};
		double[] d2 = new double[] {coord[2][0] - coord[0][0], coord[2][1] - coord[0][1], coord[2][2] - coord[0][2]};
		
		// cross product
		normal = Math3D.crossProductNormalized(d1, d2, flipNormal);
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public void setLight(double l) {
		double ll = 0, lll;
		for (double[] lightSource : Camera.LIGHT_SOURCE) {
			lll = Math3D.dotProductUnormalized(normal, lightSource);
			if (lll > ll)
				ll = lll;
		}
		light = Math3D.min(l * ll, 1);
	}
	
	public void setClip(int clipState) {
		this.clipState = clipState;
	}
	
	public double[][] toCamera(Camera camera) {
		// only the surfaces with normals pointing towards camera and within camera view
		if (camera.facingTowards(normal, coord[0]) && camera.inView(coord))
			return super.toCamera(camera);
		return null;
	}
	
	public void setDistanceLight(double distance) {
		tempDistanceLight = light * Math3D.pow(Camera.FOG, (int) distance);
	}
	
	public static class NullSurface extends Surface {
		public double[][] toCamera(Camera camera) {
			return null;
		}
	}
}

// TODO: move camera.inview check to prior to cube/shape logic
// TODO: make sure facingTowards checks smaller than 180 degrees
// TODO: cache / reuse surface points