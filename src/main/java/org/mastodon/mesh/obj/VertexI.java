package org.mastodon.mesh.obj;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

public interface VertexI< E extends HalfEdgeI< ?, ?, ? > > extends org.mastodon.graph.Vertex< E >, RealPositionable, RealLocalizable, EuclideanSpace
{

	public double x();

	public double y();

	public double z();

	public int id();

	@Override
	default int numDimensions()
	{
		return 3;
	}
}
