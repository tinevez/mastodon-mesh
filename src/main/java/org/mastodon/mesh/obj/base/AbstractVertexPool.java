package org.mastodon.mesh.obj.base;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.attributes.RealFloatPointAttribute;

public abstract class AbstractVertexPool<
				V extends AbstractVertex< V, E, T, ?, M >,
				E extends AbstractHalfEdge< E, V, T, ?, ?, M>,
				T extends AbstractTriangle< T, V, E, ?, ?, M >, 
				M extends MappedElement  >
		extends org.mastodon.graph.ref.AbstractVertexPool< V, E, M >
{

	final RealFloatPointAttribute< V > position;

	public AbstractVertexPool(
			final int initialCapacity,
			final BaseVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< M > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
		this.position = new RealFloatPointAttribute<>( layout.position, this );
	}

	public static class BaseVertexLayout extends AbstractVertexLayout
	{
		final FloatArrayField position;

		public BaseVertexLayout()
		{
			position = floatArrayField( 3 );
		}
	}
}
