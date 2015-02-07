package com.codey.OpenGL;

import org.lwjgl.LWJGLException;

public class Launcher {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int FRAME_RATE = 32;
	
	public static Painter picaso;
	public static Spring spring;
	
	public static void main(String[] args) {
		try {
			launch();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}
	}
	
	public static void launch() throws LWJGLException, Exception {
		LibraryLoader.loadNativeLibrary();
		picaso = new Painter(WIDTH, HEIGHT, FRAME_RATE);
		spring = new Spring(picaso);
		spring.run();
		
		picaso.janitor();
	}
}
