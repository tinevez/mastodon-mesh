package org.mastodon.mesh;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;

public class FacePool extends Pool< Face, ByteMappedElement >
{

	public static final FaceLayout layout = new FaceLayout();

	final IntAttribute< Face > vertex0;

	final IntAttribute< Face > vertex1;

	final IntAttribute< Face > vertex2;

	final FloatArrayAttribute< Face > normal;

	final TriMesh mesh;

	final VertexPool vertexPool;

	FacePool( final int initialCapacity, final VertexPool vertexPool, final TriMesh mesh )
	{
		super( initialCapacity, layout, Face.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.vertexPool = vertexPool;
		this.mesh = mesh;
		this.vertex0 = new IntAttribute<>( layout.vertex0, this );
		this.vertex1 = new IntAttribute<>( layout.vertex1, this );
		this.vertex2 = new IntAttribute<>( layout.vertex2, this );
		this.normal = new FloatArrayAttribute<>( layout.normal, this );
	}

	@Override
	protected Face createEmptyRef()
	{
		return new Face( this );
	}

	@Override
	public Face create( final Face face )
	{
		return super.create( face );
	}

	public static class FaceLayout extends PoolObjectLayout
	{

		final IntField vertex0;

		final IntField vertex1;

		final IntField vertex2;

		FloatArrayField normal;

		public FaceLayout()
		{
			vertex0 = intField();
			vertex1 = intField();
			vertex2 = intField();
			normal = floatArrayField( 3 );
		}
	}
}
