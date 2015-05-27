package com.codey.OpenGL;

import static org.lwjgl.opengl.GL11.glTranslated;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.glRotated;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
 
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glMultMatrixf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.threed.SphericalCoordinates;
import static org.apache.commons.math3.util.FastMath.toDegrees;


public class CameraMan {
	private SphericalCoordinates position;
	private Particle target;
	private ArrayList<Particle> particles;
	private int debounce;
    private GLFWKeyCallback keyboardHandler;
	
	public CameraMan() {
		position = new SphericalCoordinates(400, 0, 0);
		target = new Particle(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), 0.);
		debounce = 0;
		//Keyboard.enableRepeatEvents(false);
		set();
	}
	
	public void setup() {
		reset();
		//moveCamera();
		set();
	}

	public void track(Particle particle) {
		target = particle;
	}
	
	public void trackNext() {
		track(particles.get((particles.indexOf(target) + 1 ) % (particles.size()) ));
	}
	
	public void trackPrevious() {
		track(particles.get((particles.indexOf(target) - 1 ) % (particles.size()) ));
	}
	
	public void setParticles(ArrayList<Particle> particles) {
		this.particles = particles;
		target = particles.get(0);
	}
	
	private void reset() {
		glLoadIdentity();	
	}
	
	private void moveCamera() {
		double dPhi, dTheta, dr;
		double r, theta, phi;
		
		r = position.getR();
		theta = position.getTheta();
		phi = position.getPhi();
		/*
		dPhi = Mouse.isButtonDown(0) ? -Mouse.getDX() * Math.PI / 90. : 0;
		dr = -((Mouse.getDWheel() / 120) * .1 * position.getR());
		
		position = new SphericalCoordinates(r + dr, 0, phi + dPhi);
		
		
		if ((Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) && debounce == 0) {
			track(particles.get((particles.indexOf(target) + 1 ) % (particles.size()) ));
			debounce = 8;
		}
		
		if ((Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) && debounce == 0) {
			if (particles.indexOf(target) != 0)
				track(particles.get((particles.indexOf(target) - 1) % (particles.size()) ));
			else 
				track(particles.get(particles.size() - 1));
			debounce = 8;
		}
		
		if (debounce != 0)
			debounce--;
			*/
	}
	
	private void set() {
		Vector3D eye;
		Vector3D object;
		
		eye = position.getCartesian();
		object = target.getPosition();
		
		eye = eye.add(object);
		
		//lookAt(object, eye, new Vector3D(0., 1., 0.)); 
		//GLU.gluLookAt((float)positionXYZ.getX(), (float)positionXYZ.getY(), (float)positionXYZ.getZ(),
			//	(float)lookAt.getX(), (float)lookAt.getY(), (float)lookAt.getZ(), 0f, 1f, 0f);
	}
	
	private void lookAt(Vector3D object, Vector3D eye, Vector3D up) {
		Matrix4f matrix = new Matrix4f();
		FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
		Vector3D forward = object.subtract(eye).normalize();
		Vector3D side = forward.crossProduct(up).normalize();
		
		up = side.crossProduct(forward);

		matrix.m00 = (float) side.getX();
		matrix.m01 = (float) side.getY();
		matrix.m02 = (float) side.getZ();
		matrix.m03 = 0f;

		matrix.m10 = (float) up.getX();
		matrix.m11 = (float) up.getY();
		matrix.m12 = (float) up.getZ();
		matrix.m13 = (float) 0f;

		matrix.m20 = (float) forward.getX();
		matrix.m21 = (float) forward.getY();
		matrix.m22 = (float) forward.getZ();
		matrix.m23 = (float) 0f;
		
		matrix.m30 = matrix.m31 = matrix.m32 = 0f;
		matrix.m33 = 1f;
		
		matrix.store(matrixData);
		matrixData.position(0);
		glMultMatrixf(matrixData);
		
	}
	
	public void screenshot(String path) throws IOException {
		int screenWidth, screenHeight, bytesPerPixel;
		BufferedImage image;
		String format;
		File file;
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		glReadBuffer(GL_FRONT);
		screenWidth = GLFWvidmode.width(vidmode);
		screenHeight = GLFWvidmode.height(vidmode);
		bytesPerPixel = 4;
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(screenWidth * screenHeight * bytesPerPixel);
		glReadPixels(0, 0, screenWidth, screenHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
		
		file = new File(path);
		format = "JPG";
		image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
		   
		for(int x = 0; x < screenWidth; x++) 
		{
		    for(int y = 0; y < screenHeight; y++)
		    {
		        int i = (x + (screenWidth * y)) * bytesPerPixel;
		        int r = buffer.get(i) & 0xFF;
		        int g = buffer.get(i + 1) & 0xFF;
		        int b = buffer.get(i + 2) & 0xFF;
		        image.setRGB(x, screenHeight - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		    }
		}
		   
		ImageIO.write(image, format, file);
		
	}
	
}
