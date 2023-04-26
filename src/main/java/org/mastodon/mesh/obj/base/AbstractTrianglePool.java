package org.mastodon.mesh.obj.base;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;

public abstract class AbstractTrianglePool<
				T extends AbstractTriangle< T, V, E, ?, EP, M >, 
				V extends AbstractVertex< V, E, T, ?, M >,
				E extends AbstractHalfEdge< E, V, T, ?, ?, M >,
				VP extends AbstractVertexPool< V, ?, T, M >,
				EP extends AbstractEdgePool< E, V, M >,
				M extends MappedElement >
	extends Pool< T, M >
{

	final IntAttribute< T > vertex0;

	final IntAttribute< T > vertex1;

	final IntAttribute< T > vertex2;

	final FloatArrayAttribute< T > normal;

	final IntAttribute< T > edge0;

	final IntAttribute< T > edge1;

	final IntAttribute< T > edge2;

	final VP vertexPool;

	final EP edgePool;


	protected AbstractTrianglePool(
			final int initialCapacity, 
			final BaseTriangleLayout layout,
			final Class< T > triangleClass,
			final MemPool.Factory< M > memPoolFactory,
			final VP vertexPool,
			final EP edgePool )
	{
		super( initialCapacity, layout, triangleClass, memPoolFactory );
		this.vertexPool = vertexPool;
		this.edgePool = edgePool;
		this.vertex0 = new IntAttribute<>( layout.vertex0, this );
		this.vertex1 = new IntAttribute<>( layout.vertex1, this );
		this.vertex2 = new IntAttribute<>( layout.vertex2, this );
		this.normal = new FloatArrayAttribute<>( layout.normal, this );
		this.edge0 = new IntAttribute<>( layout.edge0, this );
		this.edge1 = new IntAttribute<>( layout.edge1, this );
		this.edge2 = new IntAttribute<>( layout.edge2, this );
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

		final FloatArrayField normal;

		final IntField edge0;

		final IntField edge1;

		final IntField edge2;

		public BaseTriangleLayout()
		{
			vertex0 = intField();
			vertex1 = intField();
			vertex2 = intField();
			normal = floatArrayField( 3 );
			edge0 = intField();
			edge1 = intField();
			edge2 = intField();
		}
	}
}
