package net.clonecomputers.lab.sphere.testsolution;

import static java.lang.Math.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.commons.csv.*;

import net.clonecomputers.lab.sphere.*;
import net.clonecomputers.lab.sphere.render.*;
import net.clonecomputers.lab.sphere.render.Renderer;

public class RigidTester {

	public static void main(String[] args) {
		System.out.println(RigidTester.isRigid(Arrays.asList(
				new SpherePoint(1.6850,-0.6241), 
				new SpherePoint(-1.2746,-0.4696), 
				new SpherePoint(-1.4565,0.6242), 
				new SpherePoint(-2.7536,-1.1577), 
				new SpherePoint(2.8023,-0.1660), 
				new SpherePoint(-2.3668,-0.0838), 
				new SpherePoint(0.1180,-0.8663), 
				new SpherePoint(0.3878,1.1576), 
				new SpherePoint(1.8670,0.4698), 
				new SpherePoint(-3.0235,0.8663), 
				new SpherePoint(0.7749,0.0838), 
				new SpherePoint(-0.3393,0.1660)
				)));
	}

	public static List<ConnectedPoint> makeConnections(Collection<SpherePoint> sPoints) {
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
				if(p.equals(p2)) continue;
				double cos = SpherePoint.cos(p, p2);
				if(abs(cos-maxCos) < .005) { // protect against FPE's
					p.connections.add(p2);
					p2.connections.add(p);
				}
			}
		}
		//for(ConnectedPoint p: points) p.display();
		return points;
	}

	public static boolean isRigid(Collection<SpherePoint> sPoints) { // invalid if angle is less than 90°
		List<ConnectedPoint> points = makeConnections(sPoints);
		JFileChooser fc = new JFileChooser();
		int retval = fc.showSaveDialog(null);
		File f = fc.getSelectedFile();
		if(!(f == null || retval != JFileChooser.APPROVE_OPTION)) {
			if(!f.getName().endsWith(".csv")) f = new File(f.getAbsolutePath()+".csv");
			CSVPrinter csv = null;
			try {
				csv = new CSVPrinter(new BufferedWriter(new FileWriter(f)), CSVFormat.EXCEL);
				for(ConnectedPoint p: points) {
					for(ConnectedPoint p2: points) {
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
				try {
					csv.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		Renderer r = new Renderer(600, 600);
		for(ConnectedPoint p: points) {
			r.addPoint(p,false);
			for(ConnectedPoint p2: p.connections) {
				r.addLine(p,p2,Color.BLACK, false);
			}
		}
		boolean good = true;
		for(ConnectedPoint p: points) {
			if(!ConvexHullTester.isInsideConvexHull(p.connections, p)){
				//System.out.printf("%s is bad\n", p);
				r.addPoint(p,Color.RED,false);
				good = false;
			}
		}
		r.updateDisplay();
		return good;
	}

}
