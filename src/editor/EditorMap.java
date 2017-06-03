package editor;

import java.awt.*;
import java.awt.image.BufferedImage;

class EditorMap {
	private static final Color CLEAR_COLOR = new Color(0, 0, 0, 0), FILL_COLOR = new Color(100, 100, 200), OUTLINE_COLOR = Color.BLACK; //todo: constant fill color definition
	private int width, length, height;
	private int[][][] map;
	private int imageWidth, imageHeight;
	private BufferedImage image;
	private Graphics2D brush;
	
	private int blockWidth, blockHeight, blockXShift, blockYShift;
	
	EditorMap(int width, int length, int height, int imageWidth, int imageHeight) {
		this.width = width;
		this.length = length;
		this.height = height;
		map = new int[width][length][height];
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		brush = (Graphics2D) image.getGraphics();
		brush.setBackground(CLEAR_COLOR);
		
		blockWidth = imageWidth / width;
		blockHeight = imageHeight / length;
		System.out.println(blockWidth + " " + blockHeight);
		blockXShift = 3;
		blockYShift = 9;
	}
	
	void updateMap(boolean[][] select, boolean[][] vertSelect, int value) {
		for (int z = 0; z < height; z++)
			if (vertSelect[0][z])
				for (int x = 0; x < width; x++)
					for (int y = 0; y < length; y++)
						if (select[x][y])
							map[x][y][vertSelect[0].length - z - 1] = value;
	}
	
	BufferedImage createImage() {
		brush.clearRect(0, 0, imageWidth, imageHeight);
		
		for (int z = 0; z < height; z++)
			for (int x = 0; x < width; x++)
				for (int y = 0; y < length; y++)
					if (map[x][y][z] == 1) {
						int topZ = z + 1;
						
						// x
						int leftBottomX = x * blockWidth - z * blockXShift;
						int leftTopX = x * blockWidth - topZ * blockXShift;
						int rightBottomX = leftBottomX + blockWidth;
						int rightTopX = leftTopX + blockWidth;
						
						// y
						int backBottomY = y * blockHeight - z * blockYShift;
						int backTopY = y * blockHeight - topZ * blockYShift;
						int frontBottomY = backBottomY + blockHeight;
						int frontTopY = backTopY + blockHeight;
						
						// fill
						
						// right face
						if (isEmpty(x + 1, y, z)) {
							brush.setColor(new Color(80, 150, 200));
							brush.fillPolygon(new int[] {rightTopX, rightBottomX, rightBottomX, rightTopX}, new int[] {backTopY, backBottomY, frontBottomY, frontTopY}, 4);
						}
						
						// front face
						if (isEmpty(x, y + 1, z)) {
							brush.setColor(new Color(100, 170, 220));
							brush.fillPolygon(new int[] {leftTopX, rightTopX, rightBottomX, leftBottomX}, new int[] {frontTopY, frontTopY, frontBottomY, frontBottomY}, 4);
						}
						
						// top face
						if (isEmpty(x, y, z + 1)) {
							brush.setColor(new Color(120, 190, 240));
							brush.fillPolygon(new int[] {leftTopX, rightTopX, rightTopX, leftTopX}, new int[] {backTopY, backTopY, frontTopY, frontTopY}, 4);
						}
						
						// outline
						brush.setColor(OUTLINE_COLOR);
						
						// right face
						if (isEmpty(x + 1, y, z))
							brush.drawPolygon(new int[] {rightTopX, rightBottomX, rightBottomX, rightTopX}, new int[] {backTopY, backBottomY, frontBottomY, frontTopY}, 4);
						
						// front face
						if (isEmpty(x, y + 1, z))
							brush.drawPolygon(new int[] {leftTopX, rightTopX, rightBottomX, leftBottomX}, new int[] {frontTopY, frontTopY, frontBottomY, frontBottomY}, 4);
						
						// top face
						if (isEmpty(x, y, z + 1))
							brush.drawPolygon(new int[] {leftTopX, rightTopX, rightTopX, leftTopX}, new int[] {backTopY, backTopY, frontTopY, frontTopY}, 4);
					}
		
		return image;
	}
	
	private boolean isEmpty(int x, int y, int z) {
		return x < 0 || x >= width || y < 0 || y >= length || z < 0 || z >= height || map[x][y][z] == 0;
	}
}
