package com.codey.OpenGL;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class StaticOrbit {
	private static final int NUM_PARTICLES = 10;
	private Painter picaso;
	
	public ArrayList<Particle> particles;
	public StaticGravitationalForce gravity;
	
	public StaticOrbit(Painter picaso) {
		Particle particle;
		Random random = new Random();
		this.picaso = picaso;
		particles = new ArrayList<Particle>();
		
		
		for (int i = 0; i < NUM_PARTICLES; i++) {
			particle = new Particle(new Vector3D(1920*random.nextDouble(), 1080*random.nextDouble(), 0),
									new Vector3D(50*random.nextDouble() - 50, 50*random.nextDouble() - 50, 0), 1.0);
			particles.add(particle);
		}
		
		gravity = new StaticGravitationalForce(1);
	}
	
	public void run() {	
		Vector3D force;
		
		while(!picaso.isCloseRequested()) {
			picaso.checkForDisplayResize();
			picaso.clear();
			
			for (Particle particle : particles) {
				force = gravity.forceAtPoint(particle.getPosition());
				particle.step(1, force);
				picaso.drawSphere(particle.getPosition(), 10, 20); 
			}
			picaso.render();
		}
		
		picaso.janitor();
	}
}
