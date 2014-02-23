package net.clonecomputers.lab.sphere;

import java.awt.*;

public class Line {
	public final SpherePoint start;
	public final SpherePoint end;
	public final Color color;
	
	public Line(SpherePoint start, SpherePoint end, Color color) {
		this.start = start;
		this.end = end;
		float[] hsbvals = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		this.color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1]*.2f, hsbvals[2]*.4f + .3f));
	}
}
