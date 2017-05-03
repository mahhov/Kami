package paint;

import list.LList;

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
	
	void paint(Painter painter) {
		for (LList<PainterElement> e : tailElement.reverseIterator())
			e.node.paint(painter);
	}
}