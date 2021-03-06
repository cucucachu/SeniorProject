package com.codey.OpenGL;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class StaticGravitationalForce extends ForceField {
	private static final Vector3D center = new Vector3D(960, 540, -500);
	
	public StaticGravitationalForce(double gravitationalConstant) {
		super(gravitationalConstant);
	}
	
	@Override
	public Vector3D forceAtPoint(Vector3D position) {
		Vector3D r, force;
		double magnitude;
		
		r = center.subtract(position);
		r = r.normalize();
		magnitude = r.getNorm();

		force = r.scalarMultiply(forceConstant / (magnitude * magnitude));
		
		return force;
	}

}
