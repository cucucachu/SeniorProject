package com.codey.OpenGL;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class SpringForce extends ForceField {
	
	public SpringForce(double springConstant) {
		super(springConstant);
	}
	
	public Vector3D forceAtPoint(Vector3D position) {
		return position.subtract(new Vector3D(960, 540, 0)).scalarMultiply(-forceConstant);
	}

}
