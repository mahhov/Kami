package paint.painter.TextureLwjgl;

import control.Controller;
import control.ControllerLwjgl;
import engine.Math3D;
import engine.Timer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import paint.painter.Painter;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static camera.Camera.MIN_LIGHT;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

//todo : clean up

public class PainterLwjgl implements Painter {
	private long window;
	public boolean running = true;
	private PainterQueue painterQueue;
	public int surfaceCount, drawCount;
	
	private final int bufferSize = 1000 * 100 * 10; // max recorded 151008 (6 times less)
	private float[] vertexArray = new float[bufferSize * 2];
	private float[] colorArray = new float[bufferSize * 3];
	private int vertexCount;
	private FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(bufferSize * 2);
	private FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(bufferSize * 3);
	
	private CharGlyphTextureGroup charGlyphTextureGroup;
	private BackgroundTextureGroup backgroundTextureGroup;
	
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
		
		controller.window = window;
		glfwSetKeyCallback(window, controller.lwjglKeyboardHandler());
		glfwSetCursorPosCallback(window, controller.lwjglMousePosHandler());
		glfwSetMouseButtonCallback(window, controller.lwjglMouseClickHandler());
		
		painterQueue = new PainterQueue();
		
		charGlyphTextureGroup = new CharGlyphTextureGroup();
	}
	
	private void clean() {
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
	
	private void glDrawQuad(float[] vertices, float[] color) {
		if (vertexCount > bufferSize - 4)
			return;
		
		for (int i = 0; i < 4; i++) {
			colorArray[(vertexCount) * 3] = color[0];
			colorArray[(vertexCount) * 3 + 1] = color[1];
			colorArray[(vertexCount) * 3 + 2] = color[2];
			
			vertexArray[(vertexCount) * 2] = vertices[i * 2];
			vertexArray[(vertexCount) * 2 + 1] = vertices[i * 2 + 1];
			
			vertexCount++;
		}
	}
	
	public void setBackgroundImage(BufferedImage image) {
		backgroundTextureGroup = new BackgroundTextureGroup(image);
	}
	
	public void drawBackgroundImage(int shift, int shiftVert) {
		backgroundTextureGroup.getTexture().draw(shift, shiftVert);
	}
	
	public void drawImage(BufferedImage image, double fromx, double fromy, double tox, double toy) {
	}
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame) {
		if (xy != null) {
			surfaceCount++;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5 && xy[1][i] > -.5) {
					drawCount++;
					float[] vertices = glTransoformN5to5(new float[] {(float) xy[0][0], (float) xy[1][0], (float) xy[0][1], (float) xy[1][1], (float) xy[0][2], (float) xy[1][2], (float) xy[0][3], (float) xy[1][3]});
					glDrawQuad(vertices, glTransformColor(light, color));
					return;
				}
		}
	}
	
	public void drawClipPolygon(double[][] xy, double light, Color color, int clipState, boolean frame) {
		drawPolygon(xy, light, color, frame);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, double light, Color color) {
		
	}
	
	public void drawRectangle(double x, double y, double width, double height, Color color) {
		float[] vertices = glTransform01(new float[] {(float) x, (float) y, (float) (x + width), (float) y, (float) (x + width), (float) (y + height), (float) x, (float) (y + height)});
		glDrawQuad(vertices, color.getRGBColorComponents(null));
	}
	
	public void drawRectangleFrame(double x, double y, double width, double height, Color color) {
	}
	
	public void drawBlur(double blur) {
		if (backgroundTextureGroup != null)
			backgroundTextureGroup.setTexture(blur);
	}
	
	public void drawText(double x, double y, Color color, String text) {
	}
	
	public void drawTextCentered(double x, double y, double width, double height, Color color, String text) {
	}
	
	public void drawTextVertical(double x, double y, double width, double height, Color color, String text) {
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
		while (running) {
			if (glfwWindowShouldClose(window)) {
				clean();
				running = false;
				return;
			}
			
			if (painterQueue.drawReady) {
				surfaceCount = drawCount = 0;
				
				vertexCount = 0;
				Timer.PAINTER_QUEUE_PAINT.timeStart();
				painterQueue.draw(this);
				Timer.PAINTER_QUEUE_PAINT.timeEnd();
				painterQueue.drawReady = false;
				
				Timer.PAINT.timeStart();
				vertexBuffer.clear();
				vertexBuffer.put(vertexArray).flip();
				colorBuffer.clear();
				colorBuffer.put(colorArray).flip();
				
				glEnableClientState(GL_VERTEX_ARRAY);
				glEnableClientState(GL_COLOR_ARRAY);
				glColorPointer(3, GL_FLOAT, 0, colorBuffer);
				glVertexPointer(2, GL_FLOAT, 0, vertexBuffer);
				glDrawArrays(GL_QUADS, 0, vertexCount);
				glDisableClientState(GL_VERTEX_ARRAY);
				glDisableClientState(GL_COLOR_ARRAY);
				
				DEBUG_STRING[5] = "surfaceCount: " + surfaceCount + " ; drawCount: " + drawCount;
				
				drawDebugStrings();
				
				glfwSwapBuffers(window);
				Timer.PAINT.timeEnd();
			}
			
			glfwPollEvents();
			Math3D.sleep(10);
		}
	}
	
	private void drawDebugStrings() {
		for (int y = 0; y < DEBUG_STRING.length; y++) {
			charGlyphTextureGroup.drawString(DEBUG_STRING[y], y);
		}
	}
}