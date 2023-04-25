package org.mastodon.mesh.alg.simplify;

/**
 * Facility to add a face to a {@link STriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class STriangleAdder
{

	private final float[] crossProduct;

	private final SHalfEdge eref1;

	private final SHalfEdge eref2;

	private final SHalfEdge eref3;

	private final SHalfEdge eref4;

	private final STriMesh mesh;

	STriangleAdder( final STriMesh mesh )
	{
		this.mesh = mesh;
		this.crossProduct = new float[ 3 ];
		this.eref1 = mesh.edgeRef();
		this.eref2 = mesh.edgeRef();
		this.eref3 = mesh.edgeRef();
		this.eref4 = mesh.edgeRef();
	}

	/**
	 * Assumes all vertices are in proper CCW order.
	 *
	 * @param v0
	 * @param v1
	 * @param v2
	 * @return
	 */
	public STriangle add(
			final SVertex v0,
			final SVertex v1,
			final SVertex v2,
			final STriangle ref )
	{
		// Sanity check.
		final SHalfEdge e1 = mesh.getEdge( v0, v1, eref1 );
		if ( e1 != null )
			return null;
		final SHalfEdge e2 = mesh.getEdge( v1, v2, eref2 );
		if ( e2 != null )
			return null;
		final SHalfEdge e3 = mesh.getEdge( v2, v0, eref3 );
		if ( e3 != null )
			return null;

		// Object to set.
		final STriangle triangle = mesh.trianglePool.create( ref );

		final float dx1 = v1.getFloatPosition( 0 ) - v0.getFloatPosition( 0 );
		final float dy1 = v1.getFloatPosition( 1 ) - v0.getFloatPosition( 1 );
		final float dz1 = v1.getFloatPosition( 2 ) - v0.getFloatPosition( 2 );

		final float dx2 = v2.getFloatPosition( 0 ) - v0.getFloatPosition( 0 );
		final float dy2 = v2.getFloatPosition( 1 ) - v0.getFloatPosition( 1 );
		final float dz2 = v2.getFloatPosition( 2 ) - v0.getFloatPosition( 2 );

		cross( dx1, dy1, dz1, dx2, dy2, dz2, crossProduct );
		final boolean colinear = crossProduct[ 0 ] == 0. && crossProduct[ 1 ] == 0. && crossProduct[ 2 ] == 0.;
		if ( colinear )
			throw new IllegalArgumentException( "Cannot create a face with colinear vertices." );

		// Compute normals.
		normalize( crossProduct );
		final float nx = crossProduct[ 0 ];
		final float ny = crossProduct[ 1 ];
		final float nz = crossProduct[ 2 ];

		// Create face with proper order.
		mesh.trianglePool.vertex0.setQuiet( triangle, v0.getInternalPoolIndex() );
		mesh.trianglePool.vertex1.setQuiet( triangle, v1.getInternalPoolIndex() );
		mesh.trianglePool.vertex2.setQuiet( triangle, v2.getInternalPoolIndex() );
		mesh.trianglePool.normal.setQuiet( triangle, 0, nx );
		mesh.trianglePool.normal.setQuiet( triangle, 1, ny );
		mesh.trianglePool.normal.setQuiet( triangle, 2, nz );

		// Create half edges.
		final SHalfEdge eAB = mesh.addEdge( v0, v1, eref1 );
		final SHalfEdge eBC = mesh.addEdge( v1, v2, eref2 );
		final SHalfEdge eCA = mesh.addEdge( v2, v0, eref3 );

		final SHalfEdge twinAB = mesh.getEdge( v1, v0, eref4 );
		eAB.init( eBC, eCA, twinAB, triangle );

		final SHalfEdge twinBC = mesh.getEdge( v2, v1, eref4 );
		eBC.init( eCA, eAB, twinBC, triangle );

		final SHalfEdge twinCA = mesh.getEdge( v0, v2, eref4 );
		eCA.init( eAB, eBC, twinCA, triangle );

		return triangle;
	}

	private static final void cross( final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float[] out )
	{
		out[ 0 ] = y1 * z2 - z1 * y2;
		out[ 1 ] = -x1 * z2 + z1 * x2;
		out[ 2 ] = x1 * y2 - y1 * x2;
	}

	private static final void normalize( final float[] v )
	{
		final float x = v[ 0 ];
		final float y = v[ 1 ];
		final float z = v[ 2 ];
		final float l2 = x * x + y * y + z * z;
		final float l = ( float ) Math.sqrt( l2 );
		v[ 0 ] /= l;
		v[ 1 ] /= l;
		v[ 2 ] /= l;
	}

	public boolean exists( final SVertex v0, final SVertex v1, final SVertex v2 )
	{
		final boolean is01connected = ( null != mesh.getEdge( v0, v1, eref1 ) || ( null != mesh.getEdge( v1, v0, eref1 ) ) );
		final boolean is12connected = ( null != mesh.getEdge( v1, v2, eref2 ) || ( null != mesh.getEdge( v2, v1, eref2 ) ) );
		final boolean is20connected = ( null != mesh.getEdge( v2, v0, eref3 ) || ( null != mesh.getEdge( v0, v2, eref3 ) ) );
		return is01connected && is12connected && is20connected;
	}

	/**
	 * Must not be used after this call.
	 */
	public void releaseRefs()
	{
		mesh.edges().releaseRef( eref1 );
		mesh.edges().releaseRef( eref2 );
		mesh.edges().releaseRef( eref3 );
		mesh.edges().releaseRef( eref4 );
	}
}
