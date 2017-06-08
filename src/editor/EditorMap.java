package editor;

import engine.Math3D;
import paint.painterelement.PainterPolygon;
import paint.painterelement.PainterQueue;

import java.awt.*;

class EditorMap implements ImageProvider {
	private static final int RIGHT_FACE = 0, FRONT_FACE = 1, TOP_FACE = 2;
	private static final int[][][] FACE_COLOR = new int[][][] {
			{{60, 150, 200}, {80, 170, 220}, {100, 190, 240}}, // block==1
			{{200, 150, 60}, {220, 170, 80}, {240, 190, 100}}, // preview
	}; // [block][face][rgb]
	
	private int mapWidth, mapLength, mapHeight;
	private int[][][] map;
	private int preview[][][], previewCounter;
	private boolean alpha;
	
	private int scrollX, scrollY, zoom;
	private int mapHalfShowWidthDefault, mapHalfShowLengthDefault;
	private int mapHalfShowWidth, mapHalfShowLength;
	private final double blockXShift, blockYShift;
	private final double blockWidthDefault, blockHeightDefault;
	
	EditorMap(int mapWidth, int mapLength, int mapHeight) {
		mapHalfShowWidthDefault = 4;
		mapHalfShowLengthDefault = 4;
		this.mapWidth = mapWidth;
		this.mapLength = mapLength;
		this.mapHeight = mapHeight;
		map = new int[mapWidth][mapLength][mapHeight];
		preview = new int[mapWidth][mapLength][mapHeight];
		previewCounter = 4;
		
		blockWidthDefault = 1.0 / mapWidth;
		blockHeightDefault = 1.0 / mapLength;
		blockXShift = .005;
		blockYShift = .015;
		
		zoom = 1;
		scroll(0, 0, 0);
	}
	
	void updateMap(boolean[][] select, boolean[][] vertSelect, int value) {
		for (int z = 0; z < vertSelect[0].length; z++)
			if (vertSelect[0][z])
				for (int x = 0; x < select.length; x++)
					for (int y = 0; y < select[0].length; y++)
						if (select[x][y])
							map[scrollX - mapHalfShowWidth + x][scrollY - mapHalfShowLength + y][vertSelect[0].length - z - 1] = value;
	}
	
	void updatePreviewMap(boolean[][] select, boolean[][] vertSelect) {
		previewCounter++;
		for (int z = 0; z < vertSelect[0].length; z++)
			for (int x = 0; x < select.length; x++)
				for (int y = 0; y < select[0].length; y++)
					if (select[x][y] && vertSelect[0][z])
						preview[scrollX - mapHalfShowWidth + x][scrollY - mapHalfShowLength + y][vertSelect[0].length - z - 1] = previewCounter;
	}
	
	void setAlpha(boolean value) {
		alpha = value;
	}
	
	public void provideImage(PainterQueue painterQueue, double left, double top, double width, double height, boolean all) {
		if (all)
			provideImage(painterQueue, left, top, width, height, 0, 0, mapWidth, mapLength, blockWidthDefault, blockHeightDefault);
		else {
			double startX = Math3D.maxMin(scrollX - mapHalfShowWidth, mapWidth, 0);
			double startY = Math3D.maxMin(scrollY - mapHalfShowLength, mapLength, 0);
			double endX = Math3D.maxMin(scrollX + mapHalfShowWidth, mapWidth, 0);
			double endY = Math3D.maxMin(scrollY + mapHalfShowLength, mapLength, 0);
			double blockWidth = .5 / mapHalfShowWidth;
			double blockHeight = .5 / mapHalfShowLength;
			provideImage(painterQueue, left, top, width, height, startX, startY, endX, endY, blockWidth, blockHeight);
		}
	}
	
	private void provideImage(PainterQueue painterQueue, double left, double top, double width, double height, double startX, double startY, double endX, double endY, double blockWidth, double blockHeight) {
		boolean[][] shadow = new boolean[mapWidth][mapLength];
		for (int x = (int) startX; x < endX; x++)
			for (int y = (int) startY; y < endY; y++)
				if (map[x][y][0] == 0) {
					int z = 1;
					while (z < mapHeight && map[x][y][z] == 0)
						z++;
					if (z < mapHeight)
						shadow[x][y] = true;
				}
		
		int alphaAmount = alpha ? 40 : 255;
		
		for (int z = 0; z < mapHeight; z++)
			for (int x = (int) startX; x < endX; x++)
				for (int y = (int) startY; y < endY; y++)
					if (map[x][y][z] == 1 || preview[x][y][z] == previewCounter) {
						int topZ = z + 1;
						
						// x
						double offX = x - startX;
						double leftBottomX = (offX * blockWidth - z * blockXShift) * width - .5 + left;
						double leftTopX = (offX * blockWidth - topZ * blockXShift) * width - .5 + left;
						double rightBottomX = leftBottomX + blockWidth * width;
						double rightTopX = leftTopX + blockWidth * width;
						
						// y
						double offY = y - startY;
						double backBottomY = (offY * blockHeight - z * blockYShift) * height - .5 + top;
						double backTopY = (offY * blockHeight - topZ * blockYShift) * height - .5 + top;
						double frontBottomY = backBottomY + blockHeight * height;
						double frontTopY = backTopY + blockHeight * height;
						
						double[][] rightxy = null, frontxy = null, topxy = null;
						int block = map[x][y][z] == 1 ? 0 : 1;
						
						// fill
						
						// right face
						if (isEmpty(x + 1, y, z) || alpha) {
							rightxy = new double[][] {{rightTopX, rightBottomX, rightBottomX, rightTopX}, {backTopY, backBottomY, frontBottomY, frontTopY}};
							painterQueue.add(new PainterPolygon(rightxy, 1, createColor(block, RIGHT_FACE, alphaAmount), false));
						}
						
						// front face
						if (isEmpty(x, y + 1, z) || alpha) {
							frontxy = new double[][] {{leftTopX, rightTopX, rightBottomX, leftBottomX}, {frontTopY, frontTopY, frontBottomY, frontBottomY}};
							painterQueue.add(new PainterPolygon(frontxy, 1, createColor(block, FRONT_FACE, alphaAmount), false));
						}
						
						// top face
						if (isEmpty(x, y, z + 1) || alpha) {
							topxy = new double[][] {{leftTopX, rightTopX, rightTopX, leftTopX}, {backTopY, backTopY, frontTopY, frontTopY}};
							painterQueue.add(new PainterPolygon(topxy, 1, createColor(block, TOP_FACE, alphaAmount), false));
						}
						
						// outline
						
						// right face
						if (rightxy != null)
							painterQueue.add(new PainterPolygon(rightxy, 1, null, true));
						
						// front face
						if (frontxy != null)
							painterQueue.add(new PainterPolygon(frontxy, 1, null, true));
						
						// top face
						if (topxy != null)
							painterQueue.add(new PainterPolygon(topxy, 1, null, true));
						
					} else if (z == 0 && shadow[x][y]) {
						// shadow 
						double leftBottomX = (x * blockWidth - z * blockXShift) * width - .5 + left;
						double rightBottomX = leftBottomX + blockWidth * width;
						double backBottomY = (y * blockHeight - z * blockYShift) * height - .5 + top;
						double frontBottomY = backBottomY + blockHeight * height;
						
						double[][] bottomxy = new double[][] {{leftBottomX, rightBottomX, rightBottomX, leftBottomX}, {backBottomY, backBottomY, frontBottomY, frontBottomY}};
						painterQueue.add(new PainterPolygon(bottomxy, 1, new Color(100, 100, 100, alphaAmount), false));
					}
	}
	
	private Color createColor(int block, int face, int alpha) {
		return new Color(FACE_COLOR[block][face][0], FACE_COLOR[block][face][1], FACE_COLOR[block][face][2], alpha);
	}
	
	private boolean isEmpty(int x, int y, int z) {
		return x < 0 || x >= mapWidth || y < 0 || y >= mapLength || z < 0 || z >= mapHeight || map[x][y][z] == 0;
	}
	
	void scroll(int dx, int dy, int dz) {
		zoom = Math3D.maxMin(zoom + dz, 10, 1);
		mapHalfShowWidth = mapHalfShowWidthDefault * zoom;
		mapHalfShowLength = mapHalfShowLengthDefault * zoom;
		scrollX = Math3D.maxMin(scrollX + dx * zoom, mapWidth - mapHalfShowWidth, mapHalfShowWidth);
		scrollY = Math3D.maxMin(scrollY + dy * zoom, mapLength - mapHalfShowLength, mapHalfShowLength);
	}
}
