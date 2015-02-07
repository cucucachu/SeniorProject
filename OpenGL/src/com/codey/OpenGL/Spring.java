package com.codey.OpenGL;

import org.lwjgl.LWJGLException;

public class Spring {
	
	private Painter picaso;
	
	public static Mass mass;
	public static SpringForce spring;
	
	public Spring(Painter picaso) {
		this.picaso = picaso;
		mass = new Mass(100.0, 0.0, 1.0);
		spring = new SpringForce(0.01);
	}
	
	public void run() {	
		double force;
		
		while(!picaso.isCloseRequested()) {
			picaso.checkForDisplayResize();
			force = spring.forceAtPoint(mass.getPosition());
			mass.step(1, force);
			picaso.clear();
			picaso.drawSquare(mass.getPosition(), 300, 20);
			picaso.render();
		}
		
		picaso.janitor();
	}
}
