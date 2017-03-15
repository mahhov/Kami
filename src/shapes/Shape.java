package shapes;


public class Shape {
	ShapeParent shapeParent;
	private long drawCounter;
	
	Shape(ShapeParent shapeParent) {
		this.shapeParent = shapeParent;
		if (shapeParent != null)
			drawCounter = shapeParent.getDrawCounter();
	}
	
	Surface[] getSurfaces(int xSide, int ySide, int zSide) {
		return new Surface[0];
	}
	
	// return: empty Surface[] -> nothing to draw. null -> ready to be removed from the world
	final public Surface[] draw(int xSide, int ySide, int zSide) {
		if (shapeParent != null && drawCounter != shapeParent.getDrawCounter())
			return null;
		return getSurfaces(xSide, ySide, zSide);
	}
}
