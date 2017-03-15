package ships;

import java.io.*;

public class Blueprint implements Serializable {
	private static final long serialVersionUID = "blueprint".hashCode();
	final static int MODULE_EMPTY_MODULE = 0, MODULE_HULL = 1, MODULE_ROTOR = 2, MODULE_HELIUM = 3, MODULE_FORW_BLADE = 4, MODULE_GUN = 5;
	final static String[] MODULE_NAMES = new String[] {"REMOVE", "HULL", "ROTOR", "HELIUM", "FORWARD BLADE", "GUN"};
	final static String[] DIRECTION_NAMES = new String[] {"LEFT", "RIGHT", "FRONT", "BACK", "BOTTOM", "TOP"};
	
	byte width, length, height;
	byte[][][][] blueprint;
	
	Blueprint() {
	}
	
	Blueprint(int width, int length, int height) {
		this.width = (byte) width;
		this.length = (byte) length;
		this.height = (byte) height;
		blueprint = new byte[width][length][height][2];
	}
	
	static Blueprint defaultBlueprint() {
		Blueprint bp = new Blueprint();
		bp.width = 21;
		bp.length = 21;
		bp.height = 21;
		bp.blueprint = new byte[bp.width][bp.length][bp.height][2];
		bp.blueprint[10][10][10][0] = MODULE_HULL;
		return bp;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
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
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		width = in.readByte();
		length = in.readByte();
		height = in.readByte();
		blueprint = new byte[width][length][height][2];
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