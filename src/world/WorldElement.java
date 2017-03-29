package world;

import control.Controller;
import terrain.Terrain;

public interface WorldElement {
	public void init(World world);
	
	public void update(World world, Terrain terrain, Controller controller);
}
