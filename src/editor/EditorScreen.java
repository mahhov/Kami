package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	private static final int MAP_WIDTH = 80, MAP_LENGTH = 80, MAP_HEIGHT = 10;
	
	// todo : reorganize
	private ScreenCell cell;
	private ScreenButton clearAllSelectionButton, clearSelectionButton, drawButton, zoomOutButton, zoomInButton;
	private ScreenToggleButton unselectModeButton, alphaButton;
	private ScreenTable vertMapTable;
	private SelectButtonGroup toolGroup, drawGroup, triggerGroup;
	private ScreenEditorMap screenEditorMap;
	private ScreenButton upButton, downButton, leftButton, rightButton;
	private ScreenButton saveButton, loadButton;
	
	public EditorScreen(double left, double top, double width, double height) {
		screenEditorMap = new ScreenEditorMap(MAP_WIDTH, MAP_LENGTH, MAP_HEIGHT);
		
		cell = new ScreenCell(.02, 21, 21);
		cell.setPosition(left, top, width, height);
		
		// trigger 
		triggerGroup = new SelectButtonGroup();
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 1")), 0, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 2")), 1, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 3")), 2, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 4")), 3, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 5")), 4, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 6")), 5, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 7")), 6, 0, 1, 1);
		cell.addScreenItem(triggerGroup.add(new ScreenSelectButton("T 8")), 7, 0, 1, 1);
		
		// draw, alpha, zoom
		cell.addScreenItem(drawButton = new ScreenButton("DRAW", ' '), 9, 0, 2, 1);
		cell.addScreenItem(alphaButton = new ScreenToggleButton("ALPHA", 't'), 11, 0, 2, 1);
		cell.addScreenItem(zoomOutButton = new ScreenButton("ZOOM OUT", '-'), 13, 0, 3, 1);
		cell.addScreenItem(zoomInButton = new ScreenButton("ZOOM IN", '='), 16, 0, 3, 1);
		
		// block type
		toolGroup = new SelectButtonGroup();
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("EMPTY", '0')), 0, 1, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("BLOCK", '1')), 2, 1, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("START", '2')), 4, 1, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("END", '3')), 6, 1, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("TRIGGER", '4')), 10, 1, 3, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("T FROM", '5')), 13, 1, 3, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("T TO", '6')), 16, 1, 3, 1);
		toolGroup.setSelect(1);
		
		// map, vert, scroll, minimap
		cell.addScreenItem(vertMapTable = new ScreenTable(1, MAP_HEIGHT), 0, 3, 2, 16);
		cell.addScreenItem(upButton = new ScreenButton("UP", 'w'), 3, 2, 16, 1);
		cell.addScreenItem(downButton = new ScreenButton("DOWN", 's'), 3, 19, 16, 1);
		cell.addScreenItem(leftButton = new ScreenButton("L", 'a'), 2, 3, 1, 16);
		cell.addScreenItem(rightButton = new ScreenButton("R", 'd'), 19, 3, 1, 16);
		cell.addScreenItem(new ScreenImageContainer(screenEditorMap), 14, 14, 4, 4);
		cell.addScreenItem(screenEditorMap, 3, 3, 16, 16);
		
		// load, save
		cell.addScreenItem(loadButton = new ScreenButton("LOAD", 'L', true), 20, 3, 1, 3);
		cell.addScreenItem(saveButton = new ScreenButton("SAVE", 'S', true), 20, 6, 1, 3);
		
		// selection
		cell.addScreenItem(clearAllSelectionButton = new ScreenButton("CLEAR ALL SELECTION", 'c'), 0, 20, 4, 1);
		cell.addScreenItem(clearSelectionButton = new ScreenButton("CLEAR SELECTION", 'z'), 4, 20, 4, 1);
		cell.addScreenItem(unselectModeButton = new ScreenToggleButton("UNSELECT MODE", 'x'), 8, 20, 4, 1);
		
		// draw mode
		drawGroup = new SelectButtonGroup();
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("PEN", 'b')), 14, 20, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("LINE", 'n')), 16, 20, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("RECT", 'm')), 18, 20, 2, 1);
	}
	
	public void update(InputControllerJava controller) {
		if (clearSelectionButton.press) {
			vertMapTable.clearCurrent();
			screenEditorMap.clearCurrent();
		}
		if (clearAllSelectionButton.press) {
			vertMapTable.clearAll();
			screenEditorMap.clearAll();
		}
		screenEditorMap.setSelectShape(drawGroup.getSelect());
		
		vertMapTable.setSelectMode(!unselectModeButton.toggle);
		screenEditorMap.setSelectMode(!unselectModeButton.toggle);
		
		cell.handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState(), controller.charInput, controller.getCharState());
		
		if (drawButton.press) {
			screenEditorMap.updateMap(screenEditorMap.getSelect(), vertMapTable.getSelect(), toolGroup.getSelect());
			screenEditorMap.clearAll();
		}
		screenEditorMap.updatePreviewMap(screenEditorMap.getSelect(), vertMapTable.getSelect());
		
		screenEditorMap.setAlpha(alphaButton.toggle);
		
		if (upButton.press)
			screenEditorMap.scroll(0, -1, 0);
		if (downButton.press)
			screenEditorMap.scroll(0, 1, 0);
		if (leftButton.press)
			screenEditorMap.scroll(-1, 0, 0);
		if (rightButton.press)
			screenEditorMap.scroll(1, 0, 0);
		if (zoomOutButton.press)
			screenEditorMap.scroll(0, 0, +1);
		if (zoomInButton.press)
			screenEditorMap.scroll(0, 0, -1);
	}
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
}

//todo 3d view