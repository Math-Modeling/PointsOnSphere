package net.clonecomputers.lab.sphere.render;

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import net.clonecomputers.lab.sphere.*;

public class Renderer extends JPanel {
	private BufferedImage canvas;
	private Set<SpherePoint> points;
	private double zoom = .8;
	private double pointSize = .03;
	private SpherePoint viewAngle = new SpherePoint(0,0);
	
	public static void main(String[] args) { // for testing only
		Renderer r = new Renderer(600,600);
		r.addPoint(new SpherePoint(0,PI/2));
		r.addPoint(new SpherePoint(0,-PI/2));
		r.addPoint(new SpherePoint(0,0));
		r.addPoint(new SpherePoint(2*PI/3,0));
		r.addPoint(new SpherePoint(4*PI/3,0));
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
		MouseInputListener listener = new MouseInputAdapter() {
			Point lastPoint;
			
			@Override
			public void mousePressed(MouseEvent e) {
				lastPoint = e.getPoint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				viewAngle.setTheta(viewAngle.getTheta() - .01*(e.getPoint().x - lastPoint.x));
				viewAngle.setPhi(viewAngle.getPhi() - .01*(e.getPoint().y - lastPoint.y));
				if(viewAngle.getPhi() > PI/2) viewAngle.setPhi(PI/2);
				if(viewAngle.getPhi() < -PI/2) viewAngle.setPhi(-PI/2);
				lastPoint = e.getPoint();
				Renderer.this.updateDisplay();
			}
		};
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
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
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(SpherePoint raw: points) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			drawCircle(p.getPoint().y,p.getPoint().z,pointSize,Color.BLUE,g);
		}
		drawCircle(0,0,1-pointSize,new Color(1,1,1,.6f),g);
		for(SpherePoint raw: points) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			if(cos(p.getTheta()) > 0){
				drawCircle(p.getPoint().y,p.getPoint().z,pointSize,Color.BLUE,g);
			}
		}
		this.repaint();
	}

	private int gs(double s) { // length of line converted to graphics units
		return (int)round(s*zoom*(canvas.getWidth()+canvas.getHeight())/2);
	}
	
	private int gsx(double s) { // length of line converted to graphics units
		return (int)round(s*zoom*canvas.getWidth()/2);
	}
	
	private int gsy(double s) { // length of line converted to graphics units
		return (int)round(s*zoom*canvas.getHeight()/2);
	}
	
	private int gx(double x) { // location of x value converted to graphics coordinates
		return (int)round((x*zoom*canvas.getWidth()/2) + (canvas.getWidth()/2));
	}
	
	private int gy(double y) { // location of y value converted to graphics coordinates
		return (int)round(-(y*zoom*canvas.getHeight()/2) + (canvas.getHeight()/2));
	}
	
	private void drawCircle(double x, double y, double r, Color c, Graphics g) {
		g.setColor(c);
		g.fillOval(gx(x-r), gy(y+r), gsx(2*r), gsy(2*r));
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(canvas, 0, 0, this);
	}
}
