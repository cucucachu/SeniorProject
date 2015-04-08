package com.codey.OpenGL;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class OctTree {
	
	private OctNode root;
	
	public OctTree(double width, double theta, ArrayList<Particle> particles) {
		root = new OctNode(Vector3D.ZERO, width, theta, particles);
	}
	
	public ArrayList<Particle> reducedParticles(Vector3D position) {
		return root.reducedParticles(position);
	}
	
	public OctNode getRoot() {
		return root;
	}
	
	public void divide() {
		root.divide();
	}
}
