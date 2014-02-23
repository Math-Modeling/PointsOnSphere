package net.clonecomputers.lab.sphere.testsolution;

import static java.lang.Math.*;

import java.util.*;

import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.render.*;

public class RigidTester {

	public static void main(String[] args) {
		System.out.println(RigidTester.isRigid(Arrays.asList(
				//new SpherePoint(0,0),
				new SpherePoint(PI/2,0),
				new SpherePoint(PI,0),
				new SpherePoint(-PI/2,0),
				new SpherePoint(0,PI/2),
				new SpherePoint(0,-PI/2)
		)));
	}
	
	public static List<ConnectedPoint> makeConnections(List<SpherePoint> sPoints) {
		List<ConnectedPoint> points = new ArrayList<ConnectedPoint>();
		for(SpherePoint p: sPoints) {
			points.add(new ConnectedPoint(p));
		}
		
		double maxCos = -1;
		for(SpherePoint p: points) {
			for(SpherePoint p2: points) {
				if(p.equals(p2)) continue;
				double a = SpherePoint.cos(p, p2);
				if(a > maxCos) maxCos = a;
			}
		}
		
		for(ConnectedPoint p: points) {
			for(ConnectedPoint p2: points) {
				if(p2.connections.contains(p)) {
					p.connections.add(p2);
					continue;
				}
				if(p.equals(p2)) continue;
				double cos = SpherePoint.cos(p, p2);
				if(abs(cos-maxCos) < .001) p.connections.add(p2); // protect against FPE's
			}
		}
		return points;
	}
	
	public static boolean isRigid(List<SpherePoint> sPoints) { // invalid if angle is less than 90°
		List<ConnectedPoint> points = makeConnections(sPoints);
		Renderer r = new Renderer(600, 600);
		for(ConnectedPoint p: points) {
			r.addPoint(p);
			for(ConnectedPoint p2: p.connections) {
				r.addLine(p,p2);
			}
		}
		for(ConnectedPoint p: points) {
			if(!ConvexHullTester.isInsideConvexHull(p.connections, p)){
				System.out.printf("%s is bad\n", p);
				return false;
			}
		}
		return true;
	}

}
