package paint.painter;

import control.Controller;
import control.ControllerLwjgl;
import engine.Math3D;
import engine.Timer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import paint.painterelement.PainterQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
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
	public int surfaceCount;
	
	private final int bufferSize = 1000 * 100 * 10; // max recorded 151008 (6 times less)
	private float[] vertexArray = new float[bufferSize * 2];
	private float[] colorArray = new float[bufferSize * 3];
	private int vertexCount;
	private FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(bufferSize * 2);
	private FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(bufferSize * 3);
	
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
		
		drawStringInit();
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
	
	private BackgroundTexture backgroundTexture;
	
	public void setBackgroundImage(BufferedImage image) {
		backgroundTexture = new BackgroundTexture(image);
	}
	
	public void drawBackgroundImage(int shift, int shiftVert) {
		
		backgroundTexture.draw(shift, shiftVert);
	}
	
	public void drawImage(BufferedImage image, int shift, int shiftVert) {
	}
	
	public void drawPolygon(double[][] xy, double light, Color color, boolean frame) {
		if (xy != null) {
			surfaceCount++;
			for (int i = 0; i < xy[0].length; i++)
				if (xy[0][i] > -.5 && xy[0][i] < .5 && xy[1][i] < .5 && xy[1][i] > -.5) {
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
		while (running) {
			if (glfwWindowShouldClose(window)) {
				clean();
				running = false;
				return;
			}
			
			if (painterQueue.drawReady) {
				surfaceCount = 0;
				
				vertexCount = 0;
				// glClear(GL_COLOR_BUFFER_BIT);
				Timer.PAINTER_QUEUE_PAINT.timeStart();
				painterQueue.paint(this);
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
				
				drawDebugStrings();
				
				glfwSwapBuffers(window);
				Timer.PAINT.timeEnd();
			}
			
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			Math3D.sleep(10);
		}
	}
	
	private abstract static class Texture {
		int width, height;
		int textureId;
		
		Texture(BufferedImage image) {
			this(image, (byte) -1);
		}
		
		Texture(BufferedImage image, byte alpha) {
			width = image.getWidth();
			height = image.getHeight();
			ByteBuffer byteBuffer = createByteBuffer(image, alpha);
			createTexture(byteBuffer);
		}
		
		private ByteBuffer createByteBuffer(BufferedImage image, byte alpha) {
			// get rgb
			int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
			
			// create byte buffer
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * 4);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pixel = pixels[y * width + x];
					byteBuffer.put((byte) ((pixel >> 16) & 0xFF));
					byteBuffer.put((byte) ((pixel >> 8) & 0xFF));
					byteBuffer.put((byte) (pixel & 0xFF));
					byteBuffer.put(alpha == -1 ? (byte) ((pixel >> 24) & 0xFF) : alpha);
				}
			}
			byteBuffer.flip();
			
			return byteBuffer;
		}
		
		private void createTexture(ByteBuffer byteBuffer) {
			textureId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		}
		
		void draw(float x, float y) {
			prepDraw();
			coordDraw(x, y);
			endDraw();
		}
		
		private void prepDraw() {
			glBindTexture(GL_TEXTURE_2D, textureId);
			glEnable(GL_TEXTURE_2D);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
		
		abstract void coordDraw(float x, float y);
		
		private void endDraw() {
			glDrawArrays(GL_QUADS, 0, 4);
			glDisable(GL_TEXTURE_2D);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_VERTEX_ARRAY);
			glDisable(GL_BLEND);
		}
	}
	
	private static class BackgroundTexture extends Texture {
		BackgroundTexture(BufferedImage image) {
			super(image, (byte) 100);
		}
		
		void coordDraw(float x, float y) {
			float[] textureCoord = new float[] {x / width, y / height, (x + 800) / width, y / height, (x + 800) / width, (y + 800) / height, x / width, (y + 800) / height};
			float[] quad = new float[] {-1, 1, 1, 1, 1, -1, -1, -1};
			
			glTexCoordPointer(2, GL_FLOAT, 0, textureCoord);
			glVertexPointer(2, GL_FLOAT, 0, quad);
		}
	}
	
	private static class CharGlyph extends Texture {
		private static final float[] TEXTURE_COORD = new float[] {0, 0, 1, 0, 1, 1, 0, 1};
		private static final int SIZE = 12;
		
		CharGlyph(String s) {
			super(createImage(s));
		}
		
		private static BufferedImage createImage(String s) {
			BufferedImage textImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) textImage.getGraphics();
			g.setColor(Color.WHITE);
			g.drawString(s, 1, 10);
			return textImage;
		}
		
		void coordDraw(float x, float y) {
			float size = .03f;
			float leftX = -1 + x * size;
			float topY = 1 - y * size;
			float rightX = leftX + size;
			float bottomY = topY - size;
			float[] quad = new float[] {leftX, topY, rightX, topY, rightX, bottomY, leftX, bottomY};
			
			glTexCoordPointer(2, GL_FLOAT, 0, TEXTURE_COORD);
			glVertexPointer(2, GL_FLOAT, 0, quad);
		}
	}
	
	private CharGlyph charGlyphs[] = new CharGlyph[127];
	
	private void drawStringInit() {
		for (int i = 32; i < charGlyphs.length; i++)
			charGlyphs[i] = new CharGlyph((char) i + "");
	}
	
	private void drawDebugStrings() {
		for (int y = 0; y < DEBUG_STRING.length; y++) {
			char[] c = DEBUG_STRING[y].toCharArray();
			for (int x = 0; x < c.length; x++)
				if (c[x] >= 0 && c[x] < charGlyphs.length)
					charGlyphs[c[x]].draw(x, y);
		}
	}
}