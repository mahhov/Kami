package paint.painter;

import control.Controller;
import control.ControllerLwjgl;
import engine.Math3D;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import static camera.Camera.MIN_LIGHT;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

//todo : clean up

public class PainterLwjgl implements Painter {
	private long window;
	public boolean running = true;
	ControllerLwjgl controller;
	private PainterQueue painterQueue;
	
	public PainterLwjgl(int frameSize, int imageSize, ControllerLwjgl controller) {
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
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		this.controller = controller;
		controller.window = window;
		glfwSetKeyCallback(window, controller.lwjglKeyboardHandler());
		glfwSetCursorPosCallback(window, controller.lwjgtlMousePosHandler());
		
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
	
	private float[] glTransformColor(double light, Color color) {
		if (light < MIN_LIGHT)
			return new float[3];
		else {
			light = Math3D.min(1, light);
			return new float[] {(float) (light * color.getRed() / 255), (float) (light * color.getGreen() / 255), (float) (light * color.getBlue() / 255)};
		}
	}
	
	private float[] glTransoformN5to5(float[] vertices) {
		for (int i = 0; i < vertices.length; i += 2) {
			vertices[i] = vertices[i] * 2;
			vertices[i + 1] = -vertices[i + 1] * 2;
		}
		return vertices;
	}
	
	private float[] glTransform01(float[] vertices) {
		for (int i = 0; i < vertices.length; i += 2) {
			vertices[i] = vertices[i] * 2 - 1;
			vertices[i + 1] = -vertices[i + 1] * 2 + 1;
		}
		return vertices;
	}
	
	LinkedList<Float> x;
	
	private void glDraw(int drawMode, float[] vertices, float[] color) {
		for (float f : vertices)
			y[ylen++] = f;
		
		//				glColor3fv(color);
		//				GL11.glBegin(GL11.GL_POLYGON);
		//				for (byte i = 0; i < vertices.length; i += 2)
		//					GL11.glVertex2d(vertices[i], vertices[i + 1]);
		//				GL11.glEnd();
		
		
		//				glColor3fv(color);
		//				FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		//				verticesBuffer.put(vertices).flip();
		//				int vboID = glGenBuffers();
		//				glEnableVertexAttribArray(0);
		//				glBindBuffer(GL_ARRAY_BUFFER, vboID);
		//				glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_DYNAMIC_DRAW);
		//				glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		//				glDrawArrays(drawMode, 0, vertices.length);
	}
	
	public void drawImage(BufferedImage image, int shift, int shiftVert) {
	}
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame) {
		if (xy != null)
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5 && xy[1][i] > -.5) {
					float[] vertices = glTransoformN5to5(new float[] {(float) xy[0][0], (float) xy[1][0], (float) xy[0][1], (float) xy[1][1], (float) xy[0][2], (float) xy[1][2], (float) xy[0][3], (float) xy[1][3]});
					glDraw(GL_QUADS, vertices, glTransformColor(light, color));
					return;
				}
	}
	
	public void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
		drawPolygon(xy, light, color, frame);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, double light, Color color) {
		
	}
	
	public void drawRectangle(double x, double y, double width, double height, Color color) {
		float[] vertices = glTransform01(new float[] {(float) x, (float) y, (float) (x + width), (float) y, (float) (x + width), (float) (y + height), (float) x, (float) (y + height)});
		glDraw(GL_QUADS, vertices, color.getRGBColorComponents(null));
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
	
	FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(1000 * 100 * 10);
	float[] y = new float[1000 * 100 * 10];
	int ylen;
	int vboID = -1;
	
	//todo: return to seperate thread
	public void run() {
		if (glfwWindowShouldClose(window)) {
			clean();
			running = false;
			return;
		}
		
		if (painterQueue.drawReady) {
			ylen = 0;
			
			glClear(GL_COLOR_BUFFER_BIT);
			painterQueue.paint(this);
			painterQueue.drawReady = false;
			
			verticesBuffer.clear();
			verticesBuffer.put(y).flip();
			
			if (vboID == -1) {
				vboID = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, vboID);
			}
			
			
			
			glEnableVertexAttribArray(0);
			glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_DYNAMIC_DRAW);
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
			glDrawArrays(GL_QUADS, 0, ylen / 2);
			
			glfwSwapBuffers(window);
		}
		
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}
}