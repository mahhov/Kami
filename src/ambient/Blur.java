package ambient;

import character.Character;
import control.Controller;
import paint.PainterBlur;
import paint.PainterQueue;
import terrain.Terrain;
import world.World;
import world.WorldElement;
import world.interfaceelement.InterfaceElement;

public class Blur implements WorldElement, InterfaceElement {
	private Character character;
	private double blur;
	
	public Blur(Character character) {
		this.character = character;
	}
	
	public void init(World world) {
		world.addBackgroundElement(this);
	}
	
	public void update(World world, Terrain terrain, Controller controller) {
		blur = 1 - character.getVsq() / 3 * .8;
	}
	
	public void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterBlur(blur));
	}
}
