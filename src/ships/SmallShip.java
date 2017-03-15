package ships;

import engine.Math3D;
import world.World;

public class SmallShip extends Ship {
	public SmallShip(double x, double y, double z, double angle, double angleZ, double angleTilt, World world) {
		super(x, y, z, angle, angleZ, angleTilt, world);
	}
	
	void generateBlueprint() {
		// 5 hull
		// 4 blade
		// 3 hull
		// 2 rotor - back
		// 1 hull
		// 0 rotor - front
		
		blueprint = new Blueprint(2, 6, 1);
		blueprint.width = 2;
		blueprint.length = 6;
		for (int x = 0; x < blueprint.width; x++)
			for (int y = 0; y < blueprint.length; y++)
				for (int z = 0; z < blueprint.height; z++)
					if (y == 0) {
						blueprint.blueprint[x][y][z][0] = Blueprint.MODULE_ROTOR;
						blueprint.blueprint[x][y][z][1] = Math3D.FRONT;
					} else if (y == 2) {
						blueprint.blueprint[x][y][z][0] = Blueprint.MODULE_ROTOR;
						blueprint.blueprint[x][y][z][1] = Math3D.BACK;
					} else if (y == 4)
						blueprint.blueprint[x][y][z][0] = Blueprint.MODULE_FORW_BLADE;
					else
						blueprint.blueprint[x][y][z][0] = Blueprint.MODULE_HULL;
	}
}