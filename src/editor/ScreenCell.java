package editor;

import control.Controller;
import list.LList;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

public class ScreenCell extends ScreenItem {
	private LList<ScreenItem> item, itemTail;
	private double margin, columnWidthMargin, rowHeightMargin;
	private int numColumns, numRows;
	
	public ScreenCell(double margin, int numColumns, int numRows) {
		item = itemTail = new LList<>();
		this.margin = margin;
		this.numColumns = numColumns;
		this.numRows = numRows;
	}
	
	void setPosition(double left, double top, double width, double height) {
		super.setPosition(left, top, width, height);
		columnWidthMargin = (width - margin) / numColumns;
		rowHeightMargin = (height - margin) / numRows;
	}
	
	void addScreenItem(ScreenItem screenItem, int x, int y, int width, int height) {
		item = item.add(screenItem);
		screenItem.setPosition(left + margin + columnWidthMargin * x, top + margin + rowHeightMargin * y, width * columnWidthMargin - margin, height * rowHeightMargin - margin);
	}
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		for (LList<ScreenItem> i : itemTail.reverseIterator())
			if (!i.node.handleMouseInput(screenX, screenY, mouseState, charInput, charState))
				mouseState = Controller.UP;
		return true;
	}
	
	public void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.WHITE));
		for (LList<ScreenItem> i : item)
			i.node.draw(painterQueue);
	}
	
}
