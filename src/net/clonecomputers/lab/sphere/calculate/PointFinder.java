package net.clonecomputers.lab.sphere.calculate;

import static java.lang.Math.*;
import static net.clonecomputers.lab.sphere.Point3D.*;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.clonecomputers.lab.graph.*;
import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.render.Renderer;
import net.clonecomputers.lab.sphere.testsolution.*;

public class PointFinder {
	private int numPoints = 0;
	private Renderer r;
	private Grapher g;

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
		g = new Grapher();
		JFrame f = new JFrame("PointsOnSphere "+numPoints);
		f.setContentPane(g);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setSize(600,600);
		f.setVisible(true);
		r = new Renderer(600,600);
		//r.addPoint(new SpherePoint(0, -PI/2), new PointProperties(Color.GREEN, false), false);
		//r.addPoint(new SpherePoint(0, PI/2), new PointProperties(Color.GREEN, false), false);
		//r.removeAllPoints();
		for(int i = 0; i < numPoints; i++) {
			r.addPoint(new SpherePoint(true), new PointProperties(
					Color.BLUE,//new Color((float)random(),(float)random(),(float)random()).brighter(),
					Color.RED//new Color((float)random(),(float)random(),(float)random()).darker()),
					),false);
		}
		r.updateDisplay();
		SpherePoint[] optimized = optimizePoints();
		System.out.println(Arrays.toString(optimized));
		RigidTester.isRigid(Arrays.asList(optimized));
	}

	private SpherePoint[] optimizePoints() {
		SpherePoint[] points = r.getAllPoints();
		for(int step = 20; step < 40000; step++) {
			shuffle(points);
			double score = -Double.MAX_VALUE;
			for(SpherePoint p: points) {
				double maxCosSoFar = -1;
				SpherePoint closestPoint = null;
				for(SpherePoint q: points) {
					if(q.equals(p)) continue;
					double thisCos = SpherePoint.cos(p,q);
					if(thisCos >= maxCosSoFar){
						closestPoint = q;
						maxCosSoFar = thisCos;
						if(maxCosSoFar > score) score = maxCosSoFar;
					}
				}
				if(closestPoint != null) moveApart(p,closestPoint,1.0/step);
			}
			g.addToGraph(step, score);
			if(step%500 == 0) {
				r.setMaxCos(score);
				r.updateDisplay();
				System.out.println(score);
				g.updateChart();
			}
		}
		g.updateChart();
		return points;
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

	private static void moveApart(SpherePoint p, SpherePoint q, double theta) {
		//r.addPoint(p.clone(), new PointProperties(Color.RED, false, .5), false);
		//r.addPoint(q.clone(), new PointProperties(Color.RED, false, .5), false);
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
		Point3D unitAxis = norm(cross(pp,qp));
		//r.addPoint(p(unitAxis),CYAN);
		//r.addPoint(p(product(unitAxis,-1)),CYAN);
		Point3D pRot1 = sum(prod(pp,cos(theta)),
				prod(cross(unitAxis,pp),sin(theta)),
				prod(pp,dot(unitAxis,pp)*(1-cos(theta)))
				);
		Point3D pRot2 = sum(prod(pp,cos(-theta)),
				prod(cross(unitAxis,pp),sin(-theta)),
				prod(pp,dot(unitAxis,pp)*(1-cos(-theta)))
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
