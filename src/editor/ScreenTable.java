package editor;

import control.Controller;
import engine.Math3D;
import paint.painterelement.PainterQueue;
import paint.painterelement.PainterRectangle;

import java.awt.*;

class ScreenTable extends ScreenItem {
	private int numColumns, numRows;
	private double columnWidth, rowHeight;
	private int highlightColumn, highlightRow;
	private boolean down, pressed;
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
	
	boolean handleMouseInput(double screenX, double screenY, int mouseState, char charInput, int charState) {
		if (!containsScreenCoord(screenX, screenY)) {
			highlightColumn = -1;
			return true;
		}
		
		double[] xy = screenToItemCoord(screenX, screenY);
		highlightColumn = (int) (xy[0] * numColumns);
		highlightRow = (int) (xy[1] * numRows);
		pressed = mouseState == Controller.PRESSED;
		down = pressed || mouseState == Controller.DOWN;
		if (!down)
			return false;
		
		updateSelect();
		return false;
	}
	
	private void updateSelect() {
		switch (selectShape) {
			case SELECT_PEN:
				select[highlightColumn][highlightRow] = selectMode;
				break;
			case SELECT_LINE:
				if (pressed)
					if (anchorColumn == -1) {
						anchorColumn = highlightColumn;
						anchorRow = highlightRow;
					} else {
						int[] dx = Math3D.magnitudeSign(highlightColumn - anchorColumn);
						int[] dy = Math3D.magnitudeSign(highlightRow - anchorRow);
						if (dx[0] > dy[0] * 3) {
							highlightColumn += dx[1];
							for (; anchorColumn != highlightColumn; anchorColumn += dx[1])
								select[anchorColumn][anchorRow] = selectMode;
						} else if (dy[0] > dx[0] * 3) {
							highlightRow += dy[1];
							for (; anchorRow != highlightRow; anchorRow += dy[1])
								select[anchorColumn][anchorRow] = selectMode;
						} else {
							highlightColumn += dx[1];
							highlightRow += dy[1];
							for (; anchorColumn != highlightColumn && anchorRow != highlightRow; anchorColumn += dx[1], anchorRow += dy[1])
								select[anchorColumn][anchorRow] = selectMode;
						}
						anchorColumn = -1;
					}
				break;
			case SELECT_RECTANGLE:
				if (pressed)
					if (anchorColumn == -1) {
						anchorColumn = highlightColumn;
						anchorRow = highlightRow;
					} else {
						int xd = anchorColumn < highlightColumn ? 1 : -1;
						int yd = anchorRow < highlightRow ? 1 : -1;
						highlightColumn += xd;
						highlightRow += yd;
						for (; anchorColumn != highlightColumn; anchorColumn += xd)
							for (int y = anchorRow; y != highlightRow; y += yd)
								select[anchorColumn][y] = selectMode;
						anchorColumn = -1;
					}
				break;
		}
	}
	
	void clearCurrent() {
		anchorColumn = -1;
	}
	
	void clearAll() {
		clearCurrent();
		for (int x = 0; x < numColumns; x++)
			for (int y = 0; y < numRows; y++)
				select[x][y] = false;
	}
	
	void setSelectShape(int value) {
		if (value == SELECT_PEN)
			clearCurrent();
		selectShape = value;
	}
	
	void setSelectMode(boolean value) {
		selectMode = value;
	}
	
	boolean[][] getSelect() {
		return select;
	}
	
	void draw(PainterQueue painterQueue) {
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.BLACK, false));
		for (int x = 0; x < numColumns; x++)
			for (int y = 0; y < numRows; y++)
				if (x == anchorColumn && y == anchorRow)
					drawRect(painterQueue, x, y, PRESS_COLOR, true);
				else if (x == highlightColumn && y == highlightRow)
					if (down || select[x][y])
						drawRect(painterQueue, x, y, PRESS_COLOR, true);
					else
						drawRect(painterQueue, x, y, HIGHLIGHT_COLOR, true);
				else if (select[x][y])
					drawRect(painterQueue, x, y, HIGHLIGHT_COLOR, true);
				else
					drawRect(painterQueue, x, y, TEXT_COLOR, false);
		
		painterQueue.add(new PainterRectangle(left, top, width, height, Color.BLACK, false));
	}
	
	private void drawRect(PainterQueue painterQueue, int x, int y, Color color, boolean fill) {
		painterQueue.add(new PainterRectangle(left + columnWidth * x, top + rowHeight * y, columnWidth, rowHeight, color, fill));
	}
}
