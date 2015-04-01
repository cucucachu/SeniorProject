package com.codey.OpenGL;

import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class OctoNode {
	private Vector3D position;
	private double width;
	private double mass;
	private double theta;
	
	private ArrayList<Particle> particles;

	private OctoNode NW;
	private OctoNode NE;
	private OctoNode SW;
	private OctoNode SE;
	
	public OctoNode(Vector3D position, double width, double theta, ArrayList<Particle> particles) {
		this.position = position;
		this.theta = theta;
		this.width = width;
		this.particles = particles;
		
		this.mass = 0;
		
		for (Particle particle : particles) {
			this.mass += particle.getMass();
		}
		
		try {
			if (particles.size() > 1)
				divide();
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	private void divide() throws Exception {
		ArrayList<Particle> particlesNW;
		ArrayList<Particle> particlesNE;
		ArrayList<Particle> particlesSW;
		ArrayList<Particle> particlesSE;

		Vector3D positionNW;
		Vector3D positionNE;
		Vector3D positionSW;
		Vector3D positionSE;
		
		double x = position.getX();
		double y = position.getY();
		
		if (particles.isEmpty() || particles.size() == 1)
			throw new Exception("Tried to divide a OctoNode with one or zero particles.");
		
		particlesNW = new ArrayList<Particle>();
		particlesNE = new ArrayList<Particle>();
		particlesSW = new ArrayList<Particle>();
		particlesSE = new ArrayList<Particle>();
		
		for (Particle particle : particles) {
			if (particle.getX() < x && particle.getY() > y)
				particlesNW.add(particle);
			else if (particle.getX() > x && particle.getY() > y)
				particlesNE.add(particle);
			else if (particle.getX() < x && particle.getY() < y)
				particlesSW.add(particle);
			else
				particlesSE.add(particle);
		}

		positionNW = new Vector3D(x - (width / 2.), y + (width / 2.), 0);
		positionNE = new Vector3D(x + (width / 2.), y + (width / 2.), 0);
		positionSW = new Vector3D(x - (width / 2.), y - (width / 2.), 0);
		positionSE = new Vector3D(x + (width / 2.), y - (width / 2.), 0);
		
		NW = new OctoNode(positionNW, this.width / 2., theta, particlesNW);
		NE = new OctoNode(positionNE, this.width / 2., theta, particlesNE);
		SW = new OctoNode(positionSW, this.width / 2., theta, particlesSW);
		SE = new OctoNode(positionSE, this.width / 2., theta, particlesSE);
	}
	
	public ArrayList<Particle> reducedParticles(Vector3D position) {
		ArrayList<Particle> reduced;
		double distance;

		reduced = new ArrayList<Particle>();
		
		if (particles.size() == 0)
			return reduced;
		
		if (particles.size() == 1)
			return particles;
		
		distance = this.position.subtract(position).getNorm();
		
		if (distance < theta) {
			reduced.add(new Particle(this.position, this.mass));
			return reduced;
		}

		reduced.addAll(NW.reducedParticles(position));
		reduced.addAll(NE.reducedParticles(position));
		reduced.addAll(SW.reducedParticles(position));
		reduced.addAll(SE.reducedParticles(position));
		
		return reduced;
			
	}
	
	public double getX() {
		return position.getX();
	}
	
	public double getY() {
		return position.getY();
	}
	
	public double getZ() {
		return position.getZ();
	}
	
	public double getMass() {
		return this.mass;
	}
}
