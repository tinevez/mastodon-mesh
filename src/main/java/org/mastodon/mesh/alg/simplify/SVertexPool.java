package org.mastodon.mesh.alg.simplify;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.DoubleArrayAttribute;
import org.mastodon.pool.attributes.RealPointAttribute;

public class SVertexPool extends AbstractVertexPool< SVertex, SHalfEdge, ByteMappedElement >
{

	public static final SVertexLayout layout = new SVertexLayout();

	final RealPointAttribute< SVertex > position;

	final DoubleArrayAttribute< SVertex > q;

	public SVertexPool( final int initialCapacity )
	{
		super( initialCapacity, layout, SVertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.position = new RealPointAttribute<>( layout.position, this );
		this.q = new DoubleArrayAttribute<>( layout.q, this );
	}

	@Override
	protected SVertex createEmptyRef()
	{
		return new SVertex( this );
	}

	public static class SVertexLayout extends AbstractVertexLayout
	{
		final DoubleArrayField position;

		final DoubleArrayField q;

		public SVertexLayout()
		{
			position = doubleArrayField( 3 );
			q = doubleArrayField( 10 );
		}
	}
}
