package org.mastodon.mesh.obj.base;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.mesh.obj.HalfEdgeI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.attributes.IntAttributeValue;

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

	private final IntAttributeValue next;

	private final IntAttributeValue previous;

	private final IntAttributeValue twin;

	private final IntAttributeValue triangle;

	protected AbstractHalfEdge( final EP pool )
	{
		super( pool );
		@SuppressWarnings( "unchecked" ) final E self = ( E ) this;
		this.next = pool.next.createQuietAttributeValue( self );
		this.previous = pool.previous.createQuietAttributeValue( self );
		this.twin = pool.twin.createQuietAttributeValue( self );
		this.triangle = pool.triangle.createQuietAttributeValue( self );
	}

	@Override
	protected void setToUninitializedState()
	{
		super.setToUninitializedState();
		next.set( -1 );
		previous.set( -1 );
		twin.set( -1 );
		triangle.set( -1 );
	}

	@Override
	public E next( final E ref )
	{
		return pool.getObjectIfExists( next.get(), ref );
	}

	@Override
	public E previous( final E ref )
	{
		return pool.getObjectIfExists( previous.get(), ref );
	}

	@Override
	public E twin( final E ref )
	{
		return pool.getObjectIfExists( twin.get(), ref );
	}

	@Override
	public T triangle( final T ref )
	{
		return pool.trianglePool.getObjectIfExists( triangle.get(), ref );
	}

	void init( final E eNext, final E ePrevious, final E eTwin, final T eTriangle )
	{
		next.set( eNext.getInternalPoolIndex() );
		previous.set( ePrevious.getInternalPoolIndex() );
		triangle.set( eTriangle.getInternalPoolIndex() );
		// Retrieve twin.
		if ( eTwin != null )
		{
			this.twin.set( eTwin.getInternalPoolIndex() );
			( ( AbstractHalfEdge< ?, ?, ?, ?, ?, ? > ) eTwin ).twin.set( getInternalPoolIndex() );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public String toString()
	{
		return String.format( "%s%d (V%d -> V%d, NE%d, PE%d, TE%d, F%d)",
				getClass().getSimpleName(),
				getInternalPoolIndex(), getSourceVertexInternalPoolIndex(), getTargetVertexInternalPoolIndex(),
				pool.next.get( ( E ) this ), pool.previous.get( ( E ) this ), pool.twin.get( ( E ) this ), pool.triangle.get( ( E ) this ) );
	}
}
