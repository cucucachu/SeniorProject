package com.codey.OpenGL;

import java.io.IOException;

import org.lwjgl.glfw.GLFWKeyCallback; 
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler extends GLFWKeyCallback {
	private Painter picaso;
	private CameraMan carl;
	private Librarian linda;

	public KeyboardHandler(Painter picaso, CameraMan carl, Librarian linda) {
		super();
		this.picaso = picaso;
		this.carl = carl;
		this.linda = linda;
	}
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS) {
	        if (key == GLFW_KEY_LEFT || key == GLFW_KEY_A)
	        	carl.trackNext();
	        else if (key == GLFW_KEY_RIGHT || key == GLFW_KEY_D)
	        	carl.trackPrevious();
	        else if (key == GLFW_KEY_ENTER || key == GLFW_KEY_D)
				try {
					linda.recordSimulation();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}

}
