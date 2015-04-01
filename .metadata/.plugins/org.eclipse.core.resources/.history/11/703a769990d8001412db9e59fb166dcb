package com.codey.OpenGL;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;

public abstract class NForceField {
	protected double forceConstant;
	protected ArrayList<Particle> particles;
	
	public NForceField(double forceConstant, ArrayList<Particle> particles) {
		this.forceConstant = forceConstant;
		this.particles = particles;
	}

	public abstract Vector3D forceOnParticle(Particle particle);
}
