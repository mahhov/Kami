package world;

import camera.Camera;
import engine.Painter;
import engine.Timer;
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
		Timer.timePause(3);
		Surface surfaces[];
		Line line[];
		for (LList<Shape> lShape : shapes) {
			Timer.timePause(4);
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
				if (line != null && false)
					for (Line l : line)
						if (l != null) {
							double xy[][] = l.toCamera(c);
							painter.line(xy[0][0], xy[1][0], xy[0][1], xy[1][1], 1, l.color);
						}
			}
			Timer.timePause(4);
		}
		Timer.timePause(3);
	}
}