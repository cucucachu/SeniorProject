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
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3d;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.util.glu.GLU.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
import java.nio.ByteBuffer;
 
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Matrix4f;


public class Painter {
	private int screenWidth;
	private int screenHeight;
	private static final int screenDepth = 1000000;
	private int frameRate;
	private static final int SPHERE_SLICES = 20;

	private static final float fieldOfView = 70.0f;
	private static final float aspectRatio = 16.f/9.f;//(float)Display.getWidth() / (float)Display.getHeight();
	private static final float near = 0.3f;
	private static final float far = 10000.0f;
	
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
 
    private long window;
    
	public Painter(int screenWidth, int screenHeight, int frameRate) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.frameRate = frameRate;
		displaySetup();
	}
	
	private void displaySetup() {
		double[] projectionMatrix;
		/*
		Display.setTitle("OpenGL");
		Display.setResizable(true);
		Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
		Display.setVSyncEnabled(true);
		Display.setFullscreen(true);
		Display.create();
		*/
		
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
 
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        int WIDTH = GLFWvidmode.width(vidmode);
        int HEIGHT = GLFWvidmode.height(vidmode);
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
            }
        });
 
        // Center our window
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
 
        // Make the window visible
        glfwShowWindow(window);
		
		//glViewport(0, 0, Display.getWidth(), Display.getHeight());
		

        GLContext.createFromCurrent();
 
        //glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		//gluPerspective(fieldOfView, aspectRatio, near, far);
		//buildProjectionMatrix(fieldOfView, aspectRatio, near, far);
		
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		
	}
	
	public void clear() {
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);	
	}
	
	public void drawSquare(double x, double y, double width) {		
		glColor3d(1.,1.,1.);
		
		glPushMatrix();
		{
			glTranslated(x, y, -100);
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
	
	public void drawSphere(Vector3D position, double radius, int slices) {
		Sphere sphere = new Sphere();

		glColor3d(1.,1.,1.);
		
		glPushMatrix();
		{
			glTranslated(position.getX(), position.getY(), position.getZ());
			sphere.draw((float)radius, slices, slices);
		}
		glPopMatrix();
	}
	
	public void drawParticle(Particle particle) {
		Sphere sphere = new Sphere();
		Vector3D position;
		double radius;
		double rgb[];
		
		position = particle.getPosition();
		radius = particle.getRadius();
		rgb = particle.getColor();
		
		if (radius == 0)
			radius = 10;

		glPushMatrix();
		{
			glColor3d(rgb[0], rgb[1], rgb[2]);
			glTranslated(position.getX(), position.getY(), position.getZ());
			sphere.draw((float)radius, SPHERE_SLICES, SPHERE_SLICES);
		}
		glPopMatrix();
	}
	
	public void drawOctTree(OctNode root) {
		OctNode node;

		if (root == null)
			return;
		
		drawOctTree(root.getNNW());
		drawOctTree(root.getNNE());
		drawOctTree(root.getNSW());
		drawOctTree(root.getNSE());
		drawOctTree(root.getFNW());
		drawOctTree(root.getFNE());
		drawOctTree(root.getFSW());
		drawOctTree(root.getFSE());
		
		drawOctNode(root);
	}
	
	public void drawOctNode(OctNode node) {
		double x, y, z, d;
		
		x = node.getX();
		y = node.getY();
		z = node.getZ();
		d = node.getWidth() / 2.;
		
	    glColor3d(0.0f, 1.0f, 0.2f);
	    glBegin(GL_LINE_STRIP);
	    {
	    	glVertex3d(x - d, y - d, z - d);
	    	glVertex3d(x + d, y - d, z - d);
	    	glVertex3d(x + d, y + d, z - d);
	    	glVertex3d(x - d, y + d, z - d);
	    	glVertex3d(x - d, y - d, z - d);	

	    	glVertex3d(x - d, y - d, z + d);
	    	glVertex3d(x + d, y - d, z + d);
	    	glVertex3d(x + d, y + d, z + d);
	    	glVertex3d(x - d, y + d, z + d);
	    	glVertex3d(x - d, y - d, z + d);

	    	glVertex3d(x - d, y + d, z + d);
	    	glVertex3d(x - d, y + d, z - d);
	    	glVertex3d(x + d, y + d, z - d);
	    	glVertex3d(x + d, y + d, z + d);
	    	glVertex3d(x + d, y + d, z - d);
	    	glVertex3d(x + d, y - d, z - d);
	    	glVertex3d(x + d, y - d, z + d);
	    }
	    glEnd();
	}
	
	public void render() {
        GLContext.createFromCurrent();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwSwapBuffers(window); // swap the color buffers
        
        glPushMatrix();
        {
        	glTranslated(0, 0, 400);
        }
		glPopMatrix();
		//Display.update();
		//Display.sync(frameRate);	
	}
	
	private Matrix4f buildProjectionMatrix(double fov, double aspect, double znear, double zfar) {
		Matrix4f m = new Matrix4f();
		double ymax = znear * Math.tan(fov * Math.PI / 360.);
		double ymin = -ymax;
		double xmax = ymax * aspect;
		double xmin = ymin * aspect;
		
		double width = xmax - xmin;
		double height = ymax - ymin;
		
		double depth = zfar - znear;
		double q = -(zfar + znear) / depth;
		double qn = -2 * (zfar * znear) / depth;
		
		double w = 2 * znear / width;
		w = w / aspect;
		double h = 2 * znear / height;
		
		glFrustum(-aspect*near*fov, aspect*near*fov, -fov, fov, near, far);
		
		m.m00  = (float)w;
		m.m01  = 0;
		m.m02  = 0;
		m.m03 = 0;
		
		m.m10  = 0;
		m.m11 = (float)h;
		m.m12 = 0;
		m.m13 = 0;
		
		m.m20 = 0;
		m.m21 = 0;
		m.m22 = (float)q;
		m.m23 = -1;
		
		m.m30 = 0;
		m.m31 = 0;
		m.m32 = (float)qn;
		m.m33 = 0;
		
		return m;
	}

	/*
	public void checkForDisplayResize() {
		if (Display.wasResized())
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	*/
	
	public long getWindow() {
		return window;
	}
	
	public void janitor() {
		//Display.destroy();

        glfwDestroyWindow(window);
        keyCallback.release();
        glfwTerminate();
        errorCallback.release();
	}
	
	public boolean isCloseRequested() {
		return (glfwWindowShouldClose(window) == GL_TRUE);
	}

}
