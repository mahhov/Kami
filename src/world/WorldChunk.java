package world;

import camera.Camera;
import paint.painterelement.PainterQueue;
import shapes.Shape;

class WorldChunk {
	private Cell[][][] cell;
	int count;
	
	WorldChunk(int size) {
		cell = new Cell[size][size][size];
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				for (int z = 0; z < size; z++)
					cell[x][y][z] = new Cell(this);
		count = 0;
	}
	
	boolean isEmpty() {
		return count == 0;
	}
	
	void add(int x, int y, int z, Shape shape) {
		cell[x][y][z].add(shape);
	}
	
	void draw(int x, int y, int z, PainterQueue painterQueue, Camera c, int xSide, int ySide, int zSide) {
		cell[x][y][z].drawAll(painterQueue, c, xSide, ySide, zSide);
	}
}