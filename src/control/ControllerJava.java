package control;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ControllerJava extends ControllerJavaListener {
	private Robot robot;
	final int centerMouseX, centerMouseY;
	
	public ControllerJava(int width, int height) {
		super();
		
		centerMouseX = width / 2;
		centerMouseY = height / 2;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
		if (!setKeyState(e.getKeyCode(), PRESSED))
			System.out.println("key press: " + e.getKeyCode());
	}
	
	public void keyReleased(KeyEvent e) {
		setKeyState(e.getKeyCode(), RELEASED);
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		setMouseState(PRESSED);
	}
	
	public void mouseReleased(MouseEvent e) {
		setMouseState(RELEASED);
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX() - centerMouseX, y = e.getY() - centerMouseY;
		setMouseMoved(x, y);
		robot.mouseMove(e.getXOnScreen() - x, e.getYOnScreen() - y);
	}
}	
