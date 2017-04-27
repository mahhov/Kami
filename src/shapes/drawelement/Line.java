package shapes.drawelement;

import java.awt.*;

public class Line extends DrawElement {
	public Color color;
	
	public Line(double[][] coord) {
		this.coord = coord;
		color = Color.BLACK;
	}
	
	public void setColor(Color c) {
		color = c;
	}
}