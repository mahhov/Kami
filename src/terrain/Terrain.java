package terrain;

import engine.Math3D;
import shapes.Shape;
import terrain.terrainModule.FullGray;
import terrain.terrainModule.TerrainModule;
import world.World;

public class Terrain {
	private TerrainModule[][][] part;
	private IntersectionFinder intersectionFinder;
	
	public Terrain() {
		intersectionFinder = new IntersectionFinder();
		part = new TerrainModule[200][200][100];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++) {
				part[x][y][0] = new FullGray();
				if (Math.random() > .999)
					generateTree(x, y);
			}
	}
	
	private void generateTree(int x, int y) {
		int thick = 5;
		x = Math3D.maxMin(x, part.length - thick * 2, thick);
		y = Math3D.maxMin(y, part[x].length - thick * 2, thick);
		int height = Math3D.rand(7, 14);
		int brushSpread = Math3D.rand(1, 4) + height / 5;
		int brushHeight = Math3D.rand(1, 3);
		for (int xi = x - thick; xi <= x + thick; xi++)
			for (int yi = y - thick; yi <= y + thick; yi++)
				for (int z = 0; z < height; z++)
					part[xi][yi][z] = new FullGray();
		//		for (int z = 0; z < height; z++)
		//			part[x][y][z] = new FullGray();
		int xs = Math3D.max(x - brushSpread, 0);
		int xe = Math3D.min(x + brushSpread, part.length - 1);
		int ys = Math3D.max(y - brushSpread, 0);
		int ye = Math3D.min(y + brushSpread, part[x].length - 1);
		for (int xi = xs; xi <= xe; xi++)
			for (int yi = ys; yi <= ye; yi++)
				for (int z = height; z < height + brushHeight; z++)
					part[xi][yi][z] = new FullGray();
	}
	
	public void addToWorld(World world) {
		Shape shape;
		int block[];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++)
				for (int z = 0; z < part[x][y].length; z++)
					if (part[x][y][z] != null) {
						block = getBlock(x, y, z);
						shape = part[x][y][z].getShape(x + .5, y + .5, z + .5, block);
						if (shape != null)
							world.addShape(x, y, z, shape);
					}
	}
	
	public boolean checkCollide(double x, double y, double z) {
		return part[(int) x][(int) y][(int) z] != null;
	}
	
	private class IntersectionFinder {
		private double[] orig, dir;
		private double x, y, z;
		private double nextx, nexty, nextz;
		private int intx, inty, intz;
		private double deltax, deltay, deltaz;
		private double movex, movey, movez, move;
		private double[] moveWhich;
		private double moved;
		private int collideNum;
		private boolean[] collide;
		
		private double[] find(double[] orig, double[] dir) {
			reset(orig, dir);
			
			while (true) {
				prefixComputeMove();
				
				if (moved + move > 1) {
					moveBy(1 - moved);
					
					if (isOk()) {
						x = orig[0] + dir[0];
						y = orig[1] + dir[1];
						z = orig[2] + dir[2];
						return new double[] {x, y, z};
					} else
						return new double[] {x, y, z};
				}
				
				moveBy(move + Math3D.EPSILON);
				
				if (!isOk()) {
					moveBy(move - Math3D.EPSILON);
					
					if (collideCheck((int) moveWhich[1]))
						return new double[] {x, y, z};
				}
				
				nextIter();
			}
		}
		
		private void reset(double[] orig, double[] dir) {
			this.orig = orig;
			this.dir = dir;
			x = orig[0];
			y = orig[1];
			z = orig[2];
			nextx = x;
			nexty = y;
			nextz = z;
			intx = (int) x;
			inty = (int) y;
			intz = (int) z;
			moved = 0;
			collideNum = 0;
			collide = new boolean[] {Math3D.isZero(dir[0]), Math3D.isZero(dir[1]), Math3D.isZero(dir[2])};
		}
		
		private void prefixComputeMove() {
			if (collide[0])
				movex = Math3D.sqrt3;
			else {
				if (dir[0] > 0)
					deltax = Math3D.notZero(1 + intx - x, 1);
				else
					deltax = Math3D.notZero(intx - x, -1);
				movex = deltax / dir[0];
			}
			
			if (collide[1])
				movey = Math3D.sqrt3;
			else {
				if (dir[1] > 0)
					deltay = Math3D.notZero(1 + inty - y, 1);
				else
					deltay = Math3D.notZero(inty - y, -1);
				movey = deltay / dir[1];
			}
			
			if (collide[2])
				movez = Math3D.sqrt3;
			else {
				if (dir[2] > 0)
					deltaz = Math3D.notZero(1 + intz - z, 1);
				else
					deltaz = Math3D.notZero(intz - z, -1);
				movez = deltaz / dir[2];
			}
			
			moveWhich = Math3D.minWhich(movex, movey, movez);
			move = moveWhich[0];
		}
		
		private void moveBy(double move) {
			nextx = x + dir[0] * move;
			nexty = y + dir[1] * move;
			nextz = z + dir[2] * move;
			
			intx = (int) nextx;
			inty = (int) nexty;
			intz = (int) nextz;
		}
		
		private boolean isOk() {
			return inBounds(intx, inty, intz) && isEmpty(intx, inty, intz);
		}
		
		private boolean collideCheck(int which) {
			if (!collide[which]) {
				collideNum++;
				collide[which] = true;
				dir[which] = 0;
				return collideNum == 3;
			}
			return false;
		}
		
		private void nextIter() {
			moved += move;
			x = nextx;
			y = nexty;
			z = nextz;
		}
	}
	
	public double[] findIntersection(double[] orig, double[] dir) {
		return intersectionFinder.find(orig, dir);
	}
	
	private boolean inBounds(int x, int y, int z) {
		return x >= 1 && y >= 1 && z >= 1 && x < part.length - 1 && y < part[x].length - 1 && z < part[x][y].length - 1;
		//		return x >= 0 && y >= 0 && z >= 0 && x < part.length && y < part[x].length && z < part[x][y].length;
	}
	
	private boolean isEmpty(int x, int y, int z) {
		return part[x][y][z] == null;
	}
	
	private int[] getBlock(int x, int y, int z) {
		int[] block = new int[6];
		if (x > 0 && part[x - 1][y][z] != null)
			block[Math3D.LEFT] = part[x - 1][y][z].block[Math3D.RIGHT];
		if (x < part.length - 1 && part[x + 1][y][z] != null)
			block[Math3D.RIGHT] = part[x + 1][y][z].block[Math3D.LEFT];
		if (y > 0 && part[x][y - 1][z] != null)
			block[Math3D.FRONT] = part[x][y - 1][z].block[Math3D.BACK];
		if (y < part[0].length - 1 && part[x][y + 1][z] != null)
			block[Math3D.BACK] = part[x][y + 1][z].block[Math3D.FRONT];
		if (z > 0 && part[x][y][z - 1] != null)
			block[Math3D.BOTTOM] = part[x][y][z - 1].block[Math3D.TOP];
		if (z < part[0][0].length - 1 && part[x][y][z + 1] != null)
			block[Math3D.TOP] = part[x][y][z + 1].block[Math3D.BOTTOM];
		return block;
	}
}
