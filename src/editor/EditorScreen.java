package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	private ScreenCell cell;
	private ScreenButton clearAllSelectionButton, clearSelectionButton;
	private ScreenToggleButton unselectModeButton;
	private ScreenTable vertMapTable, mainMapTable;
	private SelectButtonGroup drawGroup;
	
	public EditorScreen(double left, double top, double width, double height) {
		cell = new ScreenCell(.02, 20, 20);
		cell.setPosition(left, top, width, height);
		
		SelectButtonGroup toolGroup = new SelectButtonGroup();
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("EMPTY")), 0, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("BLOCK")), 2, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("START")), 4, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("END")), 6, 0, 2, 1);
		
		cell.addScreenItem(vertMapTable = new ScreenTable(1, 10), 0, 2, 2, 16);
		cell.addScreenItem(new ScreenButton("UP"), 3, 1, 16, 1);
		cell.addScreenItem(new ScreenButton("DOWN"), 3, 18, 16, 1);
		cell.addScreenItem(new ScreenButton("L"), 2, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("R"), 19, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("MINI MAP"), 14, 13, 4, 4);
		cell.addScreenItem(mainMapTable = new ScreenTable(16, 16), 3, 2, 16, 16);
		
		cell.addScreenItem(clearAllSelectionButton = new ScreenButton("CLEAR ALL SELECTION"), 0, 19, 4, 1);
		cell.addScreenItem(clearSelectionButton = new ScreenButton("CLEAR SELECTION"), 4, 19, 4, 1);
		cell.addScreenItem(unselectModeButton = new ScreenToggleButton("UNSELECT MODE"), 8, 19, 4, 1);
		
		drawGroup = new SelectButtonGroup();
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("FREE PEN")), 14, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("LINE")), 16, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("RECT")), 18, 19, 2, 1);
	}
	
	public void update(InputControllerJava controller) {
		// todo: update modes, hanle inputs from screen items, etc
		if (clearSelectionButton.press) {
			vertMapTable.clearCurrent();
			mainMapTable.clearCurrent();
		}
		if (clearAllSelectionButton.press) {
			vertMapTable.clearAll();
			mainMapTable.clearAll();
		}
		mainMapTable.setSelectShape(drawGroup.getSelect());
		
		vertMapTable.setSelectMode(!unselectModeButton.toggle);
		mainMapTable.setSelectMode(!unselectModeButton.toggle);
		
		cell.handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState());
	}
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
	
}
