package com.codey.OpenGL;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class ForceField {
	protected double forceConstant;
	
	protected ForceField(double forceConstant) {
		this.forceConstant = forceConstant;
	}
	
	public abstract Vector3D forceAtPoint(Vector3D position);
}
