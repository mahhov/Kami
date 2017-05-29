package control;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class ControllerLwjgl extends Controller {
	
	public long window;
	private double lastXPos, lastYPos;
	
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
	
	public GLFWCursorPosCallback lwjglMousePosHandler() {
		return new GLFWCursorPosCallback() {
			public void invoke(long window, double xpos, double ypos) {
				setMouseMoved((int) (xpos - lastXPos), (int) (ypos - lastYPos));
				lastXPos = xpos;
				lastYPos = ypos;
			}
		};
	}
	
	public GLFWMouseButtonCallback lwjglMouseClickHandler() {
		return new GLFWMouseButtonCallback() {
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW_PRESS)
					setMouseState(PRESSED);
				else if (action == GLFW_RELEASE)
					setMouseState(RELEASED);
			}
		};
	}
}
