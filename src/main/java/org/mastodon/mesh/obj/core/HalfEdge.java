package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractHalfEdge;
import org.mastodon.pool.ByteMappedElement;

public class HalfEdge extends AbstractHalfEdge< HalfEdge, Vertex, Triangle, HalfEdgePool, TrianglePool, ByteMappedElement >
{

	protected HalfEdge( final HalfEdgePool pool )
	{
		super( pool );
	}
}
