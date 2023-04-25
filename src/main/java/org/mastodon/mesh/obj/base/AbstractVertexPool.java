package org.mastodon.mesh.obj.base;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

public abstract class AbstractVertexPool<
				V extends AbstractVertex< V, E, T, ?, M >,
				E extends AbstractHalfEdge< E, V, T, ?, ?, M>,
				T extends AbstractTriangle< T, V, ?, M >, 
				M extends MappedElement  >
		extends org.mastodon.graph.ref.AbstractVertexPool< V, E, M >
{

	final RealPointAttribute< V > position;

	public AbstractVertexPool(
			final int initialCapacity,
			final BaseVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< M > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
		this.position = new RealPointAttribute<>( layout.position, this );
	}

	public static class BaseVertexLayout extends AbstractVertexLayout
	{
		final DoubleArrayField position;

		public BaseVertexLayout()
		{
			position = doubleArrayField( 3 );
		}
	}
}
