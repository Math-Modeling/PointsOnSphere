package net.clonecomputers.lab.sphere.testsolution;

import static net.clonecomputers.lab.sphere.Point2D.*;

import java.util.*;

import net.clonecomputers.lab.sphere.*;

public class ConvexHullTester {
	private static final boolean D = false;
	public static List<Point2D> getConvexHull(List<Point2D> points) {
		List<Point2D> path = new ArrayList<Point2D>();
		Point2D p = points.get(0);
		int i = 0;
		while(!path.contains(p)) {
			Point2D ref = getDistinct(points, p);
			double maxCross = -Double.MAX_VALUE;
			Point2D nextPoint = null;
			for(Point2D p2: points) {
				if(p.equals(p2)) continue;
				if(crossAgainstRef(p,p2,ref) > maxCross) {
					maxCross = crossAgainstRef(p,p2,ref);
					nextPoint = p2;
				}
			}
			path.add(p);
			p = nextPoint;
		}
		path.subList(0, path.lastIndexOf(p)).clear();
		return path;
	}
	
	public static double crossAgainstRef(Point2D p, Point2D p2, Point2D ref) {
		return p(cross(norm(diff(p2,p)), norm(diff(ref,p))),D?"%4$s against [%2$s, %3$s] is %1$.2f\n":"",p,p2,ref);
	}
	
	public static <T> T p(T t, String format, Object... objs) {
		Object[] args = new Object[objs.length+1];
		args[0] = t;
		for(int i = 1; i < args.length; i++) {
			args[i] = objs[i-1];
		}
		System.out.printf(format, args);
		return t;
	}
	
	public static <T> T getDistinct(List<T> list, T... items) {
		T t = list.get(0);
		for(int i = 0; contains(items,t); i++) {
			t = list.get(i);
		}
		return t;
	}
	
	public static <T> boolean contains(T[] list, T item) {
		for(T t: list) {
			if(t.equals(item)) return true;
		}
		return false;
	}
	
	public static boolean isInsideConvexHull(List<Point2D> points, Point2D center) {
		Point2D p = points.get(points.size()-1);
		for(Point2D p2: p(getConvexHull(points),D?"%s\n":"")) {
			if(crossAgainstRef(p,p2,center) <= 0) {
				return false;
			}
			p = p2;
		}
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(
			isInsideConvexHull(new ArrayList<Point2D>(Arrays.asList(new Point2D[]{
				new Point2D(1,1),
				new Point2D(2,2),
				new Point2D(1,2),
				new Point2D(0,3),
				new Point2D(-2,2),
				new Point2D(3,-1),
				new Point2D(-5,-5),
		})), new Point2D(-4,-4)));
	}
}
