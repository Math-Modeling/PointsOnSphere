package net.clonecomputers.lab.sphere;

import java.awt.*;

public class PointProperties {
	public final Color color;
	public final Color traceColor;
	public final boolean shouldBeMoved;
	public final double pointSize;
	
	private static final double defaultSize = .03;
	
	public PointProperties() {
		this(Color.BLUE);
	}
	
	public PointProperties(Color c) {
		this(c, true);
	}
	
	public PointProperties(boolean move) {
		this(Color.BLUE, move);
	}
	
	public PointProperties(Color c, boolean move) {
		this(c, move, 1);
	}
	
	public PointProperties(Color c, double size) {
		this(c, true, size);
	}
	
	public PointProperties(Color c, Color trace) {
		this(c, trace, true);
	}
	
	public PointProperties(Color c, Color trace, boolean move) {
		this(c, trace, move, 1);
	}
	
	public PointProperties(Color c, Color trace, double size) {
		this(c, trace, true, size);
	}
	
	public PointProperties(Color c, boolean move, double size) {
		this(c, Color.RED, move, size);
	}
	
	public PointProperties(Color c, Color trace, boolean move, double size) {
		color = c;
		traceColor = trace;
		shouldBeMoved = move;
		pointSize = defaultSize*size;
	}
	
}
