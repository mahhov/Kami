package ships;

import control.Controller;
import engine.Math3D;
import engine.Painter;
import shapes.CubeFrame;
import shapes.Shape;
import shapes.StaticCube;
import world.World;

import java.awt.*;
import java.io.Serializable;

import static ships.Blueprint.*;

public class ModelShip extends Ship implements Serializable {
	private final static String SAVE_PATH = "modelShipScratch.ot";
	private String saveStatus;
	
	private int[] selected, nextSelected;
	private int moduleSelected;
	private int directionSelected;
	
	private final static int WORLD_EDGE = 5;
	public int fullWidth, fullLength, fullHeight;
	
	public ModelShip(World world) {
		super(WORLD_EDGE, WORLD_EDGE, WORLD_EDGE, 0, 0, 0, world);
		fullWidth = width + WORLD_EDGE * 2;
		fullLength = length + WORLD_EDGE * 2;
		fullHeight = height + WORLD_EDGE * 2;
		addToWorld(world);
		initWorld(world);
		saveStatus = "";
		directionSelected = Math3D.FRONT;
	}
	
	void generateBlueprint() {
		blueprint = Blueprint.defaultBlueprint();
	}
	
	void initWorld(World world) {
		for (int xi = 0; xi < fullWidth; xi++)
			for (int yi = 0; yi < fullLength; yi++)
				for (int zi = 0; zi < 1; zi++)
					if (xi < WORLD_EDGE || xi > width + WORLD_EDGE || yi < WORLD_EDGE || yi > length + WORLD_EDGE)
						world.addShape(xi, yi, zi, new StaticCube(xi + .5, yi + .5, zi + .5, Color.GRAY, null));
					else
						world.addShape(xi, yi, zi, new StaticCube(xi + .5, yi + .5, zi + .5, Color.LIGHT_GRAY, null));
	}
	
	void addToWorld(World world) {
		// selected frame
		if (selected != null)
			world.addShape((int) x + selected[1], (int) y + selected[0], (int) z + selected[2], new CubeFrame(x + selected[1] + .5, y + selected[0] + .5, z + selected[2] + .5, new Math3D.Angle(0), new Math3D.Angle(0), new Math3D.Angle(0), .5, this));
		
		// ship
		Shape shape;
		double xc, yc, zc;
		for (int xi = 0; xi < width; xi++)
			for (int yi = 0; yi < length; yi++)
				for (int zi = 0; zi < height; zi++) {
					xc = x + yi + 0.5;
					yc = y + xi + 0.5;
					zc = z + zi + 0.5;
					
					int[] block = new int[] {0, 0, 0, 0, 0, 0};//getBlock(xi, yi, zi);
					
					shape = part[xi][yi][zi].getShape(xc, yc, zc, angle, angleZ, angleTilt, rightUp, block, this);
					if (shape != null)
						world.addShape((int) xc, (int) yc, (int) zc, shape);
				}
		
	}
	
	public void update(World world, Controller controller) {
		drawCounter++;
		selectCube(controller);
		selectModule(controller);
		selectDirection(controller);
		modify(controller);
		saveLoad(controller);
		addToWorld(world);
		textOutput();
	}
	
	private void selectCube(Controller controller) {
		double x = controller.viewOrig[1] - this.x, y = controller.viewOrig[0] - this.y, z = controller.viewOrig[2] - this.z;
		int curx = (int) x, cury = (int) y, curz = (int) z;
		int nextx = curx, nexty = cury, nextz = curz;
		double deltax, deltay, deltaz;
		double movex, movey, movez, move;
		double moved = 0, maxMoved = 75;
		
		while (moved < maxMoved && inBounds(nextx, nexty, nextz) && isEmpty(nextx, nexty, nextz)) {
			if (controller.viewDir[1] > 0)
				deltax = Math3D.notZero(1 + nextx - x, 1);
			else
				deltax = Math3D.notZero(nextx - x, -1);
			if (controller.viewDir[0] > 0)
				deltay = Math3D.notZero(1 + nexty - y, 1);
			else
				deltay = Math3D.notZero(nexty - y, -1);
			if (controller.viewDir[2] > 0)
				deltaz = Math3D.notZero(1 + nextz - z, 1);
			else
				deltaz = Math3D.notZero(nextz - z, -1);
			
			if (Math3D.isZero(controller.viewDir[1]))
				movex = Math3D.sqrt3;
			else
				movex = deltax / controller.viewDir[1];
			if (Math3D.isZero(controller.viewDir[0]))
				movey = Math3D.sqrt3;
			else
				movey = deltay / controller.viewDir[0];
			if (Math3D.isZero(controller.viewDir[2]))
				movez = Math3D.sqrt3;
			else
				movez = deltaz / controller.viewDir[2];
			
			move = Math3D.min(movex, movey, movez) + Math3D.EPSILON;
			moved += move;
			
			x += controller.viewDir[1] * move;
			y += controller.viewDir[0] * move;
			z += controller.viewDir[2] * move;
			
			curx = nextx;
			cury = nexty;
			curz = nextz;
			
			nextx = (int) x;
			nexty = (int) y;
			nextz = (int) z;
			if (x < 0)
				nextx--;
			if (y < 0)
				nexty--;
			if (z < 0)
				nextz--;
		}
		
		if (moved > maxMoved || !inBounds(curx, cury, curz))
			selected = null;
		else {
			selected = new int[] {curx, cury, curz};
			if (!inBounds(nextx, nexty, nextz))
				nextSelected = null;
			else
				nextSelected = new int[] {nextx, nexty, nextz};
		}
	}
	
	private void selectModule(Controller controller) {
		if (controller.isKeyPressed(Controller.KEY_0))
			moduleSelected = 0;
		else if (controller.isKeyPressed(Controller.KEY_1))
			moduleSelected = 1;
		else if (controller.isKeyPressed(Controller.KEY_2))
			moduleSelected = 2;
		else if (controller.isKeyPressed(Controller.KEY_3))
			moduleSelected = 3;
		else if (controller.isKeyPressed(Controller.KEY_4))
			moduleSelected = 4;
		else if (controller.isKeyPressed(Controller.KEY_5))
			moduleSelected = 5;
	}
	
	private void selectDirection(Controller controller) {
		if (controller.isKeyPressed(Controller.KEY_I))
			directionSelected = Math3D.FRONT;
		else if (controller.isKeyPressed(Controller.KEY_J))
			directionSelected = Math3D.LEFT;
		else if (controller.isKeyPressed(Controller.KEY_K))
			directionSelected = Math3D.BACK;
		else if (controller.isKeyPressed(Controller.KEY_L))
			directionSelected = Math3D.RIGHT;
		else if (controller.isKeyPressed(Controller.KEY_U))
			directionSelected = Math3D.BOTTOM;
		else if (controller.isKeyPressed(Controller.KEY_O))
			directionSelected = Math3D.TOP;
	}
	
	private void modify(Controller controller) {
		if (!controller.isMousePressed())
			return;
		
		int[] xyz;
		if (moduleSelected == MODULE_EMPTY_MODULE)
			xyz = nextSelected;
		else
			xyz = selected;
		
		if (xyz != null) {
			blueprint.blueprint[xyz[0]][xyz[1]][xyz[2]][0] = (byte) moduleSelected;
			blueprint.blueprint[xyz[0]][xyz[1]][xyz[2]][1] = (byte) directionSelected;
			generatePart(xyz[0], xyz[1], xyz[2]);
			part[xyz[0]][xyz[1]][xyz[2]].set(directionSelected, new double[3]);
			saveStatus = "unsaved changes - press '/' to save, press '\' to load";
		}
		
	}
	
	private void saveLoad(Controller controller) {
		if (controller.isKeyPressed(Controller.KEY_SLASH)) // save
			if (blueprint.save(SAVE_PATH))
				saveStatus = "saved model ship to " + SAVE_PATH;
			else
				saveStatus = "error saving model ship to " + SAVE_PATH;
		
		else if (controller.isKeyPressed(Controller.KEY_BACK_SLASH)) { // load
			Blueprint blueprint = Blueprint.load(SAVE_PATH);
			if (blueprint != null) {
				this.blueprint = blueprint;
				generateParts();
				setParts();
				saveStatus = "loaded model ship from " + SAVE_PATH;
			} else
				saveStatus = "error loading model ship from " + SAVE_PATH;
		}
	}
	
	private void textOutput() {
		Painter.outputString[0] = "seleced module: " + MODULE_NAMES[moduleSelected];
		Painter.outputString[1] = "seleced direction: " + DIRECTION_NAMES[directionSelected];
		Painter.outputString[2] = saveStatus;
	}
	
	private boolean inBounds(int x, int y, int z) {
		return x >= 0 && y >= 0 && z >= 0 && x < width && y < length && z < height;
	}
	
	private boolean isEmpty(int x, int y, int z) {
		return part[x][y][z].mass == 0;
	}
	
}

// todo: load sandbox ship from file