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
		int size = 200;
		intersectionFinder = new IntersectionFinder();
		part = new TerrainModule[size][size][100];
		for (int x = 0; x < part.length; x++)
			for (int y = 0; y < part[x].length; y++) {
				part[x][y][0] = new FullGray();
				part[x][y][1] = new FullGray();
				part[x][y][2] = new FullGray();
				if (Math.random() > .999)
					generateTree(x, y, 2);
			}
	}
	
	private void generateTree(int x, int y, int floor) {
		int thick = 5;
		x = Math3D.maxMin(x, part.length - thick * 2, thick);
		y = Math3D.maxMin(y, part[x].length - thick * 2, thick);
		int height = Math3D.rand(7, 14) + 10;
		int brushSpread = Math3D.rand(1, 3) + height / 5 - 2;
		int brushHeight = Math3D.rand(1, 3);
		for (int xi = x - thick; xi <= x + thick; xi++)
			for (int yi = y - thick; yi <= y + thick; yi++)
				for (int z = floor; z < height + floor; z++)
					part[xi][yi][z] = new FullGray();
		//		for (int z = 0; z < height; z++)
		//			part[x][y][z] = new FullGray();
		int xs = Math3D.max(x - brushSpread, 0);
		int xe = Math3D.min(x + brushSpread, part.length - 1);
		int ys = Math3D.max(y - brushSpread, 0);
		int ye = Math3D.min(y + brushSpread, part[x].length - 1);
		for (int xi = xs; xi <= xe; xi++)
			for (int yi = ys; yi <= ye; yi++)
				for (int z = height + floor; z < height + brushHeight + floor; z++)
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
	
	private class IntersectionFinder {
		private double[] orig, dir;
		private double x, y, z;
		private double nextx, nexty, nextz;
		private int intx, inty, intz;
		private double deltax, deltay, deltaz;
		private double movex, movey, movez, move;
		private double[] moveWhich;
		private double moved, maxMove;
		private int collideNum, isDirZeroNum;
		private boolean[] collide;
		private boolean[] isDirZero;
		
		private double[] find(double[] orig, double[] dir, boolean allowSlide, boolean limitDistance) {
			reset(orig, dir);
			while (true) {
				prefixComputeMove();
				if (moved + move > maxMove && limitDistance) {
					moveBy(maxMove - moved);
					return new double[] {nextx, nexty, nextz};
				}
				moveBy(move + Math3D.EPSILON);
				if (!isOk(limitDistance)) {
					moveBy(move - Math3D.EPSILON);
					if (collideCheck((int) moveWhich[1], allowSlide))
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
			maxMove = Math3D.magnitude(dir);
			Math3D.scale(dir, 1 / maxMove);
			collideNum = 0;
			isDirZeroNum = 0;
			collide = new boolean[] {false, false, false};
			isDirZero = new boolean[] {Math3D.isZero(dir[0]), Math3D.isZero(dir[1]), Math3D.isZero(dir[2])};
			if (isDirZero[0]) {
				dir[0] = 0;
				isDirZeroNum++;
			}
			if (isDirZero[1]) {
				dir[1] = 0;
				isDirZeroNum++;
			}
			if (isDirZero[2]) {
				dir[2] = 0;
				isDirZeroNum++;
			}
		}
		
		private void prefixComputeMove() {
			if (isDirZero[0])
				movex = Math3D.sqrt3;
			else {
				if (dir[0] > 0) {
					deltax = 1 + intx - x;
					movex = Math3D.notZero(deltax / dir[0], deltax / dir[0]);
				} else {
					deltax = intx - x;
					movex = Math3D.notZero(deltax / dir[0], deltax / dir[0]);
				}
			}
			
			if (isDirZero[1])
				movey = Math3D.sqrt3;
			else {
				if (dir[1] > 0) {
					deltay = 1 + inty - y;
					movey = Math3D.notZero(deltay / dir[1], deltay / dir[1]);
				} else {
					deltay = inty - y;
					movey = Math3D.notZero(deltay / dir[1], deltay / dir[1]);
				}
			}
			
			if (isDirZero[2])
				movez = Math3D.sqrt3;
			else {
				if (dir[2] > 0) {
					deltaz = 1 + intz - z;
					movez = Math3D.notZero(deltaz / dir[2], deltaz / dir[2]);
				} else {
					deltaz = intz - z;
					movez = Math3D.notZero(deltaz / dir[2], deltaz / dir[2]);
				}
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
		
		private boolean isOk(boolean limitDistance) {
			boolean inBounds = inBounds(intx, inty, intz);
			boolean ok = inBounds && isEmpty(intx, inty, intz);
			if (limitDistance)
				return ok;
			return ok || (!inBounds && comingInBounds());
		}
		
		private boolean comingInBounds() {
			return (intx >= 1 || dir[0] > 0) && (inty >= 1 || dir[1] > 0) && (intz >= 1 || dir[2] > 0) && (intx < part.length - 1 || dir[0] < 0) && (inty < part[0].length - 1 || dir[1] < 0) && (intz < part[0][0].length - 1 || dir[2] < 0);
		}
		
		private boolean collideCheck(int which, boolean allowSlide) {
			if (!allowSlide)
				return collide[0] = true;
			if (!collide[which]) {
				collideNum++;
				collide[which] = true;
				if (!isDirZero[which]) {
					isDirZeroNum++;
					isDirZero[which] = true;
				}
				dir[which] = 0;
				return collideNum == 3 || isDirZeroNum == 3;
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
	
	public double[] findIntersection(double[] orig, double[] dir, boolean allowSlide, boolean limitDistance) {
		return intersectionFinder.find(orig, dir, allowSlide, limitDistance);
	}
	
	public boolean[] getIntersectionCollide() {
		return intersectionFinder.collide;
	}
	
	private boolean inBounds(int x, int y, int z) {
		return x >= 1 && y >= 1 && z >= 1 && x < part.length - 1 && y < part[x].length - 1 && z < part[x][y].length - 1;
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
