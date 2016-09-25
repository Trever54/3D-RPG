package renderEngine;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.system.MemoryUtil.NULL;
import input.Mouse;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;

public class DisplayManager {

	public static int WIDTH;
	public static int HEIGHT;
	
	//create error Callback
	public static GLFWErrorCallback errorCallback;
	public static GLFWScrollCallback scrollCallback;
	
	public static long window;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay(){
		// set error callback
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		
		// initialize GLFW
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// configure the window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		
		// Get resolution of primary monitor for width and height
		ByteBuffer monitor = glfwGetVideoMode(glfwGetPrimaryMonitor());
		WIDTH = GLFWvidmode.width(monitor)-200;	// 1720
		HEIGHT = GLFWvidmode.height(monitor)-200;	// 880
		
		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "3D RPG ALPHA", NULL, NULL);
			if(window == NULL)
				throw new RuntimeException("Failed to Create Window");
		
		// center the window and more
		glfwSetWindowPos(window,(GLFWvidmode.width(monitor) - WIDTH) / 2, (GLFWvidmode.height(monitor) - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		lastFrameTime = getCurrentTime(); // initialize value
		
		// Handle Scrolling Input with Callback
		glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset) {	
				Mouse.setScrollOffset(yoffset);	
			}
		});
	}
	
	public static void updateDisplay(){
		glfwSwapBuffers(window); // swap the color buffers
		glfwPollEvents(); // Poll for window events
		
		// keep track of time
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
		
	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	
	public static void closeDisplay(){
		scrollCallback.release(); // release scrollCallback
		glfwDestroyWindow(window); // Destroy window
		
		glfwTerminate(); // terminate glfw
		errorCallback.release(); // release errorCallback
	}
	
	// gets current time.
	private static long getCurrentTime(){
		return (long) (GLFW.glfwGetTime()*1000); //returns time in miliseconds
	}
		
}
