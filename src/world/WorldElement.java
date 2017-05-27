package world;

import control.Controller;
import terrain.Terrain;

public interface WorldElement {
	void init(World world);
	
	void update(World world, Terrain terrain, Controller controller);
}
