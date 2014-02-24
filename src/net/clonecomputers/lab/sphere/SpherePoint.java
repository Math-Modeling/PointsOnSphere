package net.clonecomputers.lab.sphere;

import static java.lang.Math.*;

import java.util.*;

public class SpherePoint {
	public final int id;
	private static int globalId = 0;
	
	private double theta = 0;
	private double phi = 0;
	private Point3D point;
	
	private final boolean trace;
	public final SpherePoint olderInTrace;
	private SpherePoint mostRecentInTrace;
	public final SpherePoint parent;
	
	private SpherePoint(SpherePoint previous, SpherePoint parent) {
		this.id = globalId++;
		this.trace = false;
		//if(previous == null) throw new NullPointerException("Previous point null");
		this.olderInTrace = previous;
		this.theta = parent.theta;
		this.phi = parent.phi;
		this.parent = parent;
	}
	
	/**
	 * @param theta between 0 and 2pi
	 * @param phi between -pi/2 and pi/2
	 */
	public SpherePoint(double theta, double phi) {
		this(theta, phi, false);
	}
	
	public SpherePoint(double x, double y, double z) {
		this(new Point3D(x,y,z));
	}
	
	public SpherePoint(Point3D point) {
		this(point, false);
	}
	
	public SpherePoint(SpherePoint point) {
		this(point.theta, point.phi);
	}
	
	/**
	 * makes a random point
	 */
	public SpherePoint() {
		this(false);
	}
	
	/**
	 * @param theta between 0 and 2pi
	 * @param phi between -pi/2 and pi/2
	 */
	public SpherePoint(double theta, double phi, boolean trace) {
		this.id = globalId++;
		this.theta = theta;
		this.phi = phi;
		this.trace = trace;
		this.olderInTrace = null;
		this.parent = null;
		updatePoint();
		if(trace) this.mostRecentInTrace = new SpherePoint(null, this);
	}
	
	public SpherePoint(double x, double y, double z, boolean trace) {
		this(new Point3D(x,y,z), trace);
	}
	
	public SpherePoint(Point3D point, boolean trace) {
		this.id = globalId++;
		this.trace = trace;
		this.olderInTrace = null;
		this.parent = null;
		this.setPoint(point);
		if(trace) this.mostRecentInTrace = new SpherePoint(null, this);
	}
	
	/**
	 * makes a random point
	 */
	public SpherePoint(boolean trace) {
		this.id = globalId++;
		this.trace = trace;
		this.olderInTrace = null;
		this.parent = null;
		double z = random()*2-1;
		double phi = random()*2*PI;
		this.setPoint(new Point3D(sqrt(1-z*z)*cos(phi), sqrt(1-z*z)*sin(phi), z));
		if(trace) this.mostRecentInTrace = new SpherePoint(null, this);
	}

	public SpherePoint(SpherePoint point, boolean trace) {
		this(point.theta, point.phi, trace);
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
	
	public void setPoint(Point3D point) {
		this.point = point;
		this.theta = atan2(point.y,point.x);
		this.phi = asin(point.z);
		if(trace && (mostRecentInTrace == null || cos(mostRecentInTrace,this) < .9999)){
			//if(mostRecentInTrace != null) System.out.println("cos:"+cos(mostRecentInTrace,this));
			this.mostRecentInTrace = new SpherePoint(mostRecentInTrace,this);
		}
	}
	
	public boolean hasTrace() {
		return trace;
	}
	
	public Iterable<SpherePoint> getTrace() {
		if(!trace) return null;
		return new Iterable<SpherePoint>() {
			@Override public Iterator<SpherePoint> iterator() {
				return new Iterator<SpherePoint>() {
					SpherePoint current = SpherePoint.this.mostRecentInTrace;
					@Override
					public boolean hasNext() {
						return current != null;
					}

					@Override
					public SpherePoint next() {
						SpherePoint ret = current;
						current = current.olderInTrace;
						return ret;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException("Trace is readonly");
					}
					
				};
			}
		};
	}

	public Point3D getPoint() {
		return point;
	}
	
	public SpherePoint getRelative(SpherePoint s) {
		SpherePoint s2 = this.clone();
		s2.setTheta(s2.getTheta() - s.getTheta());
		s2.rotateAroundLine(-s.getPhi());
		return s2;
	}
	
	public Point2D project(SpherePoint centerOfPlane) {
		SpherePoint relative = this.getRelative(centerOfPlane);
		relative.updatePoint();
		return new Point2D(relative.point.y, relative.point.z);
	}
	
	public SpherePoint clone() {
		return new SpherePoint(theta,phi);
	}

	private void rotateAroundLine(double howFar) { // around line through (pi/2,0) and (3pi/2,0)
		double xzDistance = Math.hypot(point.x, point.z);
		double xzAngle = Math.atan2(point.z,point.x);
		Point3D newPoint = new Point3D(xzDistance*cos(xzAngle+howFar),point.y,xzDistance*sin(xzAngle+howFar));
		this.setPoint(newPoint);
		//System.out.println(howFar+": "+start.point+" -> "+this.point);
	}
	
	private static double cos(double d) {
		return Math.cos(d);
	}

	public static double cos(SpherePoint p, SpherePoint q) {
		return cos(p.getPhi())*cos(q.getPhi())*cos(p.getTheta()-q.getTheta()) + sin(p.getPhi())*sin(q.getPhi());
	}
	
	public String toString() {
		return String.format("(%.4f,%.4f)",theta,phi);
	}
	
	/*public boolean equals(Object o) {
		return (o instanceof SpherePoint) && ((SpherePoint)o).id == id;
	}
	
	public int hashCode() {
		return id;
	}*/
}
