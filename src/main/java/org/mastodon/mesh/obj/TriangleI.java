package org.mastodon.mesh.obj;

public interface TriangleI< V extends VertexI< ? > >
{

	public V getVertex0( final V ref );

	public V getVertex1( final V ref );

	public V getVertex2( final V ref );

	public void normal( final float[] normal );

	public int v0();

	public int v1();

	public int v2();

	public void init( V v0, V v1, V v2, float nx, float ny, float nz );
}
