package com.codey.OpenGL;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class OctTree {
	
	private OctNode root;
	private ArrayList<Particle> particles;
	private double theta;
	
	public OctTree(double theta, ArrayList<Particle> particles) throws Exception {
		this.particles = particles;
		this.theta = theta;
		update();
	}
	
	public ArrayList<Particle> reducedParticles(Vector3D position) {
		return root.reducedParticles(position);
	}
	
	public OctNode getRoot() {
		return root;
	}
	
	public void divide() throws Exception {
		root.divide();
	}
	
	public void update() throws Exception {
		root = new OctNode(Vector3D.ZERO, calculateWidth(), theta, particles);
	}
	
	private double calculateWidth() {
		double width;
		
		width = 0;
		
		for (Particle particle : particles) 			
			if (particle.getPosition().getNorm() > width)
				width = particle.getPosition().getNorm();
		
		return width *= 2.5;
	}
}
