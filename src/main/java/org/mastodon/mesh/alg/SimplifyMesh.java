package org.mastodon.mesh.alg;

import java.util.Vector;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.mesh.HalfEdgeI;
import org.mastodon.mesh.TriMeshI;
import org.mastodon.mesh.TriangleAdderI;
import org.mastodon.mesh.TriangleI;
import org.mastodon.mesh.VertexI;
import org.mastodon.mesh.obj.core.TriMesh;

import net.imglib2.RealPoint;

/**
 * Fast quadratic mesh simplification.
 * <p>
 * The paper that started this is the seminal article by Garland and Heckbert on
 * <a href="http://www.cs.cmu.edu/~./garland/Papers/quadrics.pdf">quadratic mesh
 * decimation</a> (1995).
 * 
 * But there are much more info in
 * <a href="https://mgarland.org/files/papers/thesis-onscreen.pdf">Garland's
 * PhD</a>.
 * 
 * <p>
 * Then, in 2014 Sven Forstmann proposed an implementation of this with speed
 * improvement called "Fast Quadric Mesh Simplification". The speed improvement
 * relies on avoiding sorting triangles to know which to delete. The C++ code
 * repo is <a href=
 * "https://github.com/sp4cerat/Fast-Quadric-Mesh-Simplification">here</a> (MIT
 * license). The <a href=
 * "https://github.com/sp4cerat/Fast-Quadric-Mesh-Simplification/blob/master/src.cmd/Simplify.h">simplify
 * function</a>.
 * <p>
 * Then James Khan (JayFella) ported it to Java in 2018, using the JMonkeyEngine
 * data structure. The port is discussed <a href=
 * "https://hub.jmonkeyengine.org/t/isosurface-mesh-simplifier/41046/1">here</a>.
 * The code by JayFella (MIT license) is on a
 * <a href="https://gist.github.com/jayfella/00a328b2dbdf6304078a821143b7aef7">a
 * gist</a>.
 * <p>
 * It was then ported by Deborah Schmidt in 2020, to use the ImageJ-Mesh data
 * structure instead of JMesh. Then finally I (JY) simply translated Deborah's
 * code to use a mastodon TriMesh instead.
 * 
 * @author James Khan / jayfella
 * @author Deborah Schmidt / frauzufall
 * @author Jean-Yves Tinevez
 */
public class SimplifyMesh< V extends VertexI< E >, E extends HalfEdgeI< E, V, T >, T extends TriangleI< V > >
{

	private final Vector< Triangle > triangles = new Vector<>();

	private final Vector< Vertex > vertices = new Vector<>();

	private final Vector< Ref > refs = new Vector<>();

	private final TriMeshI< V, E, T > inMesh;

	private final Point p = new Point();

	public SimplifyMesh( final TriMeshI< V, E, T > mesh )
	{
		this.inMesh = mesh;
	}

	private void readMesh()
	{
		vertices.clear();
		for ( final V inV : inMesh.vertices() )
		{
			final Point simpleVertex = new Point();
			simpleVertex.setPosition( inV );
			final Vertex v = new Vertex( simpleVertex );
			vertices.add( v );
		}

		triangles.clear();
		refs.clear();
		int triIndex = 0;
		for ( final T tria : inMesh.triangles() )
		{
			final Triangle t = new Triangle(
					tria.v0(),
					tria.v1(),
					tria.v2() );

			triangles.add( t );

			refs.add( new Ref( triIndex, t.v[ 0 ] ) );
			refs.add( new Ref( triIndex, t.v[ 1 ] ) );
			refs.add( new Ref( triIndex, t.v[ 2 ] ) );
			triIndex++;
		}
	}

	/**
	 * Begins the simplification process.
	 *
	 * @param target_percent
	 *            the amount in percent to attempt to achieve. For example:
	 *            0.25f would result in creating a mesh with 25% of triangles
	 *            contained in the original.
	 * @param agressiveness
	 *            sharpness to increase the threshold. 5..8 are good numbers.
	 *            more iterations yield higher quality. Minimum 4 and maximum 20
	 *            are recommended.
	 */
	public TriMesh simplify( final double target_percent, final double agressiveness )
	{
		final int target_count = ( int ) ( inMesh.triangles().size() * target_percent );
		return simplify( target_count, agressiveness );
	}

	/**
	 * Begins the simplification process.
	 *
	 * @param target_count
	 *            the amount of triangles to attempt to achieve.
	 * @param agressiveness
	 *            sharpness to increase the threshold. 5..8 are good numbers.
	 *            more iterations yield higher quality. Minimum 4 and maximum 20
	 *            are recommended.
	 */
	private TriMesh simplify( final int targetCount, final double agressiveness )
	{

		// init

		// re-read the mesh every time we simplify to start with the original
		// data.
		readMesh();

		/*
		 * System.out.println(String.format("Simplify Target: %d of %d (%d%%)",
		 * target_count, triangles.size(), target_count * 100 /
		 * triangles.size()));
		 * 
		 * final long timeStart = System.currentTimeMillis();
		 */

		triangles.forEach( t -> t.deleted = false );

		// main iteration loop

		int deletedTriangles = 0;

		final Vector< Boolean > deleted0 = new Vector<>();
		final Vector< Boolean > deleted1 = new Vector<>();

		final int triangleCount = triangles.size();

		// final Vector3f p = new Vector3f();
		p.setPosition( new long[] { 0, 0, 0 } );

		for ( int iteration = 0; iteration < 1000; iteration++ )
		{

			/*
			 * System.out.println(String.format(
			 * "Iteration %02d -> triangles [ deleted: %d : count: %d | removed: %d%% ]"
			 * , iteration, deleted_triangles, triangle_count -
			 * deleted_triangles, (deleted_triangles * 100 / triangle_count) ));
			 */

			// target number of triangles reached ? Then break
			if ( triangleCount - deletedTriangles <= targetCount )
				break;

			// update mesh once in a while
			if ( iteration % 5 == 0 )
				updateMesh( iteration );

			// clear dirty flag
			triangles.forEach( t -> t.dirty = false );

			/*
			 * All triangles with edges below the threshold will be removed.
			 * 
			 * The following numbers works well for most models. If it does not,
			 * try to adjust the 3 parameters.
			 */
			final double threshold = 0.000000001d * Math.pow( iteration + 3d, agressiveness );

			// Remove vertices & mark deleted triangles.
			for ( int i = triangles.size() - 1; i >= 0; i-- )
			{
				final Triangle t = triangles.get( i );

				/*
				 * Don't delete triangle if error nbr 4 is larger than
				 * threshold, or already deleted, or dirty.
				 */
				if ( t.err[ 3 ] > threshold || t.deleted || t.dirty )
					continue;

				for ( int j = 0; j < 3; j++ )
				{

					// Don't delete vertex if error larger than threshold.
					if ( t.err[ j ] >= threshold )
						continue;

					// Edge indices.
					final int i0 = t.v[ j ];
					final int i1 = t.v[ ( j + 1 ) % 3 ];

					// Source and target vertices.
					final Vertex v0 = vertices.get( i0 );
					final Vertex v1 = vertices.get( i1 );

					// Don't delete if edge is border edge.
					if ( v0.border || v1.border )
						continue;

					// Compute vertex to collapse to
					p.setPosition( new long[] { 0, 0, 0 } );
					calculateError( i0, i1, p );

					deleted0.setSize( v0.tcount ); // normals temporarily
					deleted1.setSize( v1.tcount ); // normals temporarily
					// deleted0.trimToSize();
					// deleted1.trimToSize();

					// Don't remove if flipped.
					if ( flipped( p, i1, v0, deleted0 ) )
						continue;

					if ( flipped( p, i0, v1, deleted1 ) )
						continue;

					// Not flipped, so remove edge.
					v0.p.setPosition( p );
					v0.q.addLocal( v1.q );

					final int tstart = refs.size();

					deletedTriangles += updateTriangles( i0, v0, deleted0 );
					deletedTriangles += updateTriangles( i0, v1, deleted1 );

					final int tcount = refs.size() - tstart;

					v0.tstart = tstart;
					v0.tcount = tcount;

					break;
				}

				// Done?
				if ( triangleCount - deletedTriangles <= targetCount )
					break;
			}
		}

		// Clean up mesh.
		compactMesh();

		// ready
		/*
		 * long timeEnd = System.currentTimeMillis();
		 * 
		 * System.out.println(String.
		 * format("Simplify: %d/%d %d%% removed in %d ms", triangle_count -
		 * deleted_triangles, triangle_count, deleted_triangles * 100 /
		 * triangle_count, timeEnd-timeStart));
		 */

		return createSimplifiedMesh();
	}

	/**
	 * Checks if a triangle flips when this edge is removed.
	 * 
	 * @param p
	 * @param i1
	 * @param v0
	 * @param deleted
	 * @return
	 */
	private boolean flipped( final Point p, final int i1, final Vertex v0, final Vector< Boolean > deleted )
	{
		for ( int k = 0; k < v0.tcount; k++ )
		{
			final Ref ref = refs.get( v0.tstart + k );
			final Triangle t = triangles.get( ref.tid );

			if ( t.deleted )
				continue;

			final int s = ref.tvertex;
			final int id1 = t.v[ ( s + 1 ) % 3 ];
			final int id2 = t.v[ ( s + 2 ) % 3 ];

			if ( id1 == i1 || id2 == i1 )
			{
				deleted.set( k, true );
				continue;
			}

			final Point d1 = vertices.get( id1 ).p.subtract( p ).normalizeLocal();
			final Point d2 = vertices.get( id2 ).p.subtract( p ).normalizeLocal();

			if ( Math.abs( d1.dot( d2 ) ) > 0.9999d )
				return true;

			final Point n = new Point( d1 ).crossLocal( d2 ).normalizeLocal();

			deleted.set( k, false );

			if ( n.dot( t.n ) < 0.2d )
				return true;
		}
		return false;
	}

	/**
	 * Updates triangle connections and edge error after a edge is collapsed.
	 * 
	 * @param i0
	 * @param v
	 * @param deleted
	 * @return
	 */
	private int updateTriangles( final int i0, final Vertex v, final Vector< Boolean > deleted )
	{
		int trisDeleted = 0;
		p.setPosition( new long[] { 0, 0, 0 } );
		for ( int k = 0; k < v.tcount; k++ )
		{
			final Ref r = refs.get( v.tstart + k );
			final Triangle t = triangles.get( r.tid );

			if ( t.deleted )
				continue;

			if ( deleted.get( k ) )
			{
				t.deleted = true;
				trisDeleted++;
				continue;
			}

			t.v[ r.tvertex ] = i0;
			t.dirty = true;
			t.err[ 0 ] = calculateError( t.v[ 0 ], t.v[ 1 ], p );
			t.err[ 1 ] = calculateError( t.v[ 1 ], t.v[ 2 ], p );
			t.err[ 2 ] = calculateError( t.v[ 2 ], t.v[ 0 ], p );
			t.err[ 3 ] = Math.min( t.err[ 0 ], Math.min( t.err[ 1 ], t.err[ 2 ] ) );

			refs.add( r );
		}
		return trisDeleted;
	}

	private void updateMesh( final int iteration )
	{

		if ( iteration > 0 )
		{ // compact triangles
			int dst = 0;
			for ( int i = 0; i < triangles.size(); i++ )
				if ( !triangles.get( i ).deleted )
					triangles.set( dst++, triangles.get( i ) );

			triangles.setSize( dst );
		}

		/*
		 * Init Quadrics by Plane & Edge Errors
		 * 
		 * required at the beginning ( iteration == 0 ) recomputing during the
		 * simplification is not required, but mostly improves the result for
		 * closed meshes
		 */
		if ( iteration == 0 )
		{
			vertices.forEach( v -> v.q.set( new SymmetricMatrix( 0.0d ) ) );

			// for (Triangle t : triangles) {
			p.setPosition( new long[] { 0, 0, 0 } );
			triangles.forEach( t -> {

				// Triangle normal.
				final Point[] tVerticesPos = new Point[] {
						vertices.get( t.v[ 0 ] ).p,
						vertices.get( t.v[ 1 ] ).p,
						vertices.get( t.v[ 2 ] ).p,
				};
				final Point n = tVerticesPos[ 1 ].subtract( tVerticesPos[ 0 ] )
						.crossLocal( tVerticesPos[ 2 ].subtract( tVerticesPos[ 0 ] ) )
						.normalizeLocal();

				// Store normal.
				t.n.setPosition( n );

				// Update triangle vertices Qs.
				final SymmetricMatrix deltaN = new SymmetricMatrix( n.getFloatPosition( 0 ), n.getFloatPosition( 1 ), n.getFloatPosition( 2 ), -n.dot( tVerticesPos[ 0 ] ) );
				for ( int j = 0; j < 3; j++ )
				{
					final Vertex vj = vertices.get( t.v[ j ] );
					final SymmetricMatrix q = vj.q;
					q.set( q.add( deltaN ) );
				}

				// Compute errors.
				for ( int j = 0; j < 3; j++ )
					t.err[ j ] = calculateError( t.v[ j ], t.v[ ( j + 1 ) % 3 ], p );

				t.err[ 3 ] = Math.min( t.err[ 0 ], Math.min( t.err[ 1 ], t.err[ 2 ] ) );
			} );
		} // End of init.

		// Init Reference ID list
		vertices.forEach( v -> {
			v.tstart = 0;
			v.tcount = 0;
		} );

		triangles.forEach( t -> {
			vertices.get( t.v[ 0 ] ).tcount++;
			vertices.get( t.v[ 1 ] ).tcount++;
			vertices.get( t.v[ 2 ] ).tcount++;
		} );

		int tstart = 0;
		for ( final Vertex v : vertices )
		{
			v.tstart = tstart;
			tstart += v.tcount;
			v.tcount = 0;
		}

		// Write References
		refs.setSize( triangles.size() * 3 );
		for ( int i = 0; i < triangles.size(); i++ )
		{
			final Triangle t = triangles.get( i );
			for ( int j = 0; j < 3; j++ )
			{
				final Vertex v = vertices.get( t.v[ j ] );
				final int index = v.tstart + v.tcount;
				final Ref ref = refs.get( index );
				ref.tid = i;
				ref.tvertex = j;
				v.tcount++;
			}
		}

		// Identify boundary : vertices[].border=0,1
		if ( iteration == 0 )
		{
			final Vector< Integer > vcount = new Vector<>();
			final Vector< Integer > vids = new Vector<>();

			vertices.forEach( v -> v.border = false );
			vertices.forEach( v -> {

				vcount.clear();
				vids.clear();

				// Iterate over all the triangles of this vertex.
				for ( int j = 0; j < v.tcount; j++ )
				{
					int k = refs.get( v.tstart + j ).tid;
					final Triangle t = triangles.get( k );
					for ( k = 0; k < 3; k++ )
					{
						int ofs = 0;
						final int id = t.v[ k ];
						while ( ofs < vcount.size() )
						{
							if ( vids.get( ofs ) == id )
								break;

							ofs++;
						}

						if ( ofs == vcount.size() )
						{
							vcount.add( 1 );
							vids.add( id );
						}
						else
						{
							vcount.set( ofs, vcount.get( ofs ) + 1 );
						}
					}
				}

				for ( int j = 0; j < vcount.size(); j++ )
					if ( vcount.get( j ) == 1 )
						vertices.get( vids.get( j ) ).border = true;
			} );
		}
	}

	/**
	 * Finally compact mesh before exiting.
	 */
	private void compactMesh()
	{
		int dst = 0;
		vertices.forEach( v -> v.tcount = 0 );
		for ( int i = 0; i < triangles.size(); i++ )
		{
			if ( !triangles.get( i ).deleted )
			{
				final Triangle t = triangles.get( i );
				triangles.set( dst++, t );
				for ( int j = 0; j < 3; j++ )
					vertices.get( t.v[ j ] ).tcount = 1;
			}
		}

		triangles.setSize( dst );
		dst = 0;
		for ( final Vertex vertice : vertices )
		{
			if ( vertice.tcount != 0 )
			{
				vertice.tstart = dst;
				vertices.get( dst ).p.setPosition( vertice.p );
				dst++;
			}
		}

		for ( final Triangle t : triangles )
			for ( int j = 0; j < 3; j++ )
				t.v[ j ] = vertices.get( t.v[ j ] ).tstart;

		vertices.setSize( dst );
	}

	/**
	 * Error between vertex and Quadric.
	 * 
	 * @param q
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static final double vertexError( final SymmetricMatrix q, final double x, final double y, final double z )
	{
		return q.getValue( 0 ) * x * x + 2
				* q.getValue( 1 ) * x * y + 2
						* q.getValue( 2 ) * x * z
				+ 2
						* q.getValue( 3 ) * x
				+ q.getValue( 4 ) * y * y + 2
						* q.getValue( 5 ) * y * z
				+ 2
						* q.getValue( 6 ) * y
				+ q.getValue( 7 ) * z * z + 2
						* q.getValue( 8 ) * z
				+ q.getValue( 9 );
	}

	/**
	 * Error for one edge.
	 * 
	 * @param sourceVid
	 *            source vertex id of the edge.
	 * @param targetVid
	 *            target vertex id of the edge.
	 * @param result
	 *            storage for resulting position (modified).
	 * @return error value.
	 */
	private double calculateError( final int sourceVid, final int targetVid, final Point result )
	{
		// compute interpolated vertex
		final Vertex source = vertices.get( sourceVid );
		final Vertex target = vertices.get( targetVid );
		final SymmetricMatrix sourceQ = source.q;
		final SymmetricMatrix targetQ = target.q;
		final SymmetricMatrix q = sourceQ.add( targetQ );
		final boolean border = source.border & target.border;
		final double det = q.det( 0, 1, 2, 1, 4, 5, 2, 5, 7 );

		double error;
		if ( det != 0 && !border )
		{
			// q_delta is invertible

			result.setPosition( ( float ) ( -1 / det * ( q.det( 1, 2, 3, 4, 5, 6, 5, 7, 8 ) ) ), 0 );
			// vx = A41/det(q_delta)

			result.setPosition( ( float ) ( 1 / det * ( q.det( 0, 2, 3, 1, 5, 6, 2, 7, 8 ) ) ), 1 );
			// vy = A42/det(q_delta)

			result.setPosition( ( float ) ( -1 / det * ( q.det( 0, 1, 3, 1, 4, 6, 2, 5, 8 ) ) ), 2 );
			// vz = A43/det(q_delta)

			error = vertexError( q, result.getDoublePosition( 0 ), result.getDoublePosition( 1 ), result.getDoublePosition( 2 ) );
		}
		else
		{
			// det = 0 -> try to find best result
			final Point p1 = vertices.get( sourceVid ).p;
			final Point p2 = vertices.get( targetVid ).p;
			final Point p3 = p1.add( p2 ).divide( 2.0f ); // (p1+p2)/2;
			final double error1 = vertexError( q, p1.getDoublePosition( 0 ), p1.getDoublePosition( 1 ), p1.getDoublePosition( 2 ) );
			final double error2 = vertexError( q, p2.getDoublePosition( 0 ), p2.getDoublePosition( 1 ), p2.getDoublePosition( 2 ) );
			final double error3 = vertexError( q, p3.getDoublePosition( 0 ), p3.getDoublePosition( 1 ), p3.getDoublePosition( 2 ) );

			error = Math.min( error1, Math.min( error2, error3 ) );

			if ( error1 == error )
				result.setPosition( p1 );
			if ( error2 == error )
				result.setPosition( p2 );
			if ( error3 == error )
				result.setPosition( p3 );
		}

		return error;
	}

	private TriMesh createSimplifiedMesh()
	{
		final Point[] vertArray = new Point[ vertices.size() ];

		for ( int i = 0; i < vertArray.length; i++ )
		{
			final Vertex v = vertices.get( i );
			vertArray[ i ] = v.p;
		}

//		final List<Integer> indexList = new ArrayList<>();
//
//		triangles.forEach(t -> {
//			indexList.add(t.v[0]);
//			indexList.add(t.v[1]);
//			indexList.add(t.v[2]);
//		});

		final TriMesh mesh = new TriMesh( vertArray.length, triangles.size() );

		final org.mastodon.mesh.obj.core.Vertex vref0 = mesh.vertexRef();
		final org.mastodon.mesh.obj.core.Vertex vref1 = mesh.vertexRef();
		final org.mastodon.mesh.obj.core.Vertex vref2 = mesh.vertexRef();
		final TriangleAdderI< org.mastodon.mesh.obj.core.Triangle, org.mastodon.mesh.obj.core.Vertex > adder = mesh.triangleAdder();
		final org.mastodon.mesh.obj.core.Triangle tref = mesh.triangleRef();
		final IntRefMap< org.mastodon.mesh.obj.core.Vertex > map = RefMaps.createIntRefMap( mesh.vertices(), -1, vertArray.length );
		try
		{
			for ( int i = 0; i < vertArray.length; i++ )
			{
				final org.mastodon.mesh.obj.core.Vertex v = mesh.addVertex( vref0 );
				v.setPosition( vertArray[ i ] );
				map.put( i, v );
			}

			for ( final Triangle t : triangles )
			{
				final org.mastodon.mesh.obj.core.Vertex v0 = map.get( t.v[ 0 ], vref0 );
				final org.mastodon.mesh.obj.core.Vertex v1 = map.get( t.v[ 1 ], vref1 );
				final org.mastodon.mesh.obj.core.Vertex v2 = map.get( t.v[ 2 ], vref2 );
				adder.add( v0, v1, v2, tref );
			}
			return mesh;
		}
		finally
		{
			mesh.releaseRef( tref );
			adder.releaseRefs();
		}
	}

	private static class Point extends RealPoint
	{

		private Point()
		{
			super( 3 );
		}

		private Point( final Point d1 )
		{
			super( d1 );
		}

		private Point subtract( final Point p )
		{
			final Point res = new Point( this );
			for ( int i = 0; i < numDimensions(); i++ )
			{
				res.setPosition( getDoublePosition( i ) - p.getDoublePosition( i ), i );
			}
			return res;
		}

		private Point normalizeLocal()
		{
			double x = getDoublePosition( 0 );
			double y = getDoublePosition( 1 );
			double z = getDoublePosition( 2 );
			double length = x * x + y * y + z * z;
			if ( length != 1f && length != 0f )
			{
				length = 1.0f / Math.sqrt( length );
				x *= length;
				y *= length;
				z *= length;
			}
			setPosition( new double[] { x, y, z } );
			return this;
		}

		private Point add( final Point p )
		{
			final Point res = new Point( this );
			for ( int i = 0; i < numDimensions(); i++ )
			{
				res.setPosition( getDoublePosition( i ) + p.getDoublePosition( i ), i );
			}
			return res;
		}

		private Point divide( final float v )
		{
			final Point res = new Point( this );
			for ( int i = 0; i < numDimensions(); i++ )
			{
				res.setPosition( getDoublePosition( i ) / v, i );
			}
			return res;
		}

		double dot( final Point p )
		{
			return getDoublePosition( 0 ) * p.getDoublePosition( 0 ) + getDoublePosition( 1 ) * p.getDoublePosition( 1 ) + getDoublePosition( 2 ) * p.getDoublePosition( 2 );
		}

		private Point crossLocal( final Point p )
		{
			final double x = getDoublePosition( 0 );
			final double y = getDoublePosition( 1 );
			final double z = getDoublePosition( 2 );
			final double otherX = p.getDoublePosition( 0 );
			final double otherY = p.getDoublePosition( 1 );
			final double otherZ = p.getDoublePosition( 2 );
			final double tempx = ( y * otherZ ) - ( z * otherY );
			final double tempy = ( z * otherX ) - ( x * otherZ );
			setPosition( x * otherY - ( y * otherX ), 2 );
			setPosition( tempx, 0 );
			setPosition( tempy, 1 );
			return this;
		}
	}

	private static class SymmetricMatrix
	{

		private final double[] m = new double[ 10 ];

		private SymmetricMatrix( final double c )
		{
			for ( int i = 0; i < 10; i++ )
				m[ i ] = c;
		}

		private SymmetricMatrix( final double m11, final double m12, final double m13, final double m14,
				final double m22, final double m23, final double m24,
				final double m33, final double m34,
				final double m44 )
		{
			m[ 0 ] = m11;
			m[ 1 ] = m12;
			m[ 2 ] = m13;
			m[ 3 ] = m14;
			m[ 4 ] = m22;
			m[ 5 ] = m23;
			m[ 6 ] = m24;
			m[ 7 ] = m33;
			m[ 8 ] = m34;
			m[ 9 ] = m44;
		}

		private SymmetricMatrix( final double a, final double b, final double c, final double d )
		{
			m[ 0 ] = a * a;
			m[ 1 ] = a * b;
			m[ 2 ] = a * c;
			m[ 3 ] = a * d;
			m[ 4 ] = b * b;
			m[ 5 ] = b * c;
			m[ 6 ] = b * d;
			m[ 7 ] = c * c;
			m[ 8 ] = c * d;
			m[ 9 ] = d * d;
		}

		private void set( final SymmetricMatrix s )
		{
			System.arraycopy( s.m, 0, m, 0, m.length );
		}

		private final double getValue( final int c )
		{
			return m[ c ];
		}

		private final double det( final int a11, final int a12, final int a13,
				final int a21, final int a22, final int a23,
				final int a31, final int a32, final int a33 )
		{
			return m[ a11 ] * m[ a22 ] * m[ a33 ] + m[ a13 ] * m[ a21 ] * m[ a32 ] + m[ a12 ] * m[ a23 ] * m[ a31 ]
					- m[ a13 ] * m[ a22 ] * m[ a31 ] - m[ a11 ] * m[ a23 ] * m[ a32 ] - m[ a12 ] * m[ a21 ] * m[ a33 ];
		}

		private final SymmetricMatrix add( final SymmetricMatrix n )
		{
			return new SymmetricMatrix(
					m[ 0 ] + n.getValue( 0 ),
					m[ 1 ] + n.getValue( 1 ),
					m[ 2 ] + n.getValue( 2 ),
					m[ 3 ] + n.getValue( 3 ),
					m[ 4 ] + n.getValue( 4 ),
					m[ 5 ] + n.getValue( 5 ),
					m[ 6 ] + n.getValue( 6 ),
					m[ 7 ] + n.getValue( 7 ),
					m[ 8 ] + n.getValue( 8 ),
					m[ 9 ] + n.getValue( 9 ) );
		}

		private void addLocal( final SymmetricMatrix n )
		{
			m[ 0 ] += n.getValue( 0 );
			m[ 1 ] += n.getValue( 1 );
			m[ 2 ] += n.getValue( 2 );
			m[ 3 ] += n.getValue( 3 );
			m[ 4 ] += n.getValue( 4 );
			m[ 5 ] += n.getValue( 5 );
			m[ 6 ] += n.getValue( 6 );
			m[ 7 ] += n.getValue( 7 );
			m[ 8 ] += n.getValue( 8 );
			m[ 9 ] += n.getValue( 9 );
		}
	}

	private static class Vertex
	{

		private final Point p;

		private int tstart;

		private int tcount;

		private final SymmetricMatrix q = new SymmetricMatrix( 0 );

		private boolean border;

		private Vertex( final Point p )
		{
			this.p = new Point( p );
		}
	}

	private static class Triangle
	{

		private final int[] v = new int[ 3 ];

		private final double[] err = new double[ 4 ];

		private boolean deleted = false;

		private boolean dirty = false;

		private final Point n = new Point();

		private Triangle( final int a, final int b, final int c )
		{
			this.v[ 0 ] = a;
			this.v[ 1 ] = b;
			this.v[ 2 ] = c;
		}
	}

	private static class Ref
	{
		private int tid;

		private int tvertex;

		private Ref( final int tid, final int tvertex )
		{
			this.tid = tid;
			this.tvertex = tvertex;
		}
	}
}
