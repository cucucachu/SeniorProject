package com.codey.OpenGL;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class OctoTree {
	
	private OctoNode root;
	
	public OctoTree(double width, double theta, ArrayList<Particle> particles) {
		root = new OctoNode(Vector3D.ZERO, width, theta, particles);
	}
	
	public ArrayList<Particle> reducedParticles(Vector3D position) {
		return root.reducedParticles(position);
	}
}
