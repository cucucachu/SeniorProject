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
		double accelMagnitude;
		ArrayList<Particle> reducedParticles;
		
		if (octTree.getTheta() != 0)
			reducedParticles = octTree.reducedParticles(particle.getPosition());
		else
			reducedParticles = particles;
		
		force = Vector3D.ZERO;
		
		for (Particle other : reducedParticles) {
			
			if (other.equals(particle))
				continue;
			
			r = other.getPosition().subtract(particle.getPosition());

			magnitude = r.getNorm();
			r = r.normalize();
			
			
			if (magnitude < 1)
				magnitude = 1;
			
			
			accelMagnitude = other.getMass() * forceConstant / (magnitude * magnitude);
			
			force = force.add(r.scalarMultiply(accelMagnitude));
			
		}
		return force;
	}
	
	public void updateOctTree() throws Exception {
		octTree.update();
	}

}
