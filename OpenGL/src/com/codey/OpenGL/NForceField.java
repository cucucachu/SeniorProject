package com.codey.OpenGL;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;

public abstract class NForceField {
	protected double forceConstant;
	protected OctoTree octoTree;
	protected ArrayList<Particle> particles;
	
	public NForceField(double forceConstant, ArrayList<Particle> particles, double theta) {
		double width;
		
		this.forceConstant = forceConstant;
		this.particles = particles;
		
		width = 0;
		
		for (Particle particle : particles) {
			
			if (particle.getX() > width)
				width = particle.getX();
			
			if (particle.getY() > width)
				width = particle.getY();
			
			if (particle.getZ() > width)
				width = particle.getZ();
		}
		
		width *= 2;
		
		this.octoTree = new OctoTree(width, theta, particles);
	}

	public abstract Vector3D forceOnParticle(Particle particle);
}
