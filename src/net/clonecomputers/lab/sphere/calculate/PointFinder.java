package net.clonecomputers.lab.sphere.calculate;

import static java.lang.Math.*;
import static net.clonecomputers.lab.sphere.Point3D.*;

import java.awt.*;
import java.util.List;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

import org.apache.commons.csv.*;

import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.graph.*;
import net.clonecomputers.lab.sphere.render.Renderer;
import net.clonecomputers.lab.sphere.testsolution.*;

public class PointFinder {
	private int numPoints = 0;
	private Renderer r;
	private Grapher g;
	private PrintWriter outputCSV;

	static volatile int x;
	static volatile boolean shouldStop = false;
	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			new PointFinder().run();
			return;
		}
		System.out.println("type 'stop' to stop");
		File saveDir = new File(System.getProperty("user.home"), "points on sphere");
		ExecutorService exec = Executors.newFixedThreadPool(9);
		exec.execute(new Runnable() {
			@Override public void run() {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				while(true) {
					try {
						String s = in.readLine();
						if(s != null && s.equalsIgnoreCase("stop")) {
							shouldStop = true;
							break;
						}
					} catch (IOException e) {
						//if(e.getMessage().equals("Stream closed")) break; // expected (not any more)
						throw new RuntimeException(e);
					}
					Thread.yield();
				}
			}
		});
		PointFinder useless = new PointFinder();
		int i;
		outer:
		for(i = 1; true; i++) {
			x++;
			exec.execute(useless.new PointRunner(i,saveDir));
			do {
				if(shouldStop) break outer;
				Thread.yield();
			} while(x > 9);
		}
		System.out.println("waiting for tasks to terminate (started "+i+")");
		//System.in.close();
		exec.shutdown();
	}
	
	private class PointRunner implements Runnable {
		final int i;
		final File saveDir;
		private PointRunner(int i, File saveDir) {
			this.i = i;
			this.saveDir = saveDir;
		}
		
		@Override public void run() {
			try {
				new PointFinder().run(i, saveDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println(i+" done");
			x--;
		}
	}
	
	private void run(int nPoints, File saveDir) throws IOException {
		saveDir.mkdirs();
		File value = new File(saveDir, nPoints+"-value.csv");
		value.createNewFile();
		File adjacency = new File(saveDir, nPoints+"-adjacency.csv");
		adjacency.createNewFile();
		
		outputCSV = new PrintWriter(value);
		
		SpherePoint[] points = new SpherePoint[nPoints];
		for(int i = 0; i < points.length; i++) {
			points[i] = new SpherePoint(false);
		}
		points = optimizePoints(points);
		List<ConnectedPoint> cPoints = RigidTester.makeConnections(Arrays.asList(points));
		CSVPrinter csv = null;
		try {
			csv = new CSVPrinter(new BufferedWriter(new FileWriter(adjacency)), CSVFormat.EXCEL);
			for(ConnectedPoint p: cPoints) {
				for(ConnectedPoint p2: cPoints) {
					if(p.connections.contains(p2)) {
						csv.print(1);
					} else {
						csv.print(0);
					}
				}
				csv.println();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			csv.close();
		}
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
		
		/*JFileChooser fc = new JFileChooser();
		int retval = fc.showSaveDialog(null);
		File f = fc.getSelectedFile();
		if(f != null && !f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath()+".csv");
		if(f == null || retval != JFileChooser.APPROVE_OPTION) {
			outputCSV = new PrintWriter(new OutputStream(){
				@Override
				public void write(int arg0) throws IOException {
					// do nothing
				}
			});
		} else {
			try {
				f.createNewFile();
				outputCSV = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}*/
		/*g = new Grapher();
		JFrame gWindow = new JFrame("PointsOnSphere "+numPoints);
		gWindow.setContentPane(g);
		gWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gWindow.pack();
		gWindow.setSize(600,600);
		gWindow.setVisible(true);*/
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
		SpherePoint[] optimized = optimizePoints(r.getAllPoints());
		System.out.println(Arrays.toString(optimized));
		RigidTester.isRigid(Arrays.asList(optimized));
	}

	private SpherePoint[] optimizePoints(SpherePoint[] points) {
		for(int step = 20; step < 100000; step++) {
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
			if(g != null) g.addToGraph(step, score);
			if(outputCSV != null) outputCSV.println(score);
			if(step%500 == 0) {
				if(r != null) r.setMaxCos(score);
				if(r != null) r.updateDisplay();
				if(r != null || g != null) System.out.println(score);
				if(g != null) g.updateChart();
			}
		}
		if(g != null) g.updateChart();
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
