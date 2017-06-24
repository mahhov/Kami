package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	private static final int MAP_WIDTH = 80, MAP_LENGTH = 80, MAP_HEIGHT = 10;
	
	private ScreenCell cell;
	private SelectButtonGroup triggerGroup, toolGroup, drawGroup;
	private ScreenButton drawButton, zoomOutButton, zoomInButton;
	private ScreenButton upButton, downButton, leftButton, rightButton;
	private ScreenButton saveButton, loadButton;
	private ScreenButton clearAllSelectionButton, clearSelectionButton;
	private ScreenToggleButton alphaButton, unselectModeButton;
	private ScreenTable vertMapTable;
	private ScreenEditorMap editorMap;
	
	public EditorScreen(double left, double top, double width, double height) {
		editorMap = new ScreenEditorMap(MAP_WIDTH, MAP_LENGTH, MAP_HEIGHT);
		
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
		for (int i = 0; i < Blueprint.MODULE_NAMES.length; i++)
			cell.addScreenItem(toolGroup.add(new ScreenSelectButton(Blueprint.MODULE_NAMES[i], (char) (i + 48))), i * 2, 1, 2, 1);
		toolGroup.setSelect(1);
		
		// map, vert, scroll, minimap
		cell.addScreenItem(vertMapTable = new ScreenTable(1, MAP_HEIGHT), 0, 3, 2, 16);
		cell.addScreenItem(upButton = new ScreenButton("UP", 'w'), 3, 2, 16, 1);
		cell.addScreenItem(downButton = new ScreenButton("DOWN", 's'), 3, 19, 16, 1);
		cell.addScreenItem(leftButton = new ScreenButton("L", 'a'), 2, 3, 1, 16);
		cell.addScreenItem(rightButton = new ScreenButton("R", 'd'), 19, 3, 1, 16);
		cell.addScreenItem(new ScreenImageContainer(editorMap), 14, 14, 4, 4);
		cell.addScreenItem(editorMap, 3, 3, 16, 16);
		
		// load, save
		cell.addScreenItem(loadButton = new ScreenButton("LOAD", 'l', true), 20, 3, 1, 3);
		cell.addScreenItem(saveButton = new ScreenButton("SAVE", 'p', true), 20, 6, 1, 3);
		
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
		cell.handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState(), controller.charInput, controller.getCharState());
		updateSelection();
		updateDrawing();
		updateScroll();
		updateStore();
	}
	
	private void updateSelection() {
		if (clearSelectionButton.press) {
			vertMapTable.clearCurrent();
			editorMap.clearCurrent();
		}
		if (clearAllSelectionButton.press) {
			vertMapTable.clearAll();
			editorMap.clearAll();
		}
		editorMap.setSelectShape(drawGroup.getSelect());
		
		vertMapTable.setSelectMode(!unselectModeButton.toggle);
		editorMap.setSelectMode(!unselectModeButton.toggle);
	}
	
	private void updateDrawing() {
		if (drawButton.press) {
			editorMap.updateMap(editorMap.getSelect(), vertMapTable.getSelect(), toolGroup.getSelect());
			editorMap.clearAll();
		}
		editorMap.updatePreviewMap(editorMap.getSelect(), vertMapTable.getSelect());
		
		editorMap.setAlpha(alphaButton.toggle);
	}
	
	private void updateScroll() {
		if (upButton.press)
			editorMap.scroll(0, -1, 0);
		if (downButton.press)
			editorMap.scroll(0, 1, 0);
		if (leftButton.press)
			editorMap.scroll(-1, 0, 0);
		if (rightButton.press)
			editorMap.scroll(1, 0, 0);
		if (zoomOutButton.press)
			editorMap.scroll(0, 0, +1);
		if (zoomInButton.press)
			editorMap.scroll(0, 0, -1);
	}
	
	private void updateStore() {
		if (saveButton.press)
			editorMap.storeSave();
		if (loadButton.press)
			editorMap.storeLoad();
	}
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
}

//todo 3d view