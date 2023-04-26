package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractTriangle;
import org.mastodon.pool.ByteMappedElement;

public class Triangle extends AbstractTriangle< Triangle, Vertex, HalfEdge, TrianglePool, HalfEdgePool, ByteMappedElement >
{

	Triangle( final TrianglePool pool )
	{
		super( pool );
	}
}
