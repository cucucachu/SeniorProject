package com.codey.OpenGL;

public class GravitationalForce extends ForceField {

	public GravitationalForce(double gravitationalConstant) {
		super(gravitationalConstant);
	}
	
	@Override
	public double forceAtPoint(double position_x, double position_y) {
		return 0;
	}

}