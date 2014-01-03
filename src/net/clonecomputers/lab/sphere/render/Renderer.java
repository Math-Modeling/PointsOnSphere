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
	private Map<SpherePoint,PointProperties> points;
	private double zoom = .8;
	private double pointSize = .03;
	private SpherePoint viewAngle = new SpherePoint(0,0);
	
	public static void main(String[] args) { // for testing only
		Renderer r = new Renderer(600,600);
		/*r.addPoint(new SpherePoint(0,PI/2));
		r.addPoint(new SpherePoint(0,-PI/2));
		r.addPoint(new SpherePoint(0,0));
		r.addPoint(new SpherePoint(2*PI/3,0));
		r.addPoint(new SpherePoint(4*PI/3,0));*/
		for(int i = 0; i < 100; i++) {
		r.addPoint(new SpherePoint(),
				new PointProperties(new Color((float)random(),(float)random(),(float)random()),random()*2),
				false);
		}
		r.updateDisplay();
	}
	
	public Renderer(int width, int height) {
		this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.points = new HashMap<SpherePoint,PointProperties>();
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
		this.updateDisplay();
	}
	
	public void addPoint(SpherePoint p) {
		addPoint(p, true);
	}
	
	public void addPoint(SpherePoint p, boolean update) {
		addPoint(p, Color.BLUE, update);
	}
	
	public void addPoint(SpherePoint p, Color c) {
		addPoint(p, c, true);
	}
	
	public void addPoint(SpherePoint p, Color c, boolean update) {
		addPoint(p,new PointProperties(c),update);
	}
	
	public void addPoint(SpherePoint p, PointProperties properties, boolean update) {
		synchronized(this) {
			points.put(p, properties);
		}
		if(update){
			updateDisplay();
		}
	}

	public void removePoint(SpherePoint p) {
		removePoint(p, true);
	}
	
	public void removePoint(SpherePoint p, boolean update) {
		synchronized(this) {
			points.remove(p);
		}
		if(update){
			updateDisplay();
		}
	}
	
	private SpherePoint[] getPointsByDepth() {
		SpherePoint[] pointsByDepth = points.keySet().toArray(new SpherePoint[0]);
		Arrays.sort(pointsByDepth, new Comparator<SpherePoint>(){
			@Override
			public int compare(SpherePoint p1, SpherePoint p2) {
				return (int)signum(
					p1.getRelative(viewAngle.clone()).getPoint().x - 
					p2.getRelative(viewAngle.clone()).getPoint().x);
			}
		});
		return pointsByDepth;
	}
	
	public synchronized void updateDisplay() {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		SpherePoint[] pointsByDepth = getPointsByDepth();
		boolean drawnBigSphere = false;
		for(SpherePoint raw: pointsByDepth) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			PointProperties props = points.get(raw);
			if(!drawnBigSphere && p.getPoint().x >= 0) {
				drawCircle(0,0,1,new Color(1,1,1,.6f),g);
				drawnBigSphere = true;
			}
			drawCircle(
				p.getPoint().y * (props.pointSize+1),
				p.getPoint().z * (props.pointSize+1),
				props.pointSize, props.color, g);
		}
		
		/*for(SpherePoint raw: points.keySet()) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			PointProperties props = points.get(raw);
			if(p.getPoint().x <= 0){
				drawCircle(
					p.getPoint().y * (props.pointSize+1),
					p.getPoint().z * (props.pointSize+1),
					props.pointSize, props.color, g);
			}
		}
		drawCircle(0,0,1,new Color(1,1,1,.6f),g);
		for(SpherePoint raw: points.keySet()) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			PointProperties props = points.get(raw);
			if(p.getPoint().x > 0){
				drawCircle(
					p.getPoint().y * (props.pointSize+1),
					p.getPoint().z * (props.pointSize+1),
					props.pointSize, props.color, g);
			}
		}*/
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
	
	@Override public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(canvas, 0, 0, this);
	}

	public synchronized void removeAllPoints() {
		points.clear();
	}
	
	public SpherePoint[] getAllPoints() {
		Set<SpherePoint> allPoints = new HashSet<SpherePoint>(points.keySet());
		for(SpherePoint p: points.keySet()) {
			if(!points.get(p).shouldBeMoved) allPoints.remove(p);
		}
		return allPoints.toArray(new SpherePoint[0]);
	}
}
