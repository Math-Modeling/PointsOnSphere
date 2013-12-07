package net.clonecomputers.lab.sphere;

import static java.lang.Math.*;

public class SpherePoint {
	public final int id;
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
	
	public SpherePoint(double x, double y, double z) {
		this(new Point3D(x,y,z));
	}
	
	public SpherePoint(Point3D point) {
		this.id = globalId++;
		this.setPoint(point);
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
		point = new Point3D(cos(phi)*cos(theta),cos(phi)*sin(theta),sin(phi));
	}
	
	public SpherePoint getRelative(SpherePoint s) {
		SpherePoint s2 = this.clone();
		s2.setTheta(s2.getTheta() - s.getTheta());
		s2.rotateAroundLine(s.getPhi());
		return s2;
	}
	
	public SpherePoint clone() {
		return new SpherePoint(theta,phi);
	}

	private void rotateAroundLine(double howFar) { // around line through (pi/2,0) and (3pi/2,0)
		SpherePoint start = this.clone();
		double xzDistance = Math.hypot(getPoint().x, getPoint().z);
		double xzAngle = Math.atan2(getPoint().z,getPoint().x);
		Point3D newPoint = new Point3D(xzDistance*cos(xzAngle+howFar),getPoint().y,xzDistance*sin(xzAngle+howFar));
		this.setPoint(newPoint);
		//System.out.println(howFar+": "+start.getPoint()+" -> "+this.getPoint());
	}
	
	public String toString() {
		return "("+theta+", "+phi+")";
	}

	public Point3D getPoint() {
		return point;
	}
	
	public void setPoint(Point3D point) {
		this.point = point;
		this.theta = atan2(point.y,point.x);
		this.phi = asin(point.z);
	}
	
	/*public boolean equals(Object o) {
		return (o instanceof SpherePoint) && ((SpherePoint)o).id == id;
	}
	
	public int hashCode() {
		return id;
	}*/
}
