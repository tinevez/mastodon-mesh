package org.mastodon.mesh.obj.base;

import org.mastodon.mesh.obj.VertexI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.attributes.RealPointAttributeValue;
import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;

public class AbstractVertex<
				V extends AbstractVertex< V, E, T, VP, M >,
				E extends AbstractHalfEdge<E,V, T, ?, ?, M>,
				T extends AbstractTriangle< T, V, ?, M >,
				VP extends AbstractVertexPool< V, E, T, M >,
				M extends MappedElement >
		extends org.mastodon.graph.ref.AbstractVertex< V, E, VP, M >
		implements VertexI< E >, DelegateRealLocalizable, DelegateRealPositionable
{

	private final RealPointAttributeValue position;

	protected AbstractVertex( final VP pool )
	{
		super( pool );
		@SuppressWarnings( "unchecked" ) final V self = ( V ) this;
		this.position = pool.position.createQuietAttributeValue( self );
	}

	@Override
	public RealPointAttributeValue delegate()
	{
		return position;
	}

	@Override
	public String toString()
	{
		return String.format( "V%d (X=%.2f, Y=%.2f, Z=%.2f)",
				getInternalPoolIndex(),
				getDoublePosition( 0 ),
				getDoublePosition( 1 ),
				getDoublePosition( 2 ) );
	}

	@Override
	public double x()
	{
		return getDoublePosition( 0 );
	}

	@Override
	public double y()
	{
		return getDoublePosition( 1 );
	}

	@Override
	public double z()
	{
		return getDoublePosition( 2 );
	}

	@Override
	public int id()
	{
		return getInternalPoolIndex();
	}

	@Override
	public int numDimensions()
	{
		return 3;
	}
}
