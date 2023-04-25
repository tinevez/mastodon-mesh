package org.mastodon.mesh.obj.base;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;

public abstract class AbstractTrianglePool<
				T extends AbstractTriangle< T, V, ?, M >, 
				V extends AbstractVertex< V, ?, T, ?, M >,
				VP extends AbstractVertexPool< V, ?, T, M >,
				M extends MappedElement >
	extends Pool< T, M >
{

	final IntAttribute< T > vertex0;

	final IntAttribute< T > vertex1;

	final IntAttribute< T > vertex2;

	final FloatArrayAttribute< T > normal;

	final VP vertexPool;

	protected AbstractTrianglePool(
			final int initialCapacity, 
			final BaseTriangleLayout layout,
			final Class< T > triangleClass,
			final MemPool.Factory< M > memPoolFactory,
			final VP vertexPool )
	{
		super( initialCapacity, layout, triangleClass, memPoolFactory );
		this.vertexPool = vertexPool;
		this.vertex0 = new IntAttribute<>( layout.vertex0, this );
		this.vertex1 = new IntAttribute<>( layout.vertex1, this );
		this.vertex2 = new IntAttribute<>( layout.vertex2, this );
		this.normal = new FloatArrayAttribute<>( layout.normal, this );
	}

	@Override
	public T create( final T ref )
	{
		return super.create( ref );
	}

	public static class BaseTriangleLayout extends PoolObjectLayout
	{

		final IntField vertex0;

		final IntField vertex1;

		final IntField vertex2;

		FloatArrayField normal;

		public BaseTriangleLayout()
		{
			vertex0 = intField();
			vertex1 = intField();
			vertex2 = intField();
			normal = floatArrayField( 3 );
		}
	}
}
