package net.clonecomputers.lab.sphere.render;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import net.clonecomputers.lab.sphere.*;

public class Renderer extends JPanel {
	private BufferedImage canvas;
	private Set<SpherePoint> points;
	private double zoom = .8;
	
	public static void main(String[] args) { // for testing only
		Renderer r = new Renderer(500,500);
		r.updateDisplay();
	}
	
	public Renderer(int width, int height) {
		this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.points = new HashSet<SpherePoint>();
		this.setMaximumSize(new Dimension(width,height));
		this.setMinimumSize(new Dimension(width,height));
		this.setPreferredSize(new Dimension(width,height));
		JFrame window = new JFrame();
		window.setSize(width,height);
		window.add(this);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	public void addPoint(SpherePoint p) {
		addPoint(p, true);
	}
	
	public void addPoint(SpherePoint p, boolean update) {
		points.add(p);
		if(update) updateDisplay();
	}
	
	public void removePoint(SpherePoint p) {
		removePoint(p, true);
	}
	
	public void removePoint(SpherePoint p, boolean update) {
		points.remove(p);
		if(update) updateDisplay();
	}
	
	public void updateDisplay() {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		drawCircle(0,0,1,new Color(0f,0f,0f,.5f),g);
		// TODO: draw stuff here
		this.repaint();
	}
	
	private int gs(double s) { // length of line converted to graphics units
		return (int)Math.round(s*zoom*(canvas.getWidth()+canvas.getHeight())/2);
	}
	
	private int gsx(double s) { // length of line converted to graphics units
		return (int)Math.round(s*zoom*canvas.getWidth()/2);
	}
	
	private int gsy(double s) { // length of line converted to graphics units
		return (int)Math.round(s*zoom*canvas.getHeight()/2);
	}
	
	private int gx(double x) { // location of x value converted to graphics coordinates
		return (int)Math.round((x*zoom*canvas.getWidth()/2) + (canvas.getWidth()/2));
	}
	
	private int gy(double y) { // location of y value converted to graphics coordinates
		return (int)Math.round((y*zoom*canvas.getHeight()/2) + (canvas.getHeight()/2));
	}
	
	private void drawCircle(double x, double y, double r, Color c, Graphics g) {
		g.setColor(c);
		g.fillOval(gx(x-r), gy(y-r), gsx(2*r), gsy(2*r));
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(canvas, 0, 0, Color.WHITE, this);
	}
}
