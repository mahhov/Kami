package ships;

import world.World;

public class FileShip extends Ship {
	private final static String SAVE_PATH = "modelShipScratch.ot";
	
	public FileShip(double x, double y, double z, double angle, double angleZ, double angleTilt, World world) {
		super(x, y, z, angle, angleZ, angleTilt, world);
	}
	
	void generateBlueprint() {
		blueprint = Blueprint.load(SAVE_PATH);
		if (blueprint == null) {
			blueprint = Blueprint.defaultBlueprint();
		}
	}
}