package editor;

import control.InputControllerJava;
import paint.painterelement.PainterQueue;

public class EditorScreen {
	ScreenCell cell;
	
	public EditorScreen(double left, double top, double width, double height) {
		cell = new ScreenCell(.02, 20, 20);
		cell.setPosition(left, top, width, height);
		
		SelectButtonGroup toolGroup = new SelectButtonGroup();
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("EMPTY")), 0, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("BLOCK")), 2, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("START")), 4, 0, 2, 1);
		cell.addScreenItem(toolGroup.add(new ScreenSelectButton("END")), 6, 0, 2, 1);
		
		cell.addScreenItem(new ScreenButton("VERT MAP"), 0, 2, 2, 16);
		cell.addScreenItem(new ScreenButton("UP"), 3, 1, 16, 1);
		cell.addScreenItem(new ScreenButton("DOWN"), 3, 18, 16, 1);
		cell.addScreenItem(new ScreenButton("L"), 2, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("R"), 19, 2, 1, 16);
		cell.addScreenItem(new ScreenButton("MINI MAP"), 14, 13, 4, 4);
		cell.addScreenItem(new ScreenButton("MAIN MAP"), 3, 2, 16, 16);
		
		cell.addScreenItem(new ScreenButton("CLEAR ALL SELECTION"), 0, 19, 4, 1);
		cell.addScreenItem(new ScreenButton("CLEAR SELECTION"), 4, 19, 4, 1);
		cell.addScreenItem(new ScreenToggleButton("UNSELECT MODE"), 8, 19, 4, 1);
		
		SelectButtonGroup drawGroup = new SelectButtonGroup();
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("FREE PEN")), 14, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("LINE")), 16, 19, 2, 1);
		cell.addScreenItem(drawGroup.add(new ScreenSelectButton("RECT")), 18, 19, 2, 1);
	}
	
	public void handleMouseInput(InputControllerJava controller) {
		cell.handleMouseInput(controller.mouseX, controller.mouseY, controller.getMouseState());
	}
	
	public void draw(PainterQueue painterQueue) {
		cell.draw(painterQueue);
	}
	
}
