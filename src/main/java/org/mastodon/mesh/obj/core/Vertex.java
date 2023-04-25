package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;

public class Vertex extends AbstractVertex< Vertex, HalfEdge, Triangle, VertexPool, ByteMappedElement >
{

	Vertex( final VertexPool pool )
	{
		super( pool );
	}
}
