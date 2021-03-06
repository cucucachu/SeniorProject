package com.codey.OpenGL;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.ArrayList;
import org.apache.commons.math3.exception.MathArithmeticException;

public class SSGravitationalForce extends NForceField {
	
	public SSGravitationalForce(double gravitationalConstant, ArrayList<Particle> particles, double theta) {
		super(gravitationalConstant, particles, theta);
	}
	
	@Override
	public Vector3D forceOnParticle(Particle particle) {
		Vector3D r, force;
		double magnitude;
		ArrayList<Particle> reducedParticles;
		
		reducedParticles = octTree.reducedParticles(particle.getPosition());
		
		force = Vector3D.ZERO;
		
		for (Particle other : reducedParticles) {
				r = other.getPosition().subtract(particle.getPosition());
				
				if (r.equals(Vector3D.ZERO))
					continue;

				magnitude = r.getNorm();
				r = r.normalize();
				
				
				force = force.add(
						r.scalarMultiply(
						other.getMass() * forceConstant / (magnitude * magnitude))
						);
			
		}
		return force;
	}
	
	public void updateOctTree() throws Exception {
		octTree.update();
	}

}
