package control;

import engine.Math3D;

public class Controller {
	public static final int UP = 0, DOWN = 1, PRESSED = 2, RELEASED = 3;
	public static final int KEY_W = 0, KEY_A = 1, KEY_S = 2, KEY_D = 3, KEY_Q = 4, KEY_E = 5, KEY_R = 6, KEY_F = 7, KEY_Z = 8, KEY_X = 9;
	public static final int KEY_ESC = 10, KEY_SPACE = 11, KEY_SHIFT = 12, KEY_ENTER = 13, KEY_P = 14;
	public static final int KEY_1 = 15, KEY_2 = 16, KEY_3 = 17, KEY_4 = 18, KEY_5 = 19, KEY_6 = 20, KEY_7 = 21, KEY_8 = 22, KEY_9 = 23, KEY_0 = 24;
	public static final int KEY_I = 25, KEY_J = 26, KEY_K = 27, KEY_L = 28, KEY_U = 29, KEY_O = 30;
	public static final int KEY_SLASH = 31, KEY_BACK_SLASH = 32;
	public static final int KEY_M = 33;
	public static final int KEY_LEFT = 34, KEY_RIGHT = 35, KEY_DOWN = 36, KEY_UP = 37, KEY_LEFT_CAROT = 38, KEY_RIGHT_CAROT = 39;
	
	private Key[] keys;
	
	private int mouseMoveX, mouseMoveY;
	private int mouseState;
	
	public double[] viewOrig, viewDir;
	public Math3D.Angle viewAngle, viewAngleZ;
	
	Controller() {
		keys = new Key[40];
		keys[KEY_W] = new Key(87);
		keys[KEY_A] = new Key(65);
		keys[KEY_S] = new Key(83);
		keys[KEY_D] = new Key(68);
		keys[KEY_Q] = new Key(81);
		keys[KEY_E] = new Key(69);
		keys[KEY_R] = new Key(82);
		keys[KEY_F] = new Key(70);
		keys[KEY_Z] = new Key(90);
		keys[KEY_X] = new Key(88);
		keys[KEY_ESC] = new Key(27);
		keys[KEY_SPACE] = new Key(32);
		keys[KEY_SHIFT] = new Key(16);
		keys[KEY_ENTER] = new Key(10);
		keys[KEY_P] = new Key(80);
		keys[KEY_1] = new Key(49);
		keys[KEY_2] = new Key(50);
		keys[KEY_3] = new Key(51);
		keys[KEY_4] = new Key(52);
		keys[KEY_5] = new Key(53);
		keys[KEY_6] = new Key(54);
		keys[KEY_7] = new Key(55);
		keys[KEY_8] = new Key(56);
		keys[KEY_9] = new Key(57);
		keys[KEY_0] = new Key(48);
		keys[KEY_I] = new Key(73);
		keys[KEY_J] = new Key(74);
		keys[KEY_K] = new Key(75);
		keys[KEY_L] = new Key(76);
		keys[KEY_U] = new Key(85);
		keys[KEY_O] = new Key(79);
		keys[KEY_SLASH] = new Key(47);
		keys[KEY_BACK_SLASH] = new Key(92);
		keys[KEY_M] = new Key(77);
		keys[KEY_LEFT] = new Key(37);
		keys[KEY_RIGHT] = new Key(39);
		keys[KEY_DOWN] = new Key(40);
		keys[KEY_UP] = new Key(38);
		keys[KEY_LEFT_CAROT] = new Key(44);
		keys[KEY_RIGHT_CAROT] = new Key(46);
		
		viewOrig = new double[3];
		viewDir = new double[3];
	}
	
	boolean setKeyState(int keyCode, int state) {
		for (Key k : keys)
			if (k.code == keyCode && (state != PRESSED || k.state == UP)) {
				k.state = state;
				return true;
			}
		return false;
	}
	
	private int getKeyState(int key) {
		int r = keys[key].state;
		if (r == PRESSED)
			keys[key].state = DOWN;
		else if (r == RELEASED)
			keys[key].state = UP;
		return r;
	}
	
	public boolean isKeyDown(int key) {
		int state = getKeyState(key);
		return state == PRESSED || state == DOWN;
	}
	
	public boolean isKeyPressed(int key) {
		int state = getKeyState(key);
		return state == PRESSED;
	}
	
	void setMouseState(int state) {
		mouseState = state;
	}
	
	void setMouseMoved(int x, int y) {
		mouseMoveX += x;
		mouseMoveY += y;
	}
	
	public int[] getMouseMovement() {
		int[] r = new int[] {mouseMoveX, mouseMoveY};
		mouseMoveX = 0;
		mouseMoveY = 0;
		return r;
	}
	
	public int getMouseState() {
		int r = mouseState;
		if (r == PRESSED)
			mouseState = DOWN;
		else if (r == RELEASED)
			mouseState = UP;
		return r;
	}
	
	public boolean isMouseDown() {
		int state = getMouseState();
		return state == PRESSED || state == DOWN;
	}
	
	public boolean isMousePressed() {
		int state = getMouseState();
		return state == PRESSED;
	}
	
	private static class Key {
		int code, state;
		
		private Key(int code) {
			this.code = code;
		}
	}
	
	public void setView(Math3D.Angle angle, Math3D.Angle angleZ, double[] orig, double[] dir) {
		viewAngle = angle;
		viewAngleZ = angleZ;
		viewOrig = orig;
		viewDir = dir;
	}
}
