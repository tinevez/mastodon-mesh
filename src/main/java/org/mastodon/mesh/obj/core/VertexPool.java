package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class VertexPool extends AbstractVertexPool< Vertex, HalfEdge, Triangle, ByteMappedElement >
{

	public VertexPool( final int initialCapacity )
	{
		super( initialCapacity, new BaseVertexLayout(), Vertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
	}

	@Override
	protected Vertex createEmptyRef()
	{
		return new Vertex( this );
	}
}
