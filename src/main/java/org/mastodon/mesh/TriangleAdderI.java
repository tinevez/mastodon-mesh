package org.mastodon.mesh;

/**
 * Facility to add a face to a {@link TriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 */
public interface TriangleAdderI< T extends TriangleI< V >, V extends VertexI< ? > >
{

	public T add( final V v0, final V v1, final V v2, final T ref );

	public boolean exists( final V v0, final V v1, final V v2 );

	/**
	 * Must not be used after this call.
	 */
	public void releaseRefs();

}
