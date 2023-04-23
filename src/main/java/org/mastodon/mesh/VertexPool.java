package org.mastodon.mesh;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

public class VertexPool extends AbstractVertexPool< Vertex, HalfEdge, ByteMappedElement >
{

	public static final VertexLayout layout = new VertexLayout();

	final RealPointAttribute< Vertex > position;

	public VertexPool( final int initialCapacity )
	{
		super( initialCapacity, layout, Vertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.position = new RealPointAttribute<>( layout.position, this );
	}

	@Override
	protected Vertex createEmptyRef()
	{
		return new Vertex( this );
	}

	public static class VertexLayout extends AbstractVertexLayout
	{
		final DoubleArrayField position;

		public VertexLayout()
		{
			position = doubleArrayField( 3 );
		}
	}
}
