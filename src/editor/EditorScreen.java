package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	private static final int MAP_WIDTH = 16, MAP_LENGTH = 16, MAP_HEIGHT = 10;
	
	private ScreenCell cell;
	private ScreenButton clearAllSelectionButton, clearSelectionButton, drawButton, zoomOutButton, zoomInButton;
	private ScreenToggleButton unselectModeButton, alphaButton;
	private ScreenTable vertMapTable, mainMapTable;
	private SelectButtonGroup toolGroup, drawGroup;
	private EditorMap editorMap;
	
	public EditorScreen(double left, double top, double width, double height) {
		editorMap = new EditorMap(MAP_WIDTH, MAP_LENGTH, MAP_HEIGHT);
		
		cell = new ScreenCell(.02, 20, 20);
		cell.setPosition(left, top, width, height);
		
		toolGroup = new SelectButtonGroup();
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("EMPTY", '0')), 0, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("BLOCK", '1')), 2, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("START", '2')), 4, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("END", '3')), 6, 0, 2, 1);
		toolGroup.setSelect(1);
		
		cell.addScreenItem(drawButton = new ScreenButton("DRAW", ' '), 10, 0, 2, 1);
		cell.addScreenItem(alphaButton = new ScreenToggleButton("ALPHA", 'a'), 12, 0, 2, 1);
		cell.addScreenItem(zoomOutButton = new ScreenButton("ZOOM OUT", '-'), 14, 0, 2, 1);
		cell.addScreenItem(zoomInButton = new ScreenButton("ZOOM IN", '+'), 16, 0, 2, 1);
		
		cell.addScreenItem(vertMapTable = new ScreenTable(1, MAP_HEIGHT), 0, 2, 2, 16);
		cell.addScreenItem(new ScreenButton("UP", 'w'), 3, 1, 16, 1);
		cell.addScreenItem(new ScreenButton("DOWN", 's'), 3, 18, 16, 1);
		cell.addScreenItem(new ScreenButton("L", 'a'), 2, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("R", 'd'), 19, 2, 1, 16);
		cell.addScreenItem(new ScreenImageContainer(editorMap, true), 14, 13, 4, 4);
		cell.addScreenItem(new ScreenImageContainer(editorMap, false), 3, 2, 16, 16);
		cell.addScreenItem(mainMapTable = new ScreenTable(MAP_WIDTH, MAP_LENGTH), 3, 2, 16, 16);
		
		cell.addScreenItem(clearAllSelectionButton = new ScreenButton("CLEAR ALL SELECTION", 'c'), 0, 19, 4, 1);
		cell.addScreenItem(clearSelectionButton = new ScreenButton("CLEAR SELECTION", 'z'), 4, 19, 4, 1);
		cell.addScreenItem(unselectModeButton = new ScreenToggleButton("UNSELECT MODE", 'x'), 8, 19, 4, 1);
		
		drawGroup = new SelectButtonGroup();
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("FREE PEN", 'b')), 14, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("LINE", 'n')), 16, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("RECT", 'm')), 18, 19, 2, 1);
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
		
		cell.handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState(), controller.charInput, controller.getCharState());
		
		if (drawButton.press) {
			editorMap.updateMap(mainMapTable.getSelect(), vertMapTable.getSelect(), toolGroup.getSelect());
			mainMapTable.clearAll();
		}
		editorMap.updatePreviewMap(mainMapTable.getSelect(), vertMapTable.getSelect());
		
		editorMap.setAlpha(alphaButton.toggle);
	}
	
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
}

//todo scroll
//todo 3d view
//todo keybarod shortcuts