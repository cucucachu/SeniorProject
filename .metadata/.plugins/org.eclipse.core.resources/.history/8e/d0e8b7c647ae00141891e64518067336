package com.codey.OpenGL;

public class Mass {
	private double position;
	private double velocity;
	private double mass;
	
	public Mass(double position, double velocity, double mass) {
		this.position = position;
		this.velocity = velocity;
		this.mass = mass;
	}
	
	public void step(double timeStep, double force) {
		velocity += (force)*(timeStep);
		position += velocity * timeStep;		
	}	
	
	public double getPosition() {
		return position;
	}
}
