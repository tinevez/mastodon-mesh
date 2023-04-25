package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractTrianglePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TrianglePool extends AbstractTrianglePool< Triangle, Vertex, VertexPool, ByteMappedElement >
{

	TrianglePool( final int initialCapacity, final VertexPool vertexPool )
	{
		super( initialCapacity, new BaseTriangleLayout(), Triangle.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
	}

	@Override
	protected Triangle createEmptyRef()
	{
		return new Triangle( this );
	}
}
