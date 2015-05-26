package com.codey.OpenGL;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Conservationist {

	private ArrayList<Particle> particles;
	private double initialEnergy;
	private double initialKineticEnergy;
	private double initialPotentialEnergy;
	private Vector3D initialLinearMomentum;
	private Vector3D initialAngularMomentum;
	private double tolerance;
	private double forceConstant;
	
	public Conservationist(ArrayList<Particle> particles, double tolerance, double forceConstant) {
		this.particles = particles;
		this.tolerance = tolerance;
		this.forceConstant = forceConstant;
		
		initialEnergy = energy();
		initialKineticEnergy = kineticEnergy();
		initialPotentialEnergy = potentialEnergy();
		initialLinearMomentum = linearMomentum();
		initialAngularMomentum = angularMomentum();
	}
	
	/*
	 * Conservation of Energy
	 */
	public boolean energyConserved() {
		double currentEnergy;
		
		currentEnergy = energy();
		
		return (currentEnergy < initialEnergy + (initialEnergy * tolerance)
				&& currentEnergy > initialEnergy - (initialEnergy * tolerance));
	}
	
	public double energyDeviation() {
		return (energy() / initialEnergy) - 1;
	}
	
	public double energy() {
		return kineticEnergy() + potentialEnergy();
	}
	
	public double kineticEnergy() {
		double ke;
		
		ke = 0;
		for (Particle particle : particles)
			ke += kineticEnergyForParticle(particle);
		
		return ke;
	}
	
	public double potentialEnergy() {
		Vector3D r;
		double pe;
		double distance;
		Particle particle, other;
		
		pe = 0;
		
		for (int i = 0; i < particles.size(); i++) {
			for (int ii = i - 1; ii >= 0; ii--) {
				particle = particles.get(i);
				other = particles.get(ii);
				
				r = other.getPosition().subtract(particle.getPosition());
				distance = r.getNorm();
				
				pe += -1 * forceConstant * other.getMass() * particle.getMass() / distance;
			}
		}
	
		return pe;
		
	}
	
	private double kineticEnergyForParticle(Particle particle) {
		double velocity;
		
		velocity = particle.getVelocity().getNorm();
		
		return 0.5 * particle.getMass() * velocity * velocity;
	}
	
	private double potentialEnergyForParticle(Particle particle) {
		Vector3D r;
		double pe;
		double distance;
		
		pe = 0;
		
		for (Particle other : particles) {
			if (!other.equals(particle)) {
				r = other.getPosition().subtract(particle.getPosition());
				distance = r.getNorm();
				
				pe += forceConstant * other.getMass() * particle.getMass() / distance;
			}
		}
		
		return pe;
	}
	
	/*
	 * Conservation of Linear Momentum 
	 */
	
	
	public boolean linearMomentumConserved() {
		Vector3D currentLinearMomentum;
		double delta;
		double momentumThreshold;
		double initialMagnitude;
		
		currentLinearMomentum = linearMomentum();
		delta = currentLinearMomentum.subtract(initialLinearMomentum).getNorm();
		
		initialMagnitude = initialLinearMomentum.getNorm();
		
		momentumThreshold = initialMagnitude - (tolerance * initialMagnitude);
		
		return delta < momentumThreshold;
	}
	
	public double linearMomentumDeviation() {
		return linearMomentum().getNorm() / initialLinearMomentum.getNorm() - 1;
	}
	
	private Vector3D linearMomentum() {
		Vector3D momentum;
		
		momentum = new Vector3D(0, 0, 0);
		
		for (Particle particle : particles) {
			momentum = momentum.add(linearMomentumForParticle(particle));
		}
		
		return momentum;
	}
	
	private Vector3D linearMomentumForParticle(Particle particle) {
		return particle.getVelocity().scalarMultiply(particle.getMass());
	}
	
	/*
	 * Conservation of Angular Momentum 
	 */
	
	
	public boolean angularMomentumConserved() {
		return true;
		
	}
	
	public double angularMomentumDeviation() {
		return 1 - angularMomentum().getNorm() / initialAngularMomentum.getNorm();
	}
	
	private Vector3D angularMomentum() {
		Vector3D momentum;
		
		momentum = Vector3D.ZERO;
		for (Particle particle : particles)
			momentum = momentum.add(angularMomentumForParticle(particle));
		
		return momentum;
	}
	
	private Vector3D angularMomentumForParticle(Particle particle) {
		Vector3D linearMomentum;
	
		linearMomentum = particle.getVelocity().scalarMultiply(particle.getMass());
		
		return particle.getPosition().crossProduct(linearMomentum);
	}
	
	
}
