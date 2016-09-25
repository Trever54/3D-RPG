package input;

import org.lwjgl.glfw.GLFW;

import renderEngine.DisplayManager;

public class Keyboard {

	public static boolean isKeyDown(int key){	
		return GLFW.glfwGetKey(DisplayManager.window, key) == GLFW.GLFW_PRESS;				
	}	
	
}
