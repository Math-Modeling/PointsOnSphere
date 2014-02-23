package net.clonecomputers.lab.sphere.testsolution;

import java.util.*;

import net.clonecomputers.lab.sphere.*;

public class ConnectedPoint extends SpherePoint{
	public final Set<ConnectedPoint> connections;

	public ConnectedPoint(SpherePoint point) {
		super(point);
		this.connections = new HashSet<ConnectedPoint>();
	}
}
