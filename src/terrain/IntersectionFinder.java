package terrain;

import engine.Math3D;

class IntersectionFinder {
	private Terrain terrain;
	private double[] orig, dir;
	private double x, y, z;
	private double nextx, nexty, nextz;
	private int intx, inty, intz;
	private double deltax, deltay, deltaz;
	private double movex, movey, movez, move;
	private double[] moveWhich;
	private double moved, maxMove;
	private int collideNum, isDirZeroNum;
	boolean[] collide;
	private boolean[] isDirZero;
	private boolean inBounds;
	
	IntersectionFinder(Terrain terrain) {
		this.terrain = terrain;
	}
	
	double[] find(double[] orig, double[] dir, boolean allowSlide, boolean limitDistance, boolean allowCollideWithEdge) {
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
				if (collideCheck((int) moveWhich[1], allowSlide, allowCollideWithEdge))
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
		inBounds = isInBounds();
		boolean ok = inBounds && terrain.isEmpty(intx, inty, intz);
		if (limitDistance)
			return ok;
		return ok || (!inBounds && isComingInBounds());
	}
	
	private boolean collideCheck(int which, boolean allowSlide, boolean allowCollideWithEdge) {
		if (!allowCollideWithEdge && !inBounds)
			return collide[1] = true;
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
	
	private boolean isComingInBounds() {
		return (intx >= 1 || dir[0] > 0) && (inty >= 1 || dir[1] > 0) && (intz >= 1 || dir[2] > 0) && (intx < terrain.width - 1 || dir[0] < 0) && (inty < terrain.length - 1 || dir[1] < 0) && (intz < terrain.height - 1 || dir[2] < 0);
	}
	
	private boolean isInBounds() {
		return intx >= 1 && inty >= 1 && intz >= 1 && intx < terrain.width - 1 && inty < terrain.length - 1 && intz < terrain.height - 1;
	}
}
