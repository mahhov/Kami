package editor;

import paint.painterelement.PainterPolygon;
import paint.painterelement.PainterQueue;

import java.awt.*;

class EditorMap implements ImageProvider {
	private int mapWidth, mapLength, mapHeight;
	private int[][][] map;
	private int scrollX, scrollY;
	
	private double blockWidth, blockHeight, blockXShift, blockYShift;
	
	EditorMap(int mapWidth, int mapLength, int mapHeight) {
		this.mapWidth = mapWidth;
		this.mapLength = mapLength;
		this.mapHeight = mapHeight;
		map = new int[mapWidth][mapLength][mapHeight];
		
		blockWidth = 1.0 / mapWidth;
		blockHeight = 1.0 / mapLength;
		blockXShift = .005;
		blockYShift = .015;
	}
	
	void updateMap(boolean[][] select, boolean[][] vertSelect, int value) {
		for (int z = 0; z < mapHeight; z++)
			if (vertSelect[0][z])
				for (int x = 0; x < mapWidth; x++)
					for (int y = 0; y < mapLength; y++)
						if (select[x][y])
							map[x][y][vertSelect[0].length - z - 1] = value;
	}
	
	public void provideImage(PainterQueue painterQueue, double left, double top, double width, double height, boolean all) {
		for (int z = 0; z < mapHeight; z++)
			for (int x = 0; x < mapWidth; x++)
				for (int y = 0; y < mapLength; y++)
					if (map[x][y][z] == 1) {
						int topZ = z + 1;
						
						// x
						double leftBottomX = (x * blockWidth - z * blockXShift) * width - .5 + left;
						double leftTopX = (x * blockWidth - topZ * blockXShift) * width - .5 + left;
						double rightBottomX = leftBottomX + blockWidth * width;
						double rightTopX = leftTopX + blockWidth * width;
						
						// y
						double backBottomY = (y * blockHeight - z * blockYShift) * height - .5 + top;
						double backTopY = (y * blockHeight - topZ * blockYShift) * height - .5 + top;
						double frontBottomY = backBottomY + blockHeight * height;
						double frontTopY = backTopY + blockHeight * height;
						
						double[][] rightxy = null, frontxy = null, topxy = null;
						
						// fill
						
						// right face
						if (isEmpty(x + 1, y, z)) {
							rightxy = new double[][] {{rightTopX, rightBottomX, rightBottomX, rightTopX}, {backTopY, backBottomY, frontBottomY, frontTopY}};
							painterQueue.add(new PainterPolygon(rightxy, 1, new Color(80, 150, 200), false));
						}
						
						// front face
						if (isEmpty(x, y + 1, z)) {
							frontxy = new double[][] {{leftTopX, rightTopX, rightBottomX, leftBottomX}, {frontTopY, frontTopY, frontBottomY, frontBottomY}};
							painterQueue.add(new PainterPolygon(frontxy, 1, new Color(100, 170, 220), false));
						}
						
						// top face
						if (isEmpty(x, y, z + 1)) {
							topxy = new double[][] {{leftTopX, rightTopX, rightTopX, leftTopX}, {backTopY, backTopY, frontTopY, frontTopY}};
							painterQueue.add(new PainterPolygon(topxy, 1, new Color(120, 190, 240), false));
						}
						
						// // outline
						
						// right face
						if (rightxy != null)
							painterQueue.add(new PainterPolygon(rightxy, 1, null, true));
						
						// front face
						if (frontxy != null)
							painterQueue.add(new PainterPolygon(frontxy, 1, null, true));
						
						// top face
						if (topxy != null)
							painterQueue.add(new PainterPolygon(topxy, 1, null, true));
					}
	}
	
	private boolean isEmpty(int x, int y, int z) {
		return x < 0 || x >= mapWidth || y < 0 || y >= mapLength || z < 0 || z >= mapHeight || map[x][y][z] == 0;
	}
}
