package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	private static final int MAP_WIDTH = 16, MAP_LENGTH = 16, MAP_HEIGHT = 10;
	
	private ScreenCell cell;
	private ScreenButton clearAllSelectionButton, clearSelectionButton, drawButton;
	private ScreenToggleButton unselectModeButton;
	private ScreenTable vertMapTable, mainMapTable;
	private SelectButtonGroup toolGroup, drawGroup;
	private EditorMap editorMap;
	
	public EditorScreen(double left, double top, double width, double height) {
		editorMap = new EditorMap(MAP_WIDTH, MAP_LENGTH, MAP_HEIGHT);
		
		cell = new ScreenCell(.02, 20, 20);
		cell.setPosition(left, top, width, height);
		
		toolGroup = new SelectButtonGroup();
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("EMPTY")), 0, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("BLOCK")), 2, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("START")), 4, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("END")), 6, 0, 2, 1);
		toolGroup.setSelect(1);
		
		cell.addScreenItem(drawButton = new ScreenButton("DRAW"), 10, 0, 2, 1);
		
		cell.addScreenItem(vertMapTable = new ScreenTable(1, MAP_HEIGHT, null), 0, 2, 2, 16);
		cell.addScreenItem(new ScreenButton("UP"), 3, 1, 16, 1);
		cell.addScreenItem(new ScreenButton("DOWN"), 3, 18, 16, 1);
		cell.addScreenItem(new ScreenButton("L"), 2, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("R"), 19, 2, 1, 16);
		cell.addScreenItem(new ScreenImageContainer(editorMap), 14, 13, 4, 4);
		cell.addScreenItem(mainMapTable = new ScreenTable(MAP_WIDTH, MAP_LENGTH, editorMap), 3, 2, 16, 16);
		
		cell.addScreenItem(clearAllSelectionButton = new ScreenButton("CLEAR ALL SELECTION"), 0, 19, 4, 1);
		cell.addScreenItem(clearSelectionButton = new ScreenButton("CLEAR SELECTION"), 4, 19, 4, 1);
		cell.addScreenItem(unselectModeButton = new ScreenToggleButton("UNSELECT MODE"), 8, 19, 4, 1);
		
		drawGroup = new SelectButtonGroup();
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("FREE PEN")), 14, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("LINE")), 16, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("RECT")), 18, 19, 2, 1);
	}
	
	public void update(InputControllerJava controller) {
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
		
		if (drawButton.press) {
			editorMap.updateMap(mainMapTable.getSelect(), vertMapTable.getSelect(), toolGroup.getSelect());
			vertMapTable.clearAll();
			mainMapTable.clearAll();
		}
	}
	
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
}