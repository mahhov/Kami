package control;

import java.awt.*;
import java.awt.event.*;

public class ControllerJava extends Controller implements KeyListener, MouseListener, MouseMotionListener {
	private Robot robot;
	
	public ControllerJava(int width, int height) {
		super(width, height);
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
		if (setKeyState(e.getKeyCode(), PRESSED) == -1)
			System.out.println("key press: " + e.getKeyCode());
	}
	
	public void keyReleased(KeyEvent e) {
		setKeyState(e.getKeyCode(), RELEASED);
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		setMouseState(e.getButton(), PRESSED);
	}
	
	public void mouseReleased(MouseEvent e) {
		setMouseState(e.getButton(), RELEASED);
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = e.getX(), y = e.getY();
		setMouseMoved(x, y);
		robot.mouseMove(centerMouseX - x + e.getXOnScreen(), centerMouseY - y + e.getYOnScreen());
	}
}	
