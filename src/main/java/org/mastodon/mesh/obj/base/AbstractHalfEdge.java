package org.mastodon.mesh.obj.base;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.mesh.obj.HalfEdgeI;
import org.mastodon.pool.MappedElement;

public class AbstractHalfEdge<
		E extends AbstractHalfEdge< E, V, T, EP, TP, M >,
				V extends AbstractVertex< V, E, T, ?, M >,
				T extends AbstractTriangle< T, V, ?, M >,
				EP extends AbstractHalfEdgePool< E, V, TP, M >,
				TP extends AbstractTrianglePool< T, V, ?, M >, 
				M extends MappedElement >
		extends AbstractEdge< E, V, EP, M >
		implements HalfEdgeI< E, V, T >
{

	protected AbstractHalfEdge( final EP pool )
	{
		super( pool );
	}

	@Override
	public E next( final E ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.next.get( ( E ) this );
		return pool.getObjectIfExists( id, ref );
	}

	@Override
	public E previous( final E ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.previous.get( ( E ) this );
		return pool.getObjectIfExists( id, ref );
	}

	@Override
	public E twin( final E ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.twin.get( ( E ) this );
		return pool.getObjectIfExists( id, ref );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public T triangle( final T ref )
	{
		final int id = pool.face.get( ( E ) this );
		return pool.trianglePool.getObjectIfExists( id, ref );
	}

	@SuppressWarnings( "unchecked" )
	void init( final E next, final E previous, final E twin, final T triangle )
	{
		pool.next.set( ( E ) this, next.getInternalPoolIndex() );
		pool.previous.set( ( E ) this, previous.getInternalPoolIndex() );
		pool.face.set( ( E ) this, triangle.getInternalPoolIndex() );
		// Retrieve twin.
		if ( twin != null )
		{
			pool.twin.set( ( E ) this, twin.getInternalPoolIndex() );
			pool.twin.set( twin, getInternalPoolIndex() );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public String toString()
	{
		return String.format( "%s%d (V%d -> V%d, NE%d, PE%d, TE%d, F%d)",
				getClass().getSimpleName(),
				getInternalPoolIndex(), getSourceVertexInternalPoolIndex(), getTargetVertexInternalPoolIndex(),
				pool.next.get( ( E ) this ), pool.previous.get( ( E ) this ), pool.twin.get( ( E ) this ), pool.face.get( ( E ) this ) );
	}
}
