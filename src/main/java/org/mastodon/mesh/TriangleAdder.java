package org.mastodon.mesh;

import org.mastodon.pool.PoolCollectionWrapper;

import net.imglib2.RealLocalizable;

/**
 * Facility to add a face to a {@link TriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class TriangleAdder
{

	private final PoolCollectionWrapper< Triangle > triangles;

	private final float[] crossProduct;

	private final HalfEdge eref1;

	private final HalfEdge eref2;

	private final HalfEdge eref3;

	private final HalfEdge eref4;

	private final TriMesh mesh;

	TriangleAdder( final TriMesh mesh )
	{
		this.mesh = mesh;
		this.triangles = mesh.triangles();
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
	public Triangle add(
			final Vertex v0,
			final Vertex v1,
			final Vertex v2,
			final Triangle ref )
	{
		// Sanity check.
		final HalfEdge e1 = mesh.getEdge( v0, v1, eref1 );
		if ( e1 != null )
			return null;
		final HalfEdge e2 = mesh.getEdge( v1, v2, eref2 );
		if ( e2 != null )
			return null;
		final HalfEdge e3 = mesh.getEdge( v2, v0, eref3 );
		if ( e3 != null )
			return null;

		// Object to set.
		final Triangle triangle = mesh.trianglePool.create( ref );

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
		final HalfEdge eAB = mesh.addEdge( v0, v1, eref1 );
		final HalfEdge eBC = mesh.addEdge( v1, v2, eref2 );
		final HalfEdge eCA = mesh.addEdge( v2, v0, eref3 );

		final HalfEdge twinAB = mesh.getEdge( v1, v0, eref4 );
		eAB.init( eBC, eCA, twinAB, triangle );

		final HalfEdge twinBC = mesh.getEdge( v2, v1, eref4 );
		eBC.init( eCA, eAB, twinBC, triangle );

		final HalfEdge twinCA = mesh.getEdge( v0, v2, eref4 );
		eCA.init( eAB, eBC, twinCA, triangle );

		return triangle;
	}

	private static final float dot( final float x1, final float y1, final float z1, final float x2, final float y2, final float z2 )
	{
		return x1 * x2 + y1 * y2 + z1 * z2;
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

	public boolean exists( final Vertex v0, final Vertex v1, final Vertex v2 )
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

	private void sortVertices( final Vertex v0, final Vertex v1, final Vertex v2, final RealLocalizable center )
	{
		final float ox = v0.getFloatPosition( 0 ) - center.getFloatPosition( 0 );
		final float oy = v0.getFloatPosition( 1 ) - center.getFloatPosition( 1 );
		final float oz = v0.getFloatPosition( 2 ) - center.getFloatPosition( 2 );

		final float dot = dot( ox, oy, oz, crossProduct[ 0 ], crossProduct[ 1 ], crossProduct[ 2 ] );

		final Vertex vb;
		final Vertex vc;
		if ( dot == 0. )
		{
			final float dx = v2.getFloatPosition( 0 ) - v1.getFloatPosition( 0 );
			if ( dx == 0. )
			{
				final float dy = v2.getFloatPosition( 1 ) - v1.getFloatPosition( 1 );
				if ( dy == 0. )
				{
					final float dz = v2.getFloatPosition( 2 ) - v1.getFloatPosition( 2 );
					if ( dz == 0. )
						throw new IllegalArgumentException( "Cannot create a face with two identical vertices." );

					vb = ( dz < 0. ) ? v2 : v1;
					vc = ( dz < 0. ) ? v1 : v2;
				}
				else
				{
					vb = ( dy < 0. ) ? v2 : v1;
					vc = ( dy < 0. ) ? v1 : v2;
				}
			}
			else
			{
				vb = ( dx < 0. ) ? v2 : v1;
				vc = ( dx < 0. ) ? v1 : v2;
			}
		}
		else
		{
			vb = ( dot < 0. ) ? v2 : v1;
			vc = ( dot < 0. ) ? v1 : v2;
		}
	}
}
