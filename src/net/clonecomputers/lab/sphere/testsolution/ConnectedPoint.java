package net.clonecomputers.lab.sphere.testsolution;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.clonecomputers.lab.sphere.*;

public class ConnectedPoint extends SpherePoint{
	public final Set<ConnectedPoint> connections;

	public ConnectedPoint(SpherePoint point) {
		super(point);
		this.connections = new HashSet<ConnectedPoint>();
	}
	
	public String toSimpleString() {
		//return String.format("(%.2f,%.2f,%.2f)",getPoint().x,getPoint().y,getPoint().z);
		return super.toString();
	}
	
	public void display() {
		JFrame window = new JFrame("points");
		window.setContentPane(new JPanel() {
			private int xgp(double x) {
				return getWidth()/2 + (int)(getWidth()*x/2);
			}
			private int ygp(double y) {
				return getHeight()/2 - (int)(getHeight()*y/2);
			}
			@Override public void paintComponent(Graphics g) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.BLACK);
				g.fillOval(xgp(0)-5, ygp(0)-5, 10, 10);
				for(ConnectedPoint point: connections) {
					Point2D p = point.project(ConnectedPoint.this);
					g.drawOval(xgp(p.x)-5, ygp(p.y)-5, 10, 10);
					g.drawLine(xgp(p.x)-5, ygp(p.y)-5, xgp(0), ygp(0));
				}
			}
		});
		window.setSize(600,600);
		window.setVisible(true);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(toSimpleString());
		sb.append(": [");
		for(ConnectedPoint p: connections) {
			sb.append(p.toSimpleString());
			sb.append(", ");
		}
		sb.delete(sb.length()-3, sb.length());
		sb.append("]");
		return sb.toString();
	}
}
