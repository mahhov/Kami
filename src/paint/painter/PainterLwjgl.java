package paint.painter;

import control.Controller;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PainterLwjgl implements Painter {
	// The window handle
	private long window;
	public boolean running = true;
	private PainterQueue painterQueue;
	
	public PainterLwjgl(int frameSize, int imageSize, Controller controller) {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		// Create the window
		window = glfwCreateWindow(frameSize, frameSize, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		//		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
		//			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
		//				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		//		});
		
		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			
			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			
			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		
		painterQueue = new PainterQueue();
	}
	
	public void clean() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void drawImage(BufferedImage image, int shift, int shiftVert) {
	}
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame) {
		
	}
	
	public void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
	
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, double light, Color color) {
		
	}
	
	public void drawRectangle(double x, double y, double width, double height, Color color) {
		
	}
	
	public void drawBlur(double blur) {
		
	}
	
	public void updateMode(Controller controller) {
		
	}
	
	public boolean isPainterQueueDone() {
		return !painterQueue.drawReady;
	}
	
	public void setPainterQueue(PainterQueue painterQueue) {
		this.painterQueue = painterQueue;
	}
	
	public void run() {
		if (glfwWindowShouldClose(window)) {
			clean();
			running = false;
			return;
		}
		
		if (painterQueue.drawReady) {
			painterQueue.paint(this);
			painterQueue.drawReady = false;
			//			paint();
		}
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		glfwSwapBuffers(window); // swap the color buffers
		
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}
}