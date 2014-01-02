package net.clonecomputers.lab.sphere.calculate;

import static java.lang.Math.*;
import static net.clonecomputers.lab.sphere.Point3D.*;

import java.awt.*;

import javax.swing.*;

import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.render.Renderer;

public class PointFinder {
	private int numPoints = 0;
	private Renderer r;

	public static void main(String[] args) {
		new PointFinder().run();
	}

	private void run() {
		boolean done = false;
		while(!done) {
			try {
				String s = JOptionPane.showInputDialog("How many points?");
				if(s == null) System.exit(0); // canceled
				numPoints = Integer.parseInt(s);
				done = true;
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Not a number", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		r = new Renderer(600,600);
		r.addPoint(new SpherePoint(0, -PI/2), new PointProperties(Color.GREEN, false), false);
		r.addPoint(new SpherePoint(0, PI/2), new PointProperties(Color.GREEN, false), false);
		for(int i = 0; i < numPoints; i++) {
			double z = random()*2-1;
			double phi = random()*2*PI;
			r.addPoint(new SpherePoint(sqrt(1-z*z)*cos(phi), sqrt(1-z*z)*sin(phi), z), false);
		}
		r.updateDisplay();
		optimizePoints();
	}

	private void optimizePoints() {
		SpherePoint[] points = r.getAllPoints();
		for(int step = 20; true; step++) {
			shuffle(points);
			double score = -Double.MAX_VALUE;
			for(SpherePoint p: points) {
				double maxCosSoFar = -1;
				SpherePoint closestPoint = null;
				for(SpherePoint q: points) {
					if(q.equals(p)) continue;
					double thisCos = cos(p,q);
					if(thisCos >= maxCosSoFar){
						closestPoint = q;
						maxCosSoFar = thisCos;
						if(maxCosSoFar > score) score = maxCosSoFar;
					}
				}
				if(closestPoint != null) moveApart(p,closestPoint,1.0/step);
			}
			r.updateDisplay();
			System.out.println(score);
			Thread.yield();
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}*/
		}
	}

	private static <T> void shuffle(T[] a) {
		T temp = null;
		for(int i = a.length - 1; i >= 0; i--) {
			int j = (int)random()*i;
			temp = a[i];
			a[i] = a[j];
			a[j] = temp;
		}
	}

	private static double cos(SpherePoint p, SpherePoint q) {
		return cos(p.getPhi())*cos(q.getPhi())*cos(p.getTheta()-q.getTheta()) + sin(p.getPhi())*sin(q.getPhi());
	}

	private static double cos(double theta){ // here so that I can still access Math.cos as cos
		return Math.cos(theta);				 // despite also defining my own cos method for 2 SpherePoints
	}

	private void moveApart(SpherePoint p, SpherePoint q, double theta) {
		r.addPoint(p.clone(), new PointProperties(Color.RED, false, .5), false);
		r.addPoint(q.clone(), new PointProperties(Color.RED, false, .5), false);
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
