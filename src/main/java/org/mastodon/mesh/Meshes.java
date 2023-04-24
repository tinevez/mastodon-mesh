package org.mastodon.mesh;

import java.util.Iterator;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefSet;
import org.mastodon.mesh.alg.MarchingCubesBooleanType;
import org.mastodon.mesh.alg.MarchingCubesRealType;
import org.mastodon.mesh.alg.MeshConnectedComponents;
import org.mastodon.mesh.alg.RemoveDuplicateVertices;
import org.mastodon.mesh.alg.TwoManifold;

import net.imagej.mesh.Mesh;
import net.imglib2.FinalRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.RealType;

/**
 * Static utilities for TriMesh.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class Meshes
{

	public static FinalRealInterval boundingBox( final TriMesh mesh )
	{
		final double[] min = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
		final double[] max = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for ( final Vertex v : mesh.vertices() )
		{
			final double x = v.getDoublePosition( 0 );
			final double y = v.getDoublePosition( 1 );
			final double z = v.getDoublePosition( 2 );
			if ( x < min[ 0 ] )
				min[ 0 ] = x;
			if ( y < min[ 1 ] )
				min[ 1 ] = y;
			if ( z < min[ 2 ] )
				min[ 2 ] = z;
			if ( x > max[ 3 ] )
				max[ 3 ] = x;
			if ( y > max[ 4 ] )
				max[ 4 ] = y;
			if ( z > max[ 5 ] )
				max[ 5 ] = z;
		}
		return new FinalRealInterval( min, max );

	}

	/**
	 * Does not respect source indices.
	 *
	 * @param source
	 *            the mesh to convert.
	 * @return a new {@link TriMesh}.
	 */
	public static final TriMesh from( final Mesh source )
	{
		final TriMesh mesh = new TriMesh( ( int ) source.vertices().size() );

		// Add vertices.
		final IntRefMap< Vertex > map = RefMaps.createIntRefMap( mesh.vertices(), -1 );
		final Vertex vref = mesh.vertexRef();
		for ( int i = 0; i < source.vertices().size(); i++ )
		{
			final Vertex v = mesh.addVertex( vref );
			v.setPosition( source.vertices().xf( i ), 0 );
			v.setPosition( source.vertices().yf( i ), 1 );
			v.setPosition( source.vertices().zf( i ), 2 );
			map.put( i, v );
		}
		mesh.releaseRef( vref );

		// Add triangles and half-edges.
		final TriangleAdder adder = mesh.triangleAdder();
		final Triangle tref = mesh.triangleRef();
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		final Vertex vref2 = mesh.vertexRef();
		for ( int i = 0; i < source.triangles().size(); i++ )
		{
			final Vertex v0 = map.get( ( int ) source.triangles().vertex0( i ), vref0 );
			final Vertex v1 = map.get( ( int ) source.triangles().vertex1( i ), vref1 );
			final Vertex v2 = map.get( ( int ) source.triangles().vertex2( i ), vref2 );
			adder.add( v0, v1, v2, tref );
		}
		mesh.releaseRef( vref0 );
		mesh.releaseRef( vref1 );
		mesh.releaseRef( vref2 );
		mesh.releaseRef( tref );
		adder.releaseRefs();

		return mesh;
	}

	public static RealPoint center( final TriMesh mesh )
	{
		final RealPoint p = new RealPoint( 0, 0, 0 );
		for ( final Vertex v : mesh.vertices() )
			p.move( v );

		final int nVertices = mesh.vertices().size();
		for ( int d = 0; d < 3; d++ )
			p.setPosition( p.getDoublePosition( d ) / nVertices, d );

		return p;
	}

	/**
	 * Creates mesh e.g. from IterableRegion by using the marching cubes
	 * algorithm.
	 *
	 * @param source
	 *            The binary input image for the marching cubes algorithm.
	 * @return The result mesh of the marching cubes algorithm.
	 */
	public static < T extends BooleanType< T > > TriMesh marchingCubes( final RandomAccessibleInterval< T > source )
	{
		return MarchingCubesBooleanType.calculate( source );
	}

	/**
	 * Creates mesh e.g. from IterableRegion by using the marching cubes
	 * algorithm.
	 *
	 * @param source
	 *            The input image for the marching cubes algorithm.
	 * @param isoLevel
	 *            The threshold to distinguish between foreground and background
	 *            values.
	 * @return The result mesh of the marching cubes algorithm.
	 */
	public static < T extends RealType< T > > TriMesh marchingCubes( final RandomAccessibleInterval< T > source, final double isoLevel )
	{
		return MarchingCubesRealType.calculate( source, isoLevel );
	}

	/**
	 * Creates a new mesh from a given mesh without any duplicate vertices.
	 * Normals and uv coordinates will be ignored and not added to the output
	 * mesh.
	 *
	 * @param mesh
	 *            Source mesh
	 * @param precision
	 *            decimal digits to take into account when comparing mesh
	 *            vertices
	 * @return new mesh without duplicate vertices. The result will not include
	 *         normals or uv coordinates.
	 */
	public static TriMesh removeDuplicateVertices( final TriMesh mesh, final int precision )
	{
		return RemoveDuplicateVertices.calculate( mesh, precision );
	}

	public static void scale( final TriMesh mesh, final double[] scales )
	{
		for ( final Vertex v : mesh.vertices() )
		{
			final double x = v.x();
			v.setPosition( x * scales[ 0 ], 0 );
			final double y = v.y();
			v.setPosition( y * scales[ 1 ], 1 );
			final double z = v.z();
			v.setPosition( z * scales[ 2 ], 2 );
		}
	}

	/**
	 * Returns the set of vertices part of the specified triangle collection.
	 * 
	 * @param triangles
	 *            the collection of triangles.
	 * @return a new {@link RefSet}.
	 */
	public static final RefSet< Vertex > verticesOf( final Iterable< Triangle > triangles, final TriMesh mesh )
	{
		final RefSet< Vertex > vertices = RefCollections.createRefSet( mesh.vertices() );
		final Vertex v0 = mesh.vertexRef();
		final Vertex v1 = mesh.vertexRef();
		final Vertex v2 = mesh.vertexRef();
		try
		{
			for ( final Triangle t : triangles )
			{
				vertices.add( t.getVertex0( v0 ) );
				vertices.add( t.getVertex1( v1 ) );
				vertices.add( t.getVertex2( v2 ) );
			}
			return vertices;
		}
		finally
		{
			mesh.releaseRef( v0 );
			mesh.releaseRef( v1 );
			mesh.releaseRef( v2 );
		}
	}

	/**
	 * Returns the number of connected components in this mesh.
	 * 
	 * @param mesh
	 *            the mesh.
	 * @return the number of connected components.
	 */
	public static int nConnectedComponents( final TriMesh mesh )
	{
		return MeshConnectedComponents.n( mesh );
	}

	/**
	 * Return an iterator over the connected components of the specified mesh.
	 * <p>
	 * The connected components are returned as new {@link TriMesh}es with
	 * objects copied from the source mesh. Object ids and ordering is not
	 * guaranteed to be the same.
	 * 
	 * @param mesh
	 *            the mesh to split.
	 * @return a new iterator over its connected components as new meshes.
	 */
	public static final Iterator< TriMesh > iterator( final TriMesh mesh )
	{
		return MeshConnectedComponents.iterator( mesh );
	}

	/**
	 * Return an iterable over the connected components of the specified mesh.
	 * <p>
	 * The connected components are returned as new {@link TriMesh}es with
	 * objects copied from the source mesh. Object ids and ordering is not
	 * guaranteed to be the same.
	 * 
	 * @param mesh
	 *            the mesh to split.
	 * @return a new iterable over its connected components as new meshes.
	 */
	public static final Iterable< TriMesh > iterable( final TriMesh mesh )
	{
		return MeshConnectedComponents.iterable( mesh );
	}

	/**
	 * Returns <code>true</code> if the specified mesh is two-manifold.
	 * <p>
	 * A two-manifold mesh is a type of mesh that has property that every edge
	 * on the mesh is shared by exactly two faces.
	 * 
	 * @param mesh
	 *            the mesh to inspect.
	 * @return <code>true</code> if it is two-manifold.
	 */
	public static boolean isTwoManifold( final TriMesh mesh )
	{
		return TwoManifold.isTwoManifold( mesh );
	}
}
