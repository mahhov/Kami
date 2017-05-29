package control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputControllerJava extends ControllerJavaListener {
	private char charPress;
	private boolean space, shift;
	private int mouseX, mouseY, mouseState;
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyChar() + "");
	}
	
	public void keyReleased(KeyEvent e) {
		//		setKeyState(e.getKeyCode(), RELEASED);
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		//		setMouseState(PRESSED);
	}
	
	public void mouseReleased(MouseEvent e) {
		//		setMouseState(RELEASED);
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
}	
