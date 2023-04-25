package org.mastodon.mesh.alg.simplify;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.ByteAttribute;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;
import org.mastodon.pool.attributes.RealPointAttribute;

public class STrianglePool extends Pool< STriangle, ByteMappedElement >
{

	public static final STriangleLayout layout = new STriangleLayout();

	final IntAttribute< STriangle > vertex0;

	final IntAttribute< STriangle > vertex1;

	final IntAttribute< STriangle > vertex2;

	final FloatArrayAttribute< STriangle > normal;

	final RealPointAttribute< STriangle > position;

	final ByteAttribute< STriangle > dirty;

	final ByteAttribute< STriangle > deleted;

	final STriMesh mesh;

	final SVertexPool vertexPool;

	STrianglePool( final int initialCapacity, final SVertexPool vertexPool, final STriMesh mesh )
	{
		super( initialCapacity, layout, STriangle.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.vertexPool = vertexPool;
		this.mesh = mesh;
		this.vertex0 = new IntAttribute<>( layout.vertex0, this );
		this.vertex1 = new IntAttribute<>( layout.vertex1, this );
		this.vertex2 = new IntAttribute<>( layout.vertex2, this );
		this.normal = new FloatArrayAttribute<>( layout.normal, this );
		this.position = new RealPointAttribute<>( layout.position, this );
		this.dirty = new ByteAttribute<>( layout.dirty, this );
		this.deleted = new ByteAttribute<>( layout.deleted, this );
	}

	@Override
	protected STriangle createEmptyRef()
	{
		return new STriangle( this );
	}

	@Override
	public STriangle create( final STriangle face )
	{
		return super.create( face );
	}

	public static class STriangleLayout extends PoolObjectLayout
	{

		final IntField vertex0;

		final IntField vertex1;

		final IntField vertex2;

		final FloatArrayField normal;

		final DoubleArrayField position;

		final ByteField dirty;

		final ByteField deleted;

		public STriangleLayout()
		{
			vertex0 = intField();
			vertex1 = intField();
			vertex2 = intField();
			normal = floatArrayField( 3 );
			position = doubleArrayField( 3 );
			dirty = byteField();
			deleted = byteField();
		}
	}
}
