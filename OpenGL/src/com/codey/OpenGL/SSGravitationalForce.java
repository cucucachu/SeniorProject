package com.codey.OpenGL;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;

public class SSGravitationalForce extends NForceField {
	
	public SSGravitationalForce(double gravitationalConstant, ArrayList<Particle> particles) {
		super(gravitationalConstant, particles);
	}
	
	@Override
	public Vector3D forceOnParticle(Particle particle) {
		Vector3D r, force;
		double magnitude;
		
		force = Vector3D.ZERO;
		
		for (Particle other : particles) {
			if (!other.equals(particle)) {
				r = other.getPosition().subtract(particle.getPosition());
				r = r.normalize();
				magnitude = r.getNorm();
				force = force.add(
						r.scalarMultiply(
						other.getMass() * forceConstant / (magnitude * magnitude * magnitude))
						);
			}
		}
		return force;
	}

}
