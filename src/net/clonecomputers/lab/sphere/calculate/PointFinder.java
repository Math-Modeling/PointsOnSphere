package net.clonecomputers.lab.sphere.calculate;

import static java.lang.Math.*;
import static net.clonecomputers.lab.sphere.Point3D.*;

import java.io.*;

import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.render.*;

public class PointFinder {
	private int numPoints;
	private Renderer r;

	public static void main(String[] args) {
		new PointFinder().run();
	}

	private void run() {
		System.out.println("How many points?");
		try {
			String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
			numPoints = Integer.parseInt(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NumberFormatException e) {
			System.err.println("Not a number");
			return;
		}
		r = new Renderer(600,600);
		while(true) {
			r.removeAllPoints();
			for(int i = 0; i < numPoints; i++) {
				r.addPoint(new SpherePoint(random()*2-1,random()*2-1,random()*2-1), false);
			}
			r.updateDisplay();
			optimizePoints();
			System.out.println("done");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void optimizePoints() {
		SpherePoint[] points = r.getAllPoints();
		for(int step = 20; step < 1000; step+=2) {
			for(SpherePoint p: points) {
				double maxCosSoFar = -1;
				SpherePoint closestPoint = null;
				for(SpherePoint q: points) {
					if(q.equals(p)) continue;
					double thisCos = cos(p,q);
					if(thisCos >= maxCosSoFar){
						closestPoint = q;
						maxCosSoFar = thisCos;
					}
				}
				if(closestPoint != null) moveApart(p,closestPoint,1.0/step);
			}
			r.updateDisplay();
			Thread.yield();
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}*/
		}
	}

	private static double cos(SpherePoint p, SpherePoint q) {
		return cos(p.getPhi())*cos(q.getPhi())*cos(p.getTheta()-q.getTheta()) + sin(p.getPhi())*sin(q.getPhi());
	}
	
	private static double cos(double theta){
		return Math.cos(theta);
	}

	private static void moveApart(SpherePoint p, SpherePoint q, double theta) {
		//Renderer r = new Renderer(600,600);
		//r.addPoint(p(p.getPoint()));
		//r.addPoint(p(q.getPoint()));
		//System.out.printf("moving %d away from %d\n",p.id,q.id);
		Point3D pp = p.getPoint();
		Point3D qp = q.getPoint();
		/*Point3D eulerRotationVector = new Point3D(
			howFar / sqrt(1 + pow((x1*z2 - x2*z1)/(y1*z2 - y2*z1),2) + pow((x1*y2 - x2*y1)/(z1*y2 - z2*y1),2)),
			howFar / sqrt(1 + pow((y1*x2 - y2*x1)/(z1*x2 - z2*x1),2) + pow((y1*z2 - y2*z1)/(x1*z2 - x2*z1),2)),
			howFar / sqrt(1 + pow((z1*y2 - z2*y1)/(x1*y2 - x2*y1),2) + pow((z1*x2 - z2*x1)/(y1*x2 - y2*x1),2))
		);*/
		Point3D unitAxis = normalize(cross(pp,qp));
		//r.addPoint(p(unitAxis),CYAN);
		//r.addPoint(p(product(unitAxis,-1)),CYAN);
		Point3D pRot1 = sum(product(pp,cos(theta)),
						   product(cross(unitAxis,pp),sin(theta)),
						   product(pp,dot(unitAxis,pp)*(1-cos(theta)))
					   );
		Point3D pRot2 = sum(product(pp,cos(-theta)),
				   			product(cross(unitAxis,pp),sin(-theta)),
				   			product(pp,dot(unitAxis,pp)*(1-cos(-theta)))
						);
		/*Point3D qRot1 = sum(product(qp,cos(theta)),
				   		   product(cross(unitAxis,qp),sin(theta)),
				   		   product(qp,dot(unitAxis,qp)*(1-cos(theta)))
					   );
		Point3D qRot2 = sum(product(qp,cos(-theta)),
							product(cross(unitAxis,qp),sin(-theta)),
							product(qp,dot(unitAxis,qp)*(1-cos(-theta)))
						);*/
		if(dist(qp,pRot1) > dist(qp,pRot2)) {
			p.setPoint(pRot1);
		} else {
			p.setPoint(pRot2);
		}
		/*if(dist(pp,qRot1) > dist(pp,qRot2)) {
			q.setPoint(qRot1);
		} else {
			q.setPoint(qRot2);
		}*/
		//r.addPoint(p(p.getPoint()),RED);
		//r.addPoint(p(q.getPoint()),RED);
	}

}
