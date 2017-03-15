package world;

import camera.Camera;
import engine.Painter;
import list.LList;
import shapes.Shape;
import shapes.Surface;

public class Cell {
	Chunk chunk;
	private LList<Shape> shapes;
	
	Cell(Chunk chunk) {
		this.chunk = chunk;
		shapes = new LList<>();
	}
	
	public void add(Shape shape) {
		chunk.count++;
		shapes = shapes.add(shape);
	}
	
	public void remove(LList lShape) {
		chunk.count--;
		shapes = shapes.remove(lShape);
	}
	
	public void drawAll(Painter painter, Camera c, int xSide, int ySide, int zSide) {
		Surface surfaces[];
		for (LList<Shape> lShape : shapes) {
			surfaces = lShape.node.draw(xSide, ySide, zSide);
			if (surfaces == null)
				remove(lShape);
			else
				for (Surface s : surfaces)
					if (s != null)
						painter.clipPolygon(s.toCamera(c), s.tempDistanceLight, s.color, s.clipState, s.frame);
		}
	}
	
	public boolean isEmpty() {
		return shapes == null;
	}
}
