package com.codey.OpenGL;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;


public class Painter {
	private int screenWidth;
	private int screenHeight;
	private int frameRate;
	
	public Painter(int screenWidth, int screenHeight, int frameRate) throws LWJGLException {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.frameRate = frameRate;
		displaySetup();
	}
	
	private void displaySetup() throws LWJGLException {
		Display.setTitle("OpenGL");
		Display.setResizable(true);
		Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
		Display.setVSyncEnabled(true);
		Display.setFullscreen(true);
		Display.create();

		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, screenWidth, 0, screenHeight, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0f, 0f, 0f, 1f);
	}
	
	public void clear() {
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
		glColor3f(.5f, .25f, 1f);
	}
	
	public void drawSquare(double x, double y, double width) {		
		
		glPushMatrix();
		{
			glTranslated(x, y, 0);
			glBegin(GL_QUADS);
			{
				glVertex2d(width/2.0, width/2.0);
				glVertex2d(width/2.0, -width/2.0);
				glVertex2d(-width/2.0, -width/2.0);
				glVertex2d(-width/2.0, width/2.0);
			}
			glEnd();
		}
		glPopMatrix();
	}
	
	public void render() {
		Display.update();
		Display.sync(frameRate);	
	}

	
	public void checkForDisplayResize() {
		if (Display.wasResized())
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public void janitor() {
		Display.destroy();
	}
	
	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

}
