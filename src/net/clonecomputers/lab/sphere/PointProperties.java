package net.clonecomputers.lab.sphere;

import java.awt.Color;

public class PointProperties {
	public final Color color;
	public final boolean shouldBeMoved;
	public final double pointSize;
	
	private static final double defaultSize = .03;
	
	public PointProperties() {
		color = Color.blue;
		shouldBeMoved = true;
		pointSize = defaultSize;
	}
	
	public PointProperties(Color c) {
		color = c;
		shouldBeMoved = true;
		pointSize = defaultSize;
	}
	
	public PointProperties(boolean move) {
		color = Color.blue;
		shouldBeMoved = move;
		pointSize = defaultSize;
	}
	
	public PointProperties(Color c, boolean move) {
		color = c;
		shouldBeMoved = move;
		pointSize = defaultSize;
	}
	
	public PointProperties(Color c, boolean move, double size) {
		color = c;
		shouldBeMoved = move;
		pointSize = defaultSize*size;
	}
	
}
