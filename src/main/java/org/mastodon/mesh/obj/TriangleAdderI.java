package org.mastodon.mesh.obj;

import org.mastodon.mesh.obj.core.TriMesh;

/**
 * Facility to add a face to a {@link TriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 */
public interface TriangleAdderI< T extends TriangleI< V, ? >, V extends VertexI< ? > >
{

	public T add( final V v0, final V v1, final V v2, final T ref );

	/**
	 * Convenience method to add a triangle based on the vertices id.
	 * 
	 * @param v0id
	 *            the id of the first vertex of the triangle.
	 * @param v1id
	 *            the id of the first vertex of the triangle.
	 * @param v2id
	 *            the id of the first vertex of the triangle.
	 * @param ref
	 *            a ref object used to return the newly triangle.
	 * @return a new triangle.
	 */
	public T add( final int v0id, final int v1id, final int v2id, final T ref );

	public boolean exists( final V v0, final V v1, final V v2 );

	/**
	 * Must not be used after this call.
	 */
	public void releaseRefs();

}
