package terrain.generator;

import engine.Math3D;
import terrain.Terrain;
import terrain.module.FullGray;

import static terrain.Terrain.CHUNK_SIZE;

public class TreeGenerator extends Generator {
	public TreeGenerator(Terrain terrain) {
		super(terrain);
	}
	
	public int getwidth() {
		return 10000;
	}
	
	public int getlength() {
		return 10000;
	}
	
	public int getheight() {
		return 200;
	}
	
	public void generate(int cx, int cy, int cz) {
		if (cz != 0)
			return;
		
		for (int x = cx * CHUNK_SIZE; x < cx * CHUNK_SIZE + CHUNK_SIZE; x++)
			for (int y = cy * CHUNK_SIZE; y < cy * CHUNK_SIZE + CHUNK_SIZE; y++) {
				terrain.add(x, y, 0, new FullGray());
				if (Math.random() > .9993)
					generateTree(x, y, 0);
			}
		if (cx == 3 && cy == 0) {
			for (int x = 0; x < 100; x++)
				for (int z = 3; z < 10; z++)
					terrain.add(x, 5, z, new FullGray());
		}
	}
	
	private void generateTree(int x, int y, int floor) {
		int thick = 5;
		x = Math3D.maxMin(x, terrain.width - thick * 2, thick);
		y = Math3D.maxMin(y, terrain.length - thick * 2, thick);
		int height = Math3D.rand(20, 30) + 10;
		int brushSpread = Math3D.rand(1, 3) + height / 5 - 4;
		int brushHeight = Math3D.rand(1, 3);
		for (int xi = x - thick; xi <= x + thick; xi++)
			for (int yi = y - thick; yi <= y + thick; yi++)
				for (int z = floor; z < height + floor; z++)
					terrain.add(xi, yi, z, new FullGray());
		int xs = Math3D.max(x - brushSpread, 0);
		int xe = Math3D.min(x + brushSpread, terrain.width - 1);
		int ys = Math3D.max(y - brushSpread, 0);
		int ye = Math3D.min(y + brushSpread, terrain.length - 1);
		for (int xi = xs; xi <= xe; xi++)
			for (int yi = ys; yi <= ye; yi++)
				for (int z = height + floor; z < height + brushHeight + floor; z++)
					terrain.add(xi, yi, z, new FullGray());
	}
}
