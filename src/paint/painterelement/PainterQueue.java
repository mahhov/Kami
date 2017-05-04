package paint.painterelement;

import list.LList;
import paint.painter.Painter;

public class PainterQueue extends PainterElement {
	private LList<PainterElement> elements;
	private LList<PainterElement> tailElement;
	public boolean drawReady;
	
	public PainterQueue() {
		tailElement = elements = new LList<>();
	}
	
	public void add(PainterElement e) {
		elements = elements.add(e);
	}
	
	public void paint(Painter painter) {
		for (LList<PainterElement> e : tailElement.reverseIterator())
			e.node.paint(painter);
	}
}