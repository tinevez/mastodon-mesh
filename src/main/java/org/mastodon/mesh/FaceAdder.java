package org.mastodon.mesh;

import net.imglib2.RealLocalizable;

/**
 * Facility to add a face to a {@link TriMesh}. Built to ensure that the ref
 * objects will not be used outside of this class.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class FaceAdder
{

	private final TrianglePool triangles;

	private final float[] crossProduct;

	private final Triangle ref;

	private final HalfEdge eref1;

	private final HalfEdge eref2;

	private final HalfEdge eref3;

	private final HalfEdge eref4;

	FaceAdder( final TriMesh mesh )
	{
		this.triangles = mesh.triangles();
		this.crossProduct = new float[ 3 ];
		this.ref = triangles.createEmptyRef();
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
			final Vertex v2 )
	{
		// Object to set.
		final Triangle face = triangles.create( ref );

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
		triangles.vertex0.setQuiet( face, v0.getInternalPoolIndex() );
		triangles.vertex1.setQuiet( face, v1.getInternalPoolIndex() );
		triangles.vertex2.setQuiet( face, v2.getInternalPoolIndex() );
		triangles.normal.setQuiet( face, 0, nx );
		triangles.normal.setQuiet( face, 1, ny );
		triangles.normal.setQuiet( face, 2, nz );

		// Sanity check.
		final HalfEdge e1 = triangles.mesh.getEdge( v0, v1, eref1 );
		if ( e1 != null )
			throw new IllegalArgumentException( "Found an already existing edge from " + v0 + " to " + v1 );
		final HalfEdge e2 = triangles.mesh.getEdge( v1, v2, eref2 );
		if ( e2 != null )
			throw new IllegalArgumentException( "Found an already existing edge from " + v1 + " to " + v2 );
		final HalfEdge e3 = triangles.mesh.getEdge( v2, v0, eref3 );
		if ( e3 != null )
			throw new IllegalArgumentException( "Found an already existing edge from " + v2 + " to " + v0 );

		// Create half edges.
		final HalfEdge eAB = triangles.mesh.addEdge( v0, v1, eref1 );
		final HalfEdge eBC = triangles.mesh.addEdge( v1, v2, eref2 );
		final HalfEdge eCA = triangles.mesh.addEdge( v2, v0, eref3 );

		final HalfEdge twinAB = triangles.mesh.getEdge( v1, v0, eref4 );
		eAB.init( eBC, eCA, twinAB, face );

		final HalfEdge twinBC = triangles.mesh.getEdge( v2, v1, eref4 );
		eBC.init( eCA, eAB, twinBC, face );

		final HalfEdge twinCA = triangles.mesh.getEdge( v0, v2, eref4 );
		eCA.init( eAB, eBC, twinCA, face );

		return face;
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

	/**
	 * Must not be used after this call.
	 */
	public void releaseRefs()
	{
		triangles.releaseRef( ref );
		triangles.mesh.edges().releaseRef( eref1 );
		triangles.mesh.edges().releaseRef( eref2 );
		triangles.mesh.edges().releaseRef( eref3 );
		triangles.mesh.edges().releaseRef( eref4 );
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
