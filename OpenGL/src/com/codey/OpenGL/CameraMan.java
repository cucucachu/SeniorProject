package com.codey.OpenGL;

import static org.lwjgl.opengl.GL11.glTranslated;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glRotated;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
	
	public CameraMan() {
		position = new SphericalCoordinates(400, 0, 0);
		target = new Particle(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), 0.);
		debounce = 0;
		Keyboard.enableRepeatEvents(false);
		set();
	}
	
	public void setup() {
		reset();
		moveCamera();
		set();
	}
	
	public void track(Particle particle) {
		target = particle;
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
	}
	
	private void set() {
		Vector3D positionXYZ;
		Vector3D lookAt;
		
		positionXYZ = position.getCartesian();
		lookAt = target.getPosition();
		
		positionXYZ = positionXYZ.add(lookAt);
		
		GLU.gluLookAt((float)positionXYZ.getX(), (float)positionXYZ.getY(), (float)positionXYZ.getZ(),
				(float)lookAt.getX(), (float)lookAt.getY(), (float)lookAt.getZ(), 0f, 1f, 0f);
	}
	
	public void screenshot(String path) throws IOException {
		int screenWidth, screenHeight, bytesPerPixel;
		BufferedImage image;
		String format;
		File file;
		
		glReadBuffer(GL_FRONT);
		screenWidth = Display.getDisplayMode().getWidth();
		screenHeight = Display.getDisplayMode().getHeight();
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
