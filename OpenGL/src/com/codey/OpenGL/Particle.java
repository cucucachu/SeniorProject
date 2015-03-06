package com.codey.OpenGL;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Particle {
	private Vector3D position;
	private Vector3D velocity;
	private double mass;
	private double radius;
	private double rgb[];
	
	public Particle(Vector3D position, Vector3D velocity, double mass) {
		this.position = position;
		this.velocity = velocity;
		this.mass = mass;
		radius = 0;
		
		rgb = new double[3];
		rgb[0] = .5;
		rgb[1] = .5;
		rgb[2] = .5;
	}
	
	public Particle(Vector3D position, Vector3D velocity, double mass, double radius) {
		this.position = position;
		this.velocity = velocity;
		this.mass = mass;
		this.radius = radius;

		rgb = new double[3];
		rgb[0] = .5;
		rgb[1] = .5;
		rgb[2] = .5;
	}
	
	public void step(double timeStep, Vector3D force) {
		velocity = velocity.add(force.scalarMultiply(timeStep));
		position = position.add(velocity.scalarMultiply(timeStep));
	}	
	
	public void verletStep(double timeStep, Vector3D accel) {
		
	}

	public void setColor(double red, double green, double blue) {
		rgb[0] = red;
		rgb[1] = green;
		rgb[2] = blue;
	}

	public void setColor(double[] rgb) {
		this.rgb[0] = rgb[0];
		this.rgb[1] = rgb[1];
		this.rgb[2] = rgb[2];
	}
	
	public Vector3D getPosition() {
		return position;
	}	
	
	public Vector3D getVelocity() {
		return velocity;
	}	
	
	public void print() {
		System.out.println("Particle:");
		System.out.printf("   Pos: (%.2f, %.2f)", position.getX(), position.getY());
		System.out.printf("   Vel: (%.2f, %.2f)\n", velocity.getX(), velocity.getY());
	}
	
	public double getMass() {
		return mass;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double[] getColor() {
		return this.rgb;
	}
}
