package com.codey.OpenGL;

public class Orbit {
	private Painter picaso;
	
	public static Mass mass;
	public static GravitationalField gravity;
	
	public Orbit(Painter picaso) {
		this.picaso = picaso;
		mass = new Mass(100.0, 0.0, 1.0);
		gravity = new GravitationalField(0.01);
	}
	
	public void run() {	
		double force;
		
		while(!picaso.isCloseRequested()) {
			picaso.checkForDisplayResize();
			force = gravity.forceAtPoint(mass.getPosition());
			mass.step(1, force);
			picaso.clear();
			picaso.drawSquare(mass.getPosition(), 300, 20);
			picaso.render();
		}
		
		picaso.janitor();
	}

}
