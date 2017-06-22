package editor;

import java.io.*;

public class Blueprint implements Serializable {
	static final String DEFAULT_PATH = "blueprint.txt";
	private static final long serialVersionUID = "blueprint".hashCode();
	final static int EMPTY = 0, BLOCK = 1, START = 2, END = 3, TRIGGER = 4, TRIGGER_FROM = 5, TRIGGER_TO = 6;
	final static String[] MODULE_NAMES = new String[] {"EMPTY", "BLOCK", "START", "END", "TRIGGER", "T FROM", "T TO"};
	
	int width, length, height;
	int[][][][] blueprint;
	
	Blueprint() {
	}
	
	Blueprint(int width, int length, int height) {
		this.width = width;
		this.length = length;
		this.height = height;
		blueprint = new int[width][length][height][3];
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeByte(width);
		out.writeByte(length);
		out.writeByte(height);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				for (int z = 0; z < height; z++) {
					out.writeByte(blueprint[x][y][z][0]);
					out.writeByte(blueprint[x][y][z][1]);
				}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		width = in.readByte();
		length = in.readByte();
		height = in.readByte();
		blueprint = new int[width][length][height][2];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < length; y++)
				for (int z = 0; z < height; z++) {
					blueprint[x][y][z][0] = in.readByte();
					blueprint[x][y][z][1] = in.readByte();
				}
	}
	
	boolean save(String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	static Blueprint load(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Blueprint blueprint = (Blueprint) objectIn.readObject();
			objectIn.close();
			return blueprint;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}