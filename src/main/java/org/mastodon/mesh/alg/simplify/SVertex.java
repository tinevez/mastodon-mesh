package org.mastodon.mesh.alg.simplify;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.DoubleArrayAttributeValue;
import org.mastodon.pool.attributes.RealPointAttributeValue;
import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;

public class SVertex extends AbstractVertex< SVertex, SHalfEdge, SVertexPool, ByteMappedElement > implements DelegateRealLocalizable, DelegateRealPositionable
{

	private final RealPointAttributeValue position;

	private final DoubleArrayAttributeValue q;

	SVertex( final SVertexPool pool )
	{
		super( pool );
		this.position = pool.position.createQuietAttributeValue( this );
		this.q = pool.q.createQuietAttributeValue( this );
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
	public SVertex init( final double x, final double y, final double z )
	{
		position.setPosition( x, 0 );
		position.setPosition( y, 1 );
		position.setPosition( z, 2 );
		return this;
	}

	public void setQ( final double a, final double b, final double c, final double d )
	{
		q.set( 0, a * a );
		q.set( 1, a * b );
		q.set( 2, a * c );
		q.set( 3, a * d );
		q.set( 4, b * b );
		q.set( 5, b * c );
		q.set( 6, b * d );
		q.set( 7, c * c );
		q.set( 8, c * d );
		q.set( 9, d * d );
	}

	public void setQ( final double c )
	{
		q.set( 0, c );
		q.set( 1, c );
		q.set( 2, c );
		q.set( 3, c );
		q.set( 4, c );
		q.set( 5, c );
		q.set( 6, c );
		q.set( 7, c );
		q.set( 8, c );
		q.set( 9, c );
	}

	public void setQ(final double m11, final double m12, final double m13, final double m14,
				final double m22, final double m23, final double m24,
				final double m33, final double m34,
				final double m44 )
	{
		q.set( 0, m11 );
		q.set( 1, m12 );
		q.set( 2, m13 );
		q.set( 3, m14 );
		q.set( 4, m22 );
		q.set( 5, m23 );
		q.set( 6, m24 );
		q.set( 7, m33 );
		q.set( 8, m34 );
		q.set( 9, m44 );
	}

	public void setQ( final double[] arr )
	{
		for ( int i = 0; i < 10; i++ )
			q.set( i, arr[i] );
	}

	public void getQ(final double[] arr)
	{
		for ( int i = 0; i < 10; i++ )
			arr[ i ] = q.get( i );
	}

	public void addInPlace( final double[] arr )
	{
		for ( int i = 0; i < 10; i++ )
			q.set( i, q.get( i ) + arr[ i ] );
	}
}
