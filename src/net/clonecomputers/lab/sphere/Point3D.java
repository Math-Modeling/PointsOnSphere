package net.clonecomputers.lab.sphere;

import static java.lang.Math.*;

public class Point3D implements Comparable<Point3D> {
	public final double x;
	public final double y;
	public final double z;
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return String.format("(%.2f,%.2f,%.2f)", x,y,z);
	}
	
	public static double dot(Point3D... points) {
		double x=1,y=1,z=1;
		for(Point3D p: points) {
			x *= p.x;
			y *= p.y;
			z *= p.z;
		}
		return x+y+z;
	}
	
	public static Point3D cross(Point3D a, Point3D b) {
		return new Point3D(
			a.y*b.z - b.y*a.z,
			a.z*b.x - b.z*a.x,
			a.x*b.y - b.x*a.y
		);
	}
	
	public static Point3D sum(Point3D... points) {
		double x=0,y=0,z=0;
		for(Point3D p: points) {
			x += p.x;
			y += p.y;
			z += p.z;
		}
		return new Point3D(x,y,z);
	}

	public static Point3D diff(Point3D a, Point3D b) {
		return sum(a,prod(b,-1));
	}
	
	public static double abs(Point3D a) {
		return sqrt(dot(a,a));
	}
	
	public static Point3D norm(Point3D a) {
		return prod(a,1/abs(a));
	}
	
	public static Point3D prod(Point3D a, double b) {
		return new Point3D(
			a.x * b,
			a.y * b,
			a.z * b
		);
	}
	
	public static double dist(Point3D a, Point3D b) {
		return abs(diff(a,b));
	}
	
	public static SpherePoint p(Point3D a) {
		return new SpherePoint(a);
	}
	
	public boolean equals(Object o) {
		return o instanceof Point3D && o != null && ((Point3D)o).x == x && ((Point3D)o).y == y && ((Point3D)o).z == z;
	}
	
	public int hashCode() {
		return new Double(x).hashCode() ^ new Double(y).hashCode() ^ new Double(z).hashCode();
	}
	
	public int compareTo(Point3D p) {
		return this.equals(p)?0:this.hashCode() > p.hashCode()?1:-1;
	}
}