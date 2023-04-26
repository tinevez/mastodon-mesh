package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractTrianglePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TrianglePool extends AbstractTrianglePool< Triangle, Vertex, HalfEdge, VertexPool, HalfEdgePool, ByteMappedElement >
{

	TrianglePool( final int initialCapacity, final VertexPool vertexPool, final HalfEdgePool edgePool )
	{
		super( initialCapacity,
				new BaseTriangleLayout(),
				Triangle.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool,
				edgePool );
	}

	@Override
	protected Triangle createEmptyRef()
	{
		return new Triangle( this );
	}
}
