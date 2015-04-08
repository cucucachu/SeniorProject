package com.codey.OpenGL;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;

public abstract class NForceField {
	protected double forceConstant;
	protected OctTree octTree;
	protected ArrayList<Particle> particles;
	
	public NForceField(double forceConstant, ArrayList<Particle> particles, double theta) {
		double width;
		
		this.forceConstant = forceConstant;
		this.particles = particles;
		
		width = 0;
		
		for (Particle particle : particles) {
			
			if (particle.getPosition().getNorm() > width)
				width = particle.getPosition().getNorm();
		}
		
		width *= 2;
		
		this.octTree = new OctTree(width, theta, particles);
	}

	public abstract Vector3D forceOnParticle(Particle particle);
	
	public OctTree getOctTree() {
		return octTree;
	}
}
