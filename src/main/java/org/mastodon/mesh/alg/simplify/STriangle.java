package org.mastodon.mesh.alg.simplify;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttributeValue;
import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;

public class STriangle extends PoolObject< STriangle, STrianglePool, ByteMappedElement > implements DelegateRealLocalizable, DelegateRealPositionable
{

	private final RealPointAttributeValue position;

	STriangle( final STrianglePool pool )
	{
		super( pool );
		this.position = pool.position.createQuietAttributeValue( this );
	}

	@Override
	protected void setToUninitializedState()
	{
		pool.vertex0.set( this, -1 );
		pool.vertex1.set( this, -1 );
		pool.vertex2.set( this, -1 );
	}

	public SVertex getVertex0( final SVertex ref )
	{
		final int id = pool.vertex0.get( this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	public SVertex getVertex1( final SVertex ref )
	{
		final int id = pool.vertex1.get( this );
		return pool.vertexPool.getObjectIfExists( id, ref );
	}

	public SVertex getVertex2( final SVertex ref )
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

	@Override
	public String toString()
	{
		return String.format( "T%d (V%d -> V%d -> V%d)", getInternalPoolIndex(), v0(), v1(), v2() );
	}

	public int v0()
	{
		return pool.vertex0.get( this );
	}

	public int v1()
	{
		return pool.vertex1.get( this );
	}

	public int v2()
	{
		return pool.vertex2.get( this );
	}

	public void setDirty( final boolean dirty )
	{
		pool.dirty.setQuiet( this, ( byte ) ( dirty ? 1 : 0 ) );
	}

	public boolean dirty()
	{
		return pool.dirty.get( this ) != 0;
	}

	public void setDeleted( final boolean deleted )
	{
		pool.deleted.setQuiet( this, ( byte ) ( deleted ? 1 : 0 ) );
	}

	public boolean deleted()
	{
		return pool.deleted.get( this ) != 0;
	}

	@Override
	public int numDimensions()
	{
		return 3;
	}

	@Override
	public RealPointAttributeValue delegate()
	{
		return position;
	}
}
