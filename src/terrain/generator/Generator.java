package terrain.generator;

import terrain.Terrain;

public abstract class Generator {
	Terrain terrain;
	
	Generator(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public abstract int getwidth();
	
	public abstract int getlength();
	
	public abstract int getheight();
	
	public abstract void generate(int cx, int cy, int cz);
}
