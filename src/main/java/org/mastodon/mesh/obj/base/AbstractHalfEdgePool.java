package org.mastodon.mesh.obj.base;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.attributes.IntAttribute;

public abstract class AbstractHalfEdgePool<
				E extends AbstractHalfEdge< E, V, ?, ?, TP, M >,
				V extends AbstractVertex< V, E, ?, ?, M >,
				TP extends AbstractTrianglePool< ?, V, ?, M >, 
				M extends MappedElement >
		extends AbstractEdgePool< E, V, M >
{

	final IntAttribute< E > next;

	final IntAttribute< E > previous;

	final IntAttribute< E > twin;

	final IntAttribute< E > face;

	TP trianglePool;

	protected AbstractHalfEdgePool(
			final int initialCapacity,
			final BaseEdgeLayout layout,
			final Class< E > edgeClass,
			final MemPool.Factory< M > memPoolFactory,
			final AbstractVertexPool< V, ?, ?, ? > vertexPool )
	{
		super( initialCapacity, layout, edgeClass, memPoolFactory, vertexPool );
		this.next = new IntAttribute<>( layout.next, this );
		this.previous = new IntAttribute<>( layout.previous, this );
		this.twin = new IntAttribute<>( layout.twin, this );
		this.face = new IntAttribute<>( layout.face, this );
	}

	public void setLinkedTrianglePool( final TP trianglePool )
	{
		this.trianglePool = trianglePool;
	}

	public static class BaseEdgeLayout extends AbstractEdgeLayout
	{

		final IntField next;

		final IntField previous;

		final IntField twin;

		final IntField face;

		public BaseEdgeLayout()
		{
			next = intField();
			previous = intField();
			twin = intField();
			face = intField();
		}
	}
}
