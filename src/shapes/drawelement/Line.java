package shapes.drawelement;

import java.awt.*;

public class Line extends DrawElement {
	public Color color;
	
	private Line(double[][] coord) {
		this.coord = coord;
	}
	
	public void setColor(Color c) {
		color = c;
	}
}