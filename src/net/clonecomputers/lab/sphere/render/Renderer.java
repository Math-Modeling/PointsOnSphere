package net.clonecomputers.lab.sphere.render;

import static java.lang.Math.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import net.clonecomputers.lab.sphere.*;

public class Renderer extends JPanel {
	private BufferedImage canvas;
	private BufferedImage background;
	private BufferedImage testPixel;
	Area clippingCircle;
	private Map<SpherePoint,PointProperties> points;
	private List<Line> lines;
	private double zoom = .8;
	private double pointSize = .03;
	private SpherePoint viewAngle = new SpherePoint(-PI/6,-PI/7);
	private double maxCos;
	
	public static void main(String[] args) { // for testing only
		Renderer r = new Renderer(600,600);
		List<SpherePoint> pts = new ArrayList<SpherePoint>();
		for(int i = 0; i < 100; i++) {
			pts.add(new SpherePoint());
			r.addPoint(pts.get(pts.size()-1),
				new PointProperties(new Color((float)random(),(float)random(),(float)random()),random()*2),
				false);
		}
		for(int i = 0; i < 100; i++) {
			r.addLine(pts.get((int)(pts.size()*Math.random())), pts.get((int)(pts.size()*Math.random())), 
				new Color((float)random(),(float)random(),(float)random()), 
				false);
		}
		/*SpherePoint base = new SpherePoint(0,0);
		SpherePoint[] pts = new SpherePoint[]{
				new SpherePoint(PI/2,0),
				new SpherePoint(0,PI/2),
				new SpherePoint(0,-PI/2)
		};
		r.addPoint(base);
		r.addPoint(pts[0]);
		r.addPoint(pts[1]);
		r.addPoint(pts[2]);
		r.addLine(base,pts[0], Color.BLACK, false);
		r.addLine(base,pts[1], Color.BLACK, false);
		r.addLine(base,pts[2], Color.BLACK, false);*/
		
		r.updateDisplay();
	}
	
	public Renderer(int width, int height) {
		this(width, height, false);
	}
	
	public Renderer(int width, int height, boolean spin) {
		this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.testPixel = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		this.clippingCircle = new Area(new Ellipse2D.Float(gx(-1), gy(1), gsx(2), gsy(2)));
		this.points = new HashMap<SpherePoint,PointProperties>();
		this.lines = new ArrayList<Line>();
		this.setMaximumSize(new Dimension(width,height));
		this.setMinimumSize(new Dimension(width,height));
		this.setPreferredSize(new Dimension(width,height));
		this.drawBackground();
		JFrame window = new JFrame();
		window.setSize(width,height);
		window.add(this);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		if(spin) {
			new Thread(new Runnable() {
				public void run() {
					while(true) {
						viewAngle.setTheta(viewAngle.getTheta() - .004);
						Renderer.this.updateDisplay();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}).start();
		}
		MouseInputListener listener = new MouseInputAdapter() {
			Point lastPoint;
			
			@Override
			public void mousePressed(MouseEvent e) {
				lastPoint = e.getPoint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				viewAngle.setTheta(viewAngle.getTheta() -
						cos(viewAngle.getPhi()) *
						.01*(e.getPoint().x - lastPoint.x));
				viewAngle.setPhi(viewAngle.getPhi() + .01*(e.getPoint().y - lastPoint.y));
				//if(viewAngle.getPhi() > PI/2) viewAngle.setPhi(PI/2);
				//if(viewAngle.getPhi() < -PI/2) viewAngle.setPhi(-PI/2);
				lastPoint = e.getPoint();
				Renderer.this.updateDisplay();
			}

			private double mod(double a, double b) {
				return (a%b)+(signum(a)<0?b:0);
			}
		};
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.updateDisplay();
	}

	public void addLine(SpherePoint start, SpherePoint end) {
		addLine(start, end, Color.BLACK, false);
	}
	
	public void addLine(SpherePoint start, SpherePoint end, Color c, boolean update) {
		addLine(new Line(start, end, c), update);
	}
	
	public int p(int x) {
		System.out.println(x);
		return x;
	}
	
	public synchronized void addLine(Line line, boolean update) {
		/*SpherePoint traceReference = new SpherePoint(start.getTheta(),start.getPhi(),true);
		traceReference.setPoint(end.getPoint());
		this.addPoint(traceReference, new PointProperties(color,color,false,-1),update);
		return traceReference;*/
		//System.out.println(line);
		if(line.length() >= maxCos) {
			maxCos = line.length();
		}
		synchronized(this) {
			lines.add(/*p(abs(Arrays.binarySearch(lines.toArray(new Line[lines.size()]),line,new Comparator<Line>() {
				@Override
				public int compare(Line o1, Line o2) {
					double x1 = Point3D.prod(Point3D.sum(o1.start.getPoint(),o1.end.getPoint()), .5).x;
					double x2 = Point3D.prod(Point3D.sum(o2.start.getPoint(),o2.end.getPoint()), .5).x;
					return x1==x2?0:x1>x2?1:-1;
				}
			})))-1, */line);
		}
		if(update){
			updateDisplay();
		}
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
		Set<SpherePoint> allPoints = new HashSet<SpherePoint>(points.keySet());
		for(SpherePoint p: points.keySet()) {
			if(!p.hasTrace()) continue;
			for(SpherePoint p2: p.getTrace()) {
				allPoints.add(p2);
			}
		}
		SpherePoint[] pointsByDepth = allPoints.toArray(new SpherePoint[0]);
		Arrays.sort(pointsByDepth, new Comparator<SpherePoint>(){
			@Override
			public int compare(SpherePoint p1, SpherePoint p2) {
				PointProperties props1 = points.get(p1);
				PointProperties props2 = points.get(p2);
				double r1 = props1==null?0:props1.pointSize;
				double r2 = props2==null?0:props2.pointSize;
				return (int)signum(
					p1.getRelative(viewAngle.clone()).getPoint().x*(r1+1) - 
					p2.getRelative(viewAngle.clone()).getPoint().x*(r2+1));
			}
		});
		return pointsByDepth;
	}
	
	private Color getFadedColor(Color c, Point3D loc) {
		if(!clippingCircle.contains(gx(loc.y),gy(loc.z))) return c;
		//Graphics testPixelGraphics = testPixel.getGraphics();
		double sphereHeight = sqrt(1 - (loc.y*loc.y + loc.z*loc.z));
		double distance = sphereHeight - loc.x;
		float fade = (float)(.5*distance/2 + (distance>.001?.2:0));
		/*testPixel.setRGB(0, 0, c.getRGB());
		testPixelGraphics.setColor(new Color(1,1,1,(float)fade));
		testPixelGraphics.fillRect(0, 0, 1, 1);
		return new Color(testPixel.getRGB(0, 0),true);
		//int[] pixel = testPixel.getRaster().getPixel(0, 0, new int[4]);
		//return new Color(pixel[0],pixel[1],pixel[2],pixel[3]);*/
		float[] base = c.getComponents(new float[4]);
		return new Color(clamp(0,base[0]*(1-fade) + fade,1),
						 clamp(0,base[1]*(1-fade) + fade,1),
						 clamp(0,base[2]*(1-fade) + fade,1));
	}
	
	private static float clamp(float min, float f, float max) {
		return f<min? min: f>max? max: f;
	}

	private void drawBackground() {
		//Graphics2D g = background.createGraphics();
		//g.setColor(Color.DARK_GRAY);
		//g.fillRect(0, 0, background.getWidth(), background.getHeight());
		int[] pixels = new int[background.getWidth()*background.getHeight()];
		for(int x = 0; x < background.getWidth(); x++) {
			for(int y = 0; y < background.getHeight(); y++) {
				double ax = agx(x), ay = agy(y), az = -sqrt(1 - (ax*ax + ay*ay));
				pixels[x + y*background.getWidth()] = getFadedColor(Color.DARK_GRAY, new Point3D(az,ax,ay)).getRGB();
			}
		}
		background.setRGB(0, 0, background.getWidth(), background.getHeight(), pixels, 0, background.getWidth());
	}
	
	public void redrawText() {
		Graphics2D g = background.createGraphics();
		FontMetrics m = g.getFontMetrics();
		String s = String.format("%d points, %.8f apart", points.size(), maxCos);
		int x = background.getWidth()/2 - m.stringWidth(s)/2;
		int y = background.getHeight()/2 + gy(-1)/2 + m.getHeight()/2;
		//System.out.printf("(%d,%d)\n",x,y);
		g.setColor(Color.WHITE);
		g.fillRect(x, y - m.getAscent(), g.getFontMetrics().stringWidth(s), g.getFontMetrics().getHeight());
		g.setColor(Color.BLACK);
		g.drawString(s, x, y);
	}
	
	public synchronized void updateDisplay() {
		redrawText();
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		canvas.getAlphaRaster().setPixels(0, 0, canvas.getWidth(), canvas.getHeight(),
				new int[canvas.getWidth()*canvas.getHeight()]);
		//g.setColor(Color.DARK_GRAY);
		//g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		//drawCircle(0,0,1,null,getFadedColor(Color.DARK_GRAY,new Point3D(-1,0,0)),g);
		
		SpherePoint[] pointsByDepth = getPointsByDepth();
		boolean drawnBigSphere = false;
		for(SpherePoint raw: pointsByDepth) {
			SpherePoint p = raw.getRelative(viewAngle.clone());
			if(!drawnBigSphere && p.getPoint().x >= 0) { // draw big sphere
				//drawCircle(0,0,1,new Color(1,1,1,.6f),g);
				for(Line l: lines) {
					Graphics testPixelGraphics = testPixel.getGraphics();
					Point3D s = l.start.getRelative(viewAngle.clone()).getPoint();
					Point3D e = l.end.getRelative(viewAngle.clone()).getPoint();
					Point3D center = Point3D.prod(Point3D.sum(s,e), .5); // average
					g.setColor(getFadedColor(l.color, center));
					g.drawLine(gx(s.y), gy(s.z), gx(e.y), gy(e.z));
				}
				drawnBigSphere = true;
			}
			if(raw.parent != null && raw.olderInTrace != null) { // draw a trace
				//System.out.println("about to draw a trace");
				SpherePoint p2 = raw.olderInTrace.getRelative(viewAngle.clone());
				PointProperties props = points.get(raw.parent);
				drawTrace(
						p.getPoint().y,
						p.getPoint().z,
						p2.getPoint().y,
						p2.getPoint().z,
						getFadedColor(props.traceColor,p.getPoint()), g);
			} else if(points.containsKey(raw)){ // draw a sphere
				PointProperties props = points.get(raw);
				drawCircle(
					p.getPoint().y * (1+1*props.pointSize),
					p.getPoint().z * (1+1*props.pointSize),
					props.pointSize, props.color, getFadedColor(props.color, p.getPoint()), g);
			} else {
				// beginning of trace
			}
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
	
	private double agx(int gx) {
		return (gx - (canvas.getWidth()/2))/zoom/canvas.getWidth()*2;
	}
	
	private double agy(int gy) {
		return -(gy - (canvas.getHeight()/2))/zoom/canvas.getHeight()*2;
	}
	
	private double agsx(int gs) {
		return gs/zoom/canvas.getWidth()*2;
	}
	
	private double agsy(int gs) {
		return gs/zoom/canvas.getHeight()*2;
	}
	
	private void drawCircle(double x, double y, double r, Color realColor, Color c, Graphics2D g) {
		if(sqrt(x*x + y*y) + r > 1) {
			g.setColor(realColor);
			g.fillOval(gx(x-r), gy(y+r), gsx(2*r), gsy(2*r));
			Area circleToDraw = new Area(new Ellipse2D.Float(gx(x-r), gy(y+r), gsx(2*r), gsy(2*r)));
			circleToDraw.intersect(clippingCircle);
			g.setColor(c);
			g.fill(circleToDraw);
		} else {
			g.setColor(c);
			g.fillOval(gx(x-r), gy(y+r), gsx(2*r), gsy(2*r));
		}
	}
	
	private void drawTrace(double x1, double y1, double x2, double y2, Color c, Graphics g) {
		g.setColor(c);
		g.drawLine(gx(x1), gy(y1), gx(x2), gy(y2));
		//System.out.printf("Drawing a trace from (%.02f, %.02f) to (%.02f, %.02f)", x1,y1, x2,y2);
	}
	
	@Override public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
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

	public void setMaxCos(double score) {
		this.maxCos = score;
	}
}
