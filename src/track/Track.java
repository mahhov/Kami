package track;

import list.LList;
import world.World;

public class Track {
	LList<Path> path;
	
	public Track() {
		path = new LList<>();
		double width = 100;
		Point[] points = new Point[8];
		int corner = 20, length = 100;
		int c = 0;
		points[c++] = new Point(corner, 0, 0);
		points[c++] = new Point(corner + length, 0, 0);
		points[c++] = new Point(length + corner * 2, corner, 0);
		points[c++] = new Point(length + corner * 2, length + corner, 0);
		points[c++] = new Point(length + corner, length + corner * 2, 0);
		points[c++] = new Point(corner, length + corner * 2, 0);
		points[c++] = new Point(0, length + corner, 0);
		points[c++] = new Point(0, corner, 0);
		
		for (int i = 0; i < points.length - 1; i++)
			path = path.add(new Path(points[i], points[i + 1], width, width));
		path = path.add(new Path(points[points.length - 1], points[0], width, width));
	}
	
	public void addToWorld(World world) {
		for (LList<Path> pn : path)
			pn.node.addToWorld(world);
	}
}