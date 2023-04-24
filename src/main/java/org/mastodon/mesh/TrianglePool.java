package org.mastodon.mesh;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;

public class TrianglePool extends Pool< Triangle, ByteMappedElement >
{

	public static final FaceLayout layout = new FaceLayout();

	final IntAttribute< Triangle > vertex0;

	final IntAttribute< Triangle > vertex1;

	final IntAttribute< Triangle > vertex2;

	final FloatArrayAttribute< Triangle > normal;

	final TriMesh mesh;

	final VertexPool vertexPool;

	TrianglePool( final int initialCapacity, final VertexPool vertexPool, final TriMesh mesh )
	{
		super( initialCapacity, layout, Triangle.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.vertexPool = vertexPool;
		this.mesh = mesh;
		this.vertex0 = new IntAttribute<>( layout.vertex0, this );
		this.vertex1 = new IntAttribute<>( layout.vertex1, this );
		this.vertex2 = new IntAttribute<>( layout.vertex2, this );
		this.normal = new FloatArrayAttribute<>( layout.normal, this );
	}

	@Override
	protected Triangle createEmptyRef()
	{
		return new Triangle( this );
	}

	@Override
	public Triangle create( final Triangle face )
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
