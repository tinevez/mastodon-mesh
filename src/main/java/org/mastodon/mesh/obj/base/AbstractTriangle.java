package org.mastodon.mesh.obj.base;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.mesh.obj.TriangleI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.FloatArrayAttributeValue;
import org.mastodon.pool.attributes.IntAttributeValue;

public class AbstractTriangle<
					T extends AbstractTriangle< T, V, E, TP, EP, M >,
					V extends AbstractVertex< V, E, T, ?, M >, 
					E extends AbstractHalfEdge< E, V, T, ?, TP, M >,
					TP extends AbstractTrianglePool< T, V, E, ?, ?, M >,
					EP extends AbstractEdgePool< E, V, M >,
					M extends MappedElement >
	extends PoolObject< T, TP, M > 
	implements TriangleI< V, E >
{

	private final IntAttributeValue vertex0;

	private final IntAttributeValue vertex1;

	private final IntAttributeValue vertex2;

	private final FloatArrayAttributeValue normal;

	private final IntAttributeValue edge0;

	private final IntAttributeValue edge1;

	private final IntAttributeValue edge2;

	protected AbstractTriangle( final TP pool )
	{
		super( pool );
		@SuppressWarnings( "unchecked" )
		final T self = ( T ) this;
		this.vertex0 = pool.vertex0.createQuietAttributeValue( self );
		this.vertex1 = pool.vertex1.createQuietAttributeValue( self );
		this.vertex2 = pool.vertex2.createQuietAttributeValue( self );
		this.normal = pool.normal.createQuietAttributeValue( self );
		this.edge0 = pool.edge0.createQuietAttributeValue( self );
		this.edge1 = pool.edge1.createQuietAttributeValue( self );
		this.edge2 = pool.edge2.createQuietAttributeValue( self );
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
		edge0.set( -1 );
		edge1.set( -1 );
		edge2.set( -1 );
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
	public E getHalfEdge0( final E ref )
	{
		return pool.edgePool.getObjectIfExists( edge0.get(), ref );
	}

	@Override
	public E getHalfEdge1( final E ref )
	{
		return pool.edgePool.getObjectIfExists( edge1.get(), ref );
	}

	@Override
	public E getHalfEdge2( final E ref )
	{
		return pool.edgePool.getObjectIfExists( edge2.get(), ref );
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

	public Iterator< V > vertexIterator()
	{
		return new MyPoolObjIterator< V >( Arrays.asList( v0(), v1(), v2() ), pool.vertexPool );
	}

	public Iterable< V > vertexIterable()
	{
		return new Iterable< V >()
		{

			@Override
			public Iterator< V > iterator()
			{
				return vertexIterator();
			}
		};
	}

	public Iterator< E > edgeIterator()
	{
		return new MyPoolObjIterator< E >( Arrays.asList( e0(), e1(), e2() ), pool.edgePool );
	}

	public Iterable< E > edgeIterable()
	{
		return new Iterable< E >()
		{

			@Override
			public Iterator< E > iterator()
			{
				return edgeIterator();
			}
		};
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

	@Override
	public int e0()
	{
		return edge0.get();
	}

	@Override
	public int e1()
	{
		return edge1.get();
	}

	@Override
	public int e2()
	{
		return edge2.get();
	}

	private static final class MyPoolObjIterator< O extends PoolObject< O, ?, ? > > implements Iterator< O >
	{

		private final O ref;

		private final Iterator< Integer > it;

		private final Pool< O, ? > pool;

		public MyPoolObjIterator( final List< Integer > ids, final Pool< O, ? > pool )
		{
			this.pool = pool;
			this.ref = pool.createRef();
			this.it = ids.iterator();
		}

		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}

		@Override
		public O next()
		{
			return pool.getObjectIfExists( it.next(), ref );
		}
	}
}
