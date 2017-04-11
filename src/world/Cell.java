package world;

import camera.Camera;
import engine.Painter;
import list.LList;
import shapes.Shape;
import shapes.Surface;

public class Cell {
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
		for (LList<Shape> lShape : shapes) {
			surfaces = lShape.node.draw(xSide, ySide, zSide);
			if (surfaces == null)
				remove(lShape);
			else
				for (Surface s : surfaces)
					if (s != null) {
						double xy[][] = s.toCamera(c);
						painter.clipPolygon(xy, s.tempDistanceLight, s.color, s.clipState, s.frame);
					}
		}
	}
}