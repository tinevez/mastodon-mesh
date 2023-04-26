package org.mastodon.mesh.obj.base;

import org.mastodon.mesh.obj.TriangleI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.FloatArrayAttributeValue;
import org.mastodon.pool.attributes.IntAttributeValue;

public class AbstractTriangle<
					T extends AbstractTriangle< T, V, TP, M >,
					V extends AbstractVertex< V, ?, T, ?, M >, 
					TP extends AbstractTrianglePool< T, V, ?, M>,
					M extends MappedElement >
	extends PoolObject< T, TP, M > 
	implements TriangleI< V >
{

	private final IntAttributeValue vertex0;

	private final IntAttributeValue vertex1;

	private final IntAttributeValue vertex2;

	private final FloatArrayAttributeValue normal;

	protected AbstractTriangle( final TP pool )
	{
		super( pool );
		@SuppressWarnings( "unchecked" )
		final T self = ( T ) this;
		this.vertex0 = pool.vertex0.createQuietAttributeValue( self );
		this.vertex1 = pool.vertex1.createQuietAttributeValue( self );
		this.vertex2 = pool.vertex2.createQuietAttributeValue( self );
		this.normal = pool.normal.createQuietAttributeValue( self );
	}

	@Override
	protected void setToUninitializedState()
	{
		vertex0.set( -1 );
		vertex1.set( -1 );
		vertex2.set( -1 );
		normal.set( 0, 0f );
		normal.set( 1, 0f );
		normal.set( 2, 0f );
	}

	@Override
	public V getVertex0( final V ref )
	{
		return pool.vertexPool.getObjectIfExists( vertex0.get(), ref );
	}

	@Override
	public V getVertex1( final V ref )
	{
		return pool.vertexPool.getObjectIfExists( vertex1.get(), ref );
	}

	@Override
	public V getVertex2( final V ref )
	{
		return pool.vertexPool.getObjectIfExists( vertex2.get(), ref );
	}

	@Override
	public void normal( final float[] normal )
	{
		normal[ 0 ] = this.normal.get( 0 );
		normal[ 1 ] = this.normal.get( 1 );
		normal[ 2 ] = this.normal.get( 2 );
	}

	@Override
	public String toString()
	{
		return String.format( "T%d (V%d -> V%d -> V%d)", getInternalPoolIndex(), v0(), v1(), v2() );
	}

	@Override
	public int v0()
	{
		return vertex0.get();
	}

	@Override
	public int v1()
	{
		return vertex1.get();
	}

	@Override
	public int v2()
	{
		return vertex2.get();
	}

	@Override
	public void init( final V v0, final V v1, final V v2, final float nx, final float ny, final float nz )
	{
		vertex0.set( v0.getInternalPoolIndex() );
		vertex1.set( v1.getInternalPoolIndex() );
		vertex2.set( v2.getInternalPoolIndex() );
		normal.set( 0, nx );
		normal.set( 1, ny );
		normal.set( 2, nz );
	}
}
