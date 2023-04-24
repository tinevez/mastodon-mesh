package org.mastodon.mesh.alg;

import org.mastodon.RefPool;
import org.mastodon.collection.ObjectRefMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.mesh.TriMesh;
import org.mastodon.mesh.Triangle;
import org.mastodon.mesh.TriangleAdder;
import org.mastodon.mesh.Vertex;

import net.imglib2.RealLocalizable;

/**
 * Adapted from ImageJ-Mesh.
 *
 * @author Deborah Schmidt
 * @author Jean-Yves Tinevez
 */
public class RemoveDuplicateVertices
{

	public static TriMesh calculate( final TriMesh in, final int precision )
	{
		final TriMesh out = new TriMesh();

		final int[][] triangles = new int[ in.triangles().size() ][ 3 ];
		final ObjectRefMap< String, Vertex > vertices = RefMaps.createObjectRefMap( out.vertices() );

		final Vertex ivref0 = in.vertexRef();
		final Vertex ivref1 = in.vertexRef();
		final Vertex ivref2 = in.vertexRef();
		final Vertex ovref = out.vertexRef();

		int trianglesCount = 0;
		for ( final Triangle triangle : in.triangles() )
		{
			final Vertex p0 = triangle.getVertex0( ivref0 );
			final Vertex p1 = triangle.getVertex1( ivref1 );
			final Vertex p2 = triangle.getVertex2( ivref2 );

			triangles[ trianglesCount ][ 0 ] = getVertex( vertices, p0, precision, out, ovref );
			triangles[ trianglesCount ][ 1 ] = getVertex( vertices, p1, precision, out, ovref );
			triangles[ trianglesCount ][ 2 ] = getVertex( vertices, p2, precision, out, ovref );
			trianglesCount++;
		}

		out.releaseRef( ovref );
		in.releaseRef( ivref0 );
		in.releaseRef( ivref1 );
		in.releaseRef( ivref2 );

		final TriangleAdder triangleAdder = out.triangleAdder();
		final Triangle tref = out.triangleRef();
		final Vertex ovref0 = out.vertexRef();
		final Vertex ovref1 = out.vertexRef();
		final Vertex ovref2 = out.vertexRef();
		final RefPool< Vertex > pool = out.vertices().getRefPool();
		for ( final int[] triangle : triangles )
		{
			final Vertex v0 = pool.getObjectIfExists( triangle[ 0 ], ovref0 );
			final Vertex v1 = pool.getObjectIfExists( triangle[ 1 ], ovref1 );
			final Vertex v2 = pool.getObjectIfExists( triangle[ 2 ], ovref2 );
			triangleAdder.add( v0, v1, v2, tref );
		}
		out.releaseRef( ovref0 );
		out.releaseRef( ovref1 );
		out.releaseRef( ovref2 );
		out.releaseRef( tref );
		triangleAdder.releaseRefs();

		return out;
	}

	private static int getVertex(
			final ObjectRefMap< String, Vertex > vertices,
			final RealLocalizable p,
			final int precision,
			final TriMesh out,
			final Vertex vref )
	{
		final String hash = getHash( p, precision );
		final Vertex vertex = vertices.get( hash );
		if ( vertex != null )
			return vertex.getInternalPoolIndex();

		final Vertex newVertex = createVertex( p, precision, out, vref );
		vertices.put( hash, newVertex );
		return newVertex.getInternalPoolIndex();
	}

	private static Vertex createVertex( final RealLocalizable pos, final int precision, final TriMesh out, final Vertex ref )
	{
		final double factor = Math.pow( 10, precision );
		final double x = Math.round( pos.getDoublePosition( 0 ) * factor ) / factor;
		final double y = Math.round( pos.getDoublePosition( 1 ) * factor ) / factor;
		final double z = Math.round( pos.getDoublePosition( 2 ) * factor ) / factor;
		final Vertex v = out.addVertex( ref );
		v.setPosition( x, 0 );
		v.setPosition( y, 1 );
		v.setPosition( z, 2 );
		return v;
	}

	private static String getHash( final RealLocalizable p, final int precision )
	{
		final int factor = ( int ) Math.pow( 10, precision );
		return Math.round( p.getFloatPosition( 0 ) * factor ) + "-" + Math.round( p.getFloatPosition( 1 ) * factor ) + "-" + Math.round( p.getFloatPosition( 2 ) * factor );
	}
}
