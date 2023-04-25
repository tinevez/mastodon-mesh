package org.mastodon.mesh.obj.base;

import org.mastodon.mesh.TriangleI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

public class AbstractTriangle<
					T extends AbstractTriangle< T, V, TP, M >,
					V extends AbstractVertex< V, ?, T, ?, M >, 
					TP extends AbstractTrianglePool< T, V, ?, M>,
					M extends MappedElement >
	extends PoolObject< T, TP, M > 
	implements TriangleI< V >
{

	protected AbstractTriangle( final TP pool )
	{
		super( pool );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected void setToUninitializedState()
	{
		pool.vertex0.set( ( T ) this, -1 );
		pool.vertex1.set( ( T ) this, -1 );
		pool.vertex2.set( ( T ) this, -1 );
	}

	@Override
	public V getVertex0( final V ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.vertex0.get( ( T ) this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	@Override
	public V getVertex1( final V ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.vertex1.get( ( T ) this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	@Override
	public V getVertex2( final V ref )
	{
		@SuppressWarnings( "unchecked" )
		final int id = pool.vertex2.get( ( T ) this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void normal( final float[] normal )
	{
		normal[ 0 ] = pool.normal.get( ( T ) this, 0 );
		normal[ 1 ] = pool.normal.get( ( T ) this, 1 );
		normal[ 2 ] = pool.normal.get( ( T ) this, 2 );
	}

	@Override
	public String toString()
	{
		return String.format( "T%d (V%d -> V%d -> V%d)", getInternalPoolIndex(), v0(), v1(), v2() );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int v0()
	{
		return pool.vertex0.get( ( T ) this );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int v1()
	{
		return pool.vertex1.get( ( T ) this );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int v2()
	{
		return pool.vertex2.get( ( T ) this );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void init( final V v0, final V v1, final V v2, final float nx, final float ny, final float nz )
	{
		pool.vertex0.setQuiet( ( T ) this, v0.getInternalPoolIndex() );
		pool.vertex1.setQuiet( ( T ) this, v1.getInternalPoolIndex() );
		pool.vertex2.setQuiet( ( T ) this, v2.getInternalPoolIndex() );
		pool.normal.setQuiet( ( T ) this, 0, nx );
		pool.normal.setQuiet( ( T ) this, 1, ny );
		pool.normal.setQuiet( ( T ) this, 2, nz );

	}
}
