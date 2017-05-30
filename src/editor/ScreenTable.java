package editor;

import control.Controller;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

class ScreenTable extends ScreenItem {
	private int numColumns, numRows;
	private double columnWidth, rowHeight;
	private int highlightColumn, highlightRow;
	private boolean down;
	private int anchorColumn, anchorRow; // for select line & rect
	private boolean[][] select;
	static final int SELECT_PEN = 0, SELECT_LINE = 1, SELECT_RECTANGLE = 2;
	private int selectShape; // pen, line, rect
	private boolean selectMode; // select / unselect
	
	ScreenTable(int numColumns, int numRows) {
		this.numColumns = numColumns;
		this.numRows = numRows;
		select = new boolean[numColumns][numRows];
		anchorColumn = -1;
		selectMode = true;
	}
	
	void setPosition(double left, double top, double width, double height) {
		super.setPosition(left, top, width, height);
		columnWidth = width / numColumns;
		rowHeight = height / numRows;
	}
	
	public boolean handleMouseInput(double screenX, double screenY, int mouseState) {
		if (!containsScreenCoord(screenX, screenY)) {
			highlightColumn = -1;
			return true;
		}
		
		double[] xy = screenToItemCoord(screenX, screenY);
		highlightColumn = (int) (xy[0] * numColumns);
		highlightRow = (int) (xy[1] * numRows);
		down = mouseState == Controller.DOWN;
		if (!down)
			return false;
		
		// todo : seperate helper function
		switch (selectShape) {
			case SELECT_PEN:
				select[highlightColumn][highlightRow] = selectMode;
				break;
			case SELECT_LINE:
				if (anchorColumn == -1) {
					anchorColumn = highlightColumn;
					anchorRow = highlightRow;
				} else {
					// do this
					anchorColumn = -1;
				}
				break;
			case SELECT_RECTANGLE:
				if (anchorColumn == -1) {
					anchorColumn = highlightColumn;
					anchorRow = highlightRow;
				} else {
					int xd = anchorColumn < highlightColumn ? 1 : -1;
					int yd = anchorRow < highlightRow ? 1 : -1;
					for (int x = anchorColumn; x < highlightColumn; x += xd)
						for (int y = anchorRow; y < highlightRow; y += yd)
							select[x][y] = selectMode;
					anchorColumn = -1;
				}
				break;
		}
		return false;
	}
	
	void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.BLACK, false));
		for (int x = 0; x < numColumns; x++)
			for (int y = 0; y < numRows; y++)
				if (x == highlightColumn && y == highlightRow)
					if (down || select[x][y])
						drawRect(painterQueue, x, y, PRESS_COLOR, true);
					else
						drawRect(painterQueue, x, y, HIGHLIGHT_COLOR, true);
				else if (select[x][y])
					drawRect(painterQueue, x, y, HIGHLIGHT_COLOR, true);
				else
					drawRect(painterQueue, x, y, TEXT_COLOR, false);
	}
	
	private void drawRect(PainterQueue painterQueue, int x, int y, Color color, boolean fill) {
		painterQueue.add(new PainterRectangle(left + columnWidth * x, top + rowHeight * y, columnWidth, rowHeight, color, fill));
	}
}
