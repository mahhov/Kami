package world;

import camera.Camera;
import engine.Painter;
import list.LList;
import shapes.Shape;
import shapes.drawelement.Line;
import shapes.drawelement.Surface;

class Cell {
	WorldChunk chunk;
	private LList<Shape> shapes;
	
	Cell(WorldChunk chunk) {
		this.chunk = chunk;
		shapes = new LList<>();
	}
	
	public void add(Shape shape) {
		chunk.count++;
		shapes = shapes.add(shape);
	}
	
	public void remove(LList<Shape> lShape) {
		chunk.count--;
		shapes = shapes.remove(lShape);
	}
	
	public void drawAll(Painter painter, Camera c, int xSide, int ySide, int zSide) {
		Surface surfaces[];
		Line line[];
		for (LList<Shape> lShape : shapes) {
			surfaces = lShape.node.drawSurfaces(xSide, ySide, zSide);
			line = lShape.node.drawLines();
			if (surfaces == null)
				remove(lShape);
			else {
				for (Surface s : surfaces)
					if (s != null) {
						double xy[][] = s.toCamera(c);
						painter.clipPolygon(xy, s.tempDistanceLight, s.color, s.clipState, s.frame);
					}
//				if (line != null)
//					for (Line l : line) {
//						double xy[][] = l.toCamera(c);
//						painter.line(xy[0][0], xy[1][0], xy[0][1], xy[1][1], 1, l.color);
//					}
			}
		}
	}
}