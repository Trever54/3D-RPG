package input;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import renderEngine.DisplayManager;

public class Mouse {
	
	// create scroll callback
	public static GLFWScrollCallback scrollCallback;
	
	//Bytebuffers to keep track of coords
	public static ByteBuffer 
		mouseXb = ByteBuffer.allocateDirect(8),
		mouseYb = ByteBuffer.allocateDirect(8);
	
	public static double mouseX = 0, mouseY = 0;
	
	public static double currentFrameX =0, lastFrameX = 0, currentFrameY = 0, lastFrameY = 0;
	public static double deltaX = 0, deltaY = 0;
	
	public static double scrollOffset = 0;
	
	// updates the mouse coordinates to control deltaX and deltaY
	public static void update(){
		currentFrameX = getX();
		currentFrameY = getY();
		deltaX = currentFrameX - lastFrameX;
		deltaY = currentFrameY - lastFrameY;
		lastFrameX = currentFrameX;
		lastFrameY = currentFrameY;
	}
	
	// returns the x position of the mouse in the window
	public static double getX(){	
		glfwGetCursorPos(DisplayManager.window, mouseXb, mouseYb);
		mouseXb.order(ByteOrder.LITTLE_ENDIAN);
		mouseX = mouseXb.getDouble();
		mouseXb.flip();
		return mouseX;	
	}
	
	// returns the y position of the mouse in the window
	public static double getY(){ 
		glfwGetCursorPos(DisplayManager.window, mouseXb, mouseYb);
		mouseYb.order(ByteOrder.LITTLE_ENDIAN);
		mouseY = mouseYb.getDouble();
		mouseYb.flip();
		return mouseY;
	}
	
	public static double getDX(){
		return deltaX;
	}
	
	public static double getDY(){
		return deltaY;
	}

	public static double getScrollOffset() {
		return scrollOffset;
	}

	public static void setScrollOffset(double scrollOffset) {
		Mouse.scrollOffset = scrollOffset;
	}

	// returns whether or not a mouse button was pushed
	public static boolean isButtonDown(int button){
		return GLFW.glfwGetMouseButton(DisplayManager.window, button) == GLFW.GLFW_PRESS;
	}
	
	
}
