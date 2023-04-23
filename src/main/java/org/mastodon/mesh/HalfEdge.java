package org.mastodon.mesh;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;

public class HalfEdge extends AbstractEdge< HalfEdge, Vertex, HalfEdgePool, ByteMappedElement >
{

	protected HalfEdge( final HalfEdgePool pool )
	{
		super( pool );
	}

	public HalfEdge next( final HalfEdge ref )
	{
		final int id = pool.next.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public HalfEdge previous( final HalfEdge ref )
	{
		final int id = pool.previous.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public HalfEdge twin( final HalfEdge ref )
	{
		final int id = pool.twin.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public Face face( final Face ref )
	{
		final int id = pool.face.get( this );
		return pool.facePool.getObjectIfExists( id, ref );
	}

	void init( final HalfEdge next, final HalfEdge previous, final HalfEdge twin, final Face face )
	{
		pool.next.set( this, next.getInternalPoolIndex() );
		pool.previous.set( this, previous.getInternalPoolIndex() );
		pool.face.set( this, face.getInternalPoolIndex() );
		// Retrieve twin.
		if ( twin != null )
		{
			pool.twin.set( this, twin.getInternalPoolIndex() );
			pool.twin.set( twin, getInternalPoolIndex() );
		}

	}
}
