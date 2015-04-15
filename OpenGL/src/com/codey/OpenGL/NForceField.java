package com.codey.OpenGL;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;

public abstract class NForceField {
	protected double forceConstant;
	protected OctTree octTree;
	protected ArrayList<Particle> particles;
	
	public NForceField(double forceConstant, ArrayList<Particle> particles, double theta) {
		
		this.forceConstant = forceConstant;
		this.particles = particles;
		
		try {
			this.octTree = new OctTree(theta, particles);
		}
		catch (Exception ex) {
			System.out.println("Exception Caught while creating octtree. " + ex);
		}
	}

	public abstract Vector3D forceOnParticle(Particle particle);
	
	public OctTree getOctTree() {
		return octTree;
	}
}
