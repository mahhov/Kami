package control;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputControllerJava extends ControllerJavaListener {
	private final int WIDTH, HEIGHT;
	public char charInput;
	private int charState;
	private boolean space, shift;
	public double mouseX, mouseY;
	
	public InputControllerJava(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void keyPressed(KeyEvent e) {
		charInput = e.getKeyChar();
		charState = PRESSED;
	}
	
	public void keyReleased(KeyEvent e) {
		if (charInput == e.getKeyChar())
			charState = RELEASED;
	}
	
	public int getCharState() {
		int r = charState;
		if (r == PRESSED)
			charState = DOWN;
		else if (r == RELEASED)
			charState = UP;
		return r;
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
		mouseX = 1. * e.getX() / WIDTH;
		mouseY = 1. * e.getY() / HEIGHT;
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseX = 1. * e.getX() / WIDTH;
		mouseY = 1. * e.getY() / HEIGHT;
	}
}	
