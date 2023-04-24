package org.mastodon.mesh;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;

public class Triangle extends PoolObject< Triangle, TrianglePool, ByteMappedElement >
{

	Triangle( final TrianglePool pool )
	{
		super( pool );
	}

	@Override
	protected void setToUninitializedState()
	{
		pool.vertex0.set( this, -1 );
		pool.vertex1.set( this, -1 );
		pool.vertex2.set( this, -1 );
	}

	public Vertex getVertex0( final Vertex ref )
	{
		final int id = pool.vertex0.get( this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	public Vertex getVertex1( final Vertex ref )
	{
		final int id = pool.vertex1.get( this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	public Vertex getVertex2( final Vertex ref )
	{
		final int id = pool.vertex2.get( this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	public void normal( final float[] normal )
	{
		normal[ 0 ] = pool.normal.get( this, 0 );
		normal[ 1 ] = pool.normal.get( this, 1 );
		normal[ 2 ] = pool.normal.get( this, 2 );
	}
}
