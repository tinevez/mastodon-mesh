package org.mastodon.mesh;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.RealPointAttributeValue;
import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;

public class Vertex extends AbstractVertex< Vertex, HalfEdge, VertexPool, ByteMappedElement > implements DelegateRealLocalizable, DelegateRealPositionable
{

	private final RealPointAttributeValue position;

	Vertex( final VertexPool pool )
	{
		super( pool );
		this.position = pool.position.createAttributeValue( this );
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

	public double x()
	{
		return getDoublePosition( 0 );
	}

	public double y()
	{
		return getDoublePosition( 1 );
	}

	public double z()
	{
		return getDoublePosition( 2 );
	}

	/**
	 * Sets the X,Y,Z position of this vertex.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vertex init( final double x, final double y, final double z )
	{
		position.setPosition( x, 0 );
		position.setPosition( y, 1 );
		position.setPosition( z, 2 );
		return this;
	}
}
