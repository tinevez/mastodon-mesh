package org.mastodon.mesh.alg.simplify;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class SHalfEdgePool extends AbstractEdgePool< SHalfEdge, SVertex, ByteMappedElement >
{

	public static final EdgeLayout layout = new EdgeLayout();

	final IntAttribute< SHalfEdge > next;

	final IntAttribute< SHalfEdge > previous;

	final IntAttribute< SHalfEdge > twin;

	final IntAttribute< SHalfEdge > face;

	STrianglePool facePool;

	SHalfEdgePool( final int initialCapacity, final SVertexPool vertexPool )
	{
		super( initialCapacity, layout, SHalfEdge.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
		this.next = new IntAttribute<>( layout.next, this );
		this.previous = new IntAttribute<>( layout.previous, this );
		this.twin = new IntAttribute<>( layout.twin, this );
		this.face = new IntAttribute<>( layout.face, this );
	}

	@Override
	protected SHalfEdge createEmptyRef()
	{
		return new SHalfEdge( this );
	}

	void setLinkedFacePool( final STrianglePool facePool )
	{
		this.facePool = facePool;
	}

	public static class EdgeLayout extends AbstractEdgeLayout
	{

		final IntField next;

		final IntField previous;

		final IntField twin;

		final IntField face;

		public EdgeLayout()
		{
			next = intField();
			previous = intField();
			twin = intField();
			face = intField();
		}
	}

}
