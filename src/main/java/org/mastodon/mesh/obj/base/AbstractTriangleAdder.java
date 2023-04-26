package org.mastodon.mesh.obj.base;

import org.mastodon.mesh.obj.TriMeshI;
import org.mastodon.mesh.obj.TriangleAdderI;
import org.mastodon.mesh.util.GeomUtil;

/**
 * Facility to add a face to a {@link TriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class AbstractTriangleAdder<
					V extends AbstractVertex< V, E, T, ?, ? >,
					E extends AbstractHalfEdge< E, V, T, ?, ?, ? >,
					T extends AbstractTriangle< T, V, ?, ? > >
		implements TriangleAdderI< T, V >
{

	private final float[] crossProduct;

	private final E eref1;

	private final E eref2;

	private final E eref3;

	private final E eref4;

	private final TriMeshI< V, E, T > mesh;

	protected AbstractTriangleAdder( final TriMeshI< V, E, T > mesh )
	{
		this.mesh = mesh;
		this.crossProduct = new float[ 3 ];
		this.eref1 = mesh.edgeRef();
		this.eref2 = mesh.edgeRef();
		this.eref3 = mesh.edgeRef();
		this.eref4 = mesh.edgeRef();
	}

	/**
	 * Assumes all vertices are in proper CCW order. FIXME: What if they are
	 * not?
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @return
	 */
	@Override
	public T add( final V v0, final V v1, final V v2, final T ref )
	{
		// Sanity check.
		final E e1 = mesh.getEdge( v0, v1, eref1 );
		if ( e1 != null )
			return null;
		final E e2 = mesh.getEdge( v1, v2, eref2 );
		if ( e2 != null )
			return null;
		final E e3 = mesh.getEdge( v2, v0, eref3 );
		if ( e3 != null )
			return null;

		GeomUtil.crossProduct( v1, v0, v2, v0, crossProduct );
		final boolean colinear = crossProduct[ 0 ] == 0. && crossProduct[ 1 ] == 0. && crossProduct[ 2 ] == 0.;
		if ( colinear )
			throw new IllegalArgumentException( "Cannot create a face with colinear vertices." );

		// Compute normals.
		GeomUtil.normalize( crossProduct );
		final float nx = crossProduct[ 0 ];
		final float ny = crossProduct[ 1 ];
		final float nz = crossProduct[ 2 ];

		// Object to set.
		final T triangle = mesh.addTriangle( ref );

		// Create face with proper order.
		triangle.init( v0, v1, v2, nx, ny, nz );

		// Create half edges.
		final E eAB = mesh.addEdge( v0, v1, eref1 );
		final E eBC = mesh.addEdge( v1, v2, eref2 );
		final E eCA = mesh.addEdge( v2, v0, eref3 );

		final E twinAB = mesh.getEdge( v1, v0, eref4 );
		eAB.init( eBC, eCA, twinAB, triangle );

		final E twinBC = mesh.getEdge( v2, v1, eref4 );
		eBC.init( eCA, eAB, twinBC, triangle );

		final E twinCA = mesh.getEdge( v0, v2, eref4 );
		eCA.init( eAB, eBC, twinCA, triangle );

		return triangle;
	}

	@Override
	public boolean exists( final V v0, final V v1, final V v2 )
	{
		final boolean is01connected = ( null != mesh.getEdge( v0, v1, eref1 ) || ( null != mesh.getEdge( v1, v0, eref1 ) ) );
		final boolean is12connected = ( null != mesh.getEdge( v1, v2, eref2 ) || ( null != mesh.getEdge( v2, v1, eref2 ) ) );
		final boolean is20connected = ( null != mesh.getEdge( v2, v0, eref3 ) || ( null != mesh.getEdge( v0, v2, eref3 ) ) );
		return is01connected && is12connected && is20connected;
	}

	/**
	 * Must not be used after this call.
	 */
	@Override
	public void releaseRefs()
	{
		mesh.edges().releaseRef( eref1 );
		mesh.edges().releaseRef( eref2 );
		mesh.edges().releaseRef( eref3 );
		mesh.edges().releaseRef( eref4 );
	}
}
