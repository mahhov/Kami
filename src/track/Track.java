package track;

import list.LList;
import world.World;

public class Track {
	LList<Path> path;
	
	public Track() {
		path = new LList<>();
		double width = 100;
		double edge = 50;
		Point[] points = new Point[8];
		int corner = 20, length = 100;
		int c = 0;
		points[c++] = new Point(edge + corner, edge + 0, 0);
		points[c++] = new Point(edge + corner + length, edge + 0, 0);
		points[c++] = new Point(edge + length + corner * 2, edge + corner, 0);
		points[c++] = new Point(edge + length + corner * 2, edge + length + corner, 0);
		points[c++] = new Point(edge + length + corner, edge + length + corner * 2, 0);
		points[c++] = new Point(edge + corner, edge + length + corner * 2, 0);
		points[c++] = new Point(edge + 0, edge + length + corner, 0);
		points[c++] = new Point(edge + 0, edge + corner, 0);
		
		for (int i = 0; i < points.length - 1; i++)
			path = path.add(new Path(points[i], points[i + 1], width, width));
		path = path.add(new Path(points[points.length - 1], points[0], width, width));
	}
	
	public void addToWorld(World world) {
		for (LList<Path> pn : path)
			pn.node.addToWorld(world);
	}
}