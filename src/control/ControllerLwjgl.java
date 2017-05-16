package control;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class ControllerLwjgl extends Controller {
	
	public long window;
	private double lastXPos, lastYPos;
	
	public ControllerLwjgl(int width, int height) {
		super(0, 0);
	}
	
	public GLFWKeyCallback lwjglKeyboardHandler() {
		return new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (action == GLFW_PRESS)
					setKeyState(key, PRESSED);
				else if (action == GLFW_RELEASE)
					setKeyState(key, RELEASED);
			}
		};
	}
	
	public GLFWCursorPosCallback lwjgtlMousePosHandler() {
		return new GLFWCursorPosCallback() {
			public void invoke(long window, double xpos, double ypos) {
				setMouseMoved((int) (xpos - lastXPos), (int) (ypos - lastYPos));
				lastXPos = xpos;
				lastYPos = ypos;
			}
		};
	}
	
	public void update() {
		//		double[] x = new double[1], y = new double[1];
		//		 (window, x, y);
		//		glfwSetCursorPos(window, 0, 0);
		
		//		System.out.println(x[0] + " " + y[0]);
	}
	
	//	setMouseState(e.getButton(), PRESSED);
	
	//	setMouseState(e.getButton(), RELEASED);
	
	//	int x = e.getX(), y = e.getY();
	//	setMouseMoved(x, y);
	//   robot.mouseMove(centerMouseX - x + e.getXOnScreen(), centerMouseY - y + e.getYOnScreen());
	
}
