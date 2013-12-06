package net.clonecomputers.lab.sphere;

import static java.lang.Math.*;

public class SpherePoint {
	private final int id;
	private static int globalId = 0;
	private double theta = 0;
	private double phi = 0;
	private Point3D point;
	
	/**
	 * @param theta between 0 and 2pi
	 * @param phi between -pi/2 and pi/2
	 */
	public SpherePoint(double theta, double phi) {
		this.id = globalId++;
		this.theta = theta;
		this.phi = phi;
		updatePoint();
	}
	
	public SpherePoint() {
		this.id = globalId++;
		updatePoint();
	}
	
	public double getTheta() {
		return theta;
	}
	
	public double getPhi() {
		return phi;
	}
	
	public void setTheta(double theta) {
		this.theta = theta;
		updatePoint();
	}
	
	public void setPhi(double phi) {
		this.phi = phi;
		updatePoint();
	}
	
	private void updatePoint() {
		point = new Point3D(cos(theta),sin(theta),sin(phi));
	}

	public Point3D getPoint() {
		return point;
	}
	
	private class Point3D {
		private final double x;
		private final double y;
		private final double z;
		public Point3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
