package net.clonecomputers.lab.sphere.render;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import net.clonecomputers.lab.sphere.*;

public class Renderer extends JPanel {
	private BufferedImage canvas;
	private Set<SpherePoint> points;
	
	public Renderer(int width, int height) {
		this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.points = new HashSet<SpherePoint>();
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
		// TODO: draw stuff here
		this.repaint();
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(canvas, 0, 0, Color.WHITE, this);
	}
}
