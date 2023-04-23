package org.mastodon.mesh;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class HalfEdgePool extends AbstractEdgePool< HalfEdge, Vertex, ByteMappedElement >
{

	public static final EdgeLayout layout = new EdgeLayout();

	final IntAttribute< HalfEdge > next;

	final IntAttribute< HalfEdge > previous;

	final IntAttribute< HalfEdge > twin;

	final IntAttribute< HalfEdge > face;

	FacePool facePool;

	HalfEdgePool( final int initialCapacity, final VertexPool vertexPool )
	{
		super( initialCapacity, layout, HalfEdge.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
		this.next = new IntAttribute<>( layout.next, this );
		this.previous = new IntAttribute<>( layout.previous, this );
		this.twin = new IntAttribute<>( layout.twin, this );
		this.face = new IntAttribute<>( layout.face, this );
	}

	@Override
	protected HalfEdge createEmptyRef()
	{
		return new HalfEdge( this );
	}

	void setLinkedFacePool( final FacePool facePool )
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
