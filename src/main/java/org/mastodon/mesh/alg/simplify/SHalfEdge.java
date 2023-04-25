package org.mastodon.mesh.alg.simplify;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;

public class SHalfEdge extends AbstractEdge< SHalfEdge, SVertex, SHalfEdgePool, ByteMappedElement >
{

	protected SHalfEdge( final SHalfEdgePool pool )
	{
		super( pool );
	}

	public SHalfEdge next( final SHalfEdge ref )
	{
		final int id = pool.next.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public SHalfEdge previous( final SHalfEdge ref )
	{
		final int id = pool.previous.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public SHalfEdge twin( final SHalfEdge ref )
	{
		final int id = pool.twin.get( this );
		return pool.getObjectIfExists( id, ref );
	}

	public STriangle triangle( final STriangle ref )
	{
		final int id = pool.face.get( this );
		return pool.facePool.getObjectIfExists( id, ref );
	}

	void init( final SHalfEdge next, final SHalfEdge previous, final SHalfEdge twin, final STriangle face )
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

	@Override
	public String toString()
	{
		return String.format( "E%d (V%d -> V%d, NE%d, PE%d, TE%d, F%d)",
				getInternalPoolIndex(), getSourceVertexInternalPoolIndex(), getTargetVertexInternalPoolIndex(),
				pool.next.get( this ), pool.previous.get( this ), pool.twin.get( this ), pool.face.get( this ) );
	}
}
