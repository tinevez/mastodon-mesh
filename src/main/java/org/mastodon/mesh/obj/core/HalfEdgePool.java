package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractHalfEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class HalfEdgePool extends AbstractHalfEdgePool< HalfEdge, Vertex, TrianglePool, ByteMappedElement >
{

	HalfEdgePool( final int initialCapacity, final VertexPool vertexPool )
	{
		super( initialCapacity, new BaseEdgeLayout(), HalfEdge.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
	}

	@Override
	protected HalfEdge createEmptyRef()
	{
		return new HalfEdge( this );
	}
}
