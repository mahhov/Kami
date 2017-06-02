package editor;

import java.awt.*;
import java.awt.image.BufferedImage;

class EditorMap {
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
		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		brush = (Graphics2D) image.getGraphics();
		
		blockWidth = imageWidth / width;
		blockHeight = imageHeight / length;
		System.out.println(blockWidth + " " + blockHeight);
		blockXShift = blockYShift = 10;
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
		brush.setColor(Color.WHITE);
		brush.fillRect(0, 0, imageWidth, imageHeight);
		
		brush.setColor(Color.BLUE);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				for (int z = 0; z < height; z++)
					if (map[x][y][z] == 1)
						brush.drawRect(x * blockWidth - z * blockXShift, y * blockHeight - z * blockYShift, blockWidth, blockHeight);
		
		return image;
	}
}
