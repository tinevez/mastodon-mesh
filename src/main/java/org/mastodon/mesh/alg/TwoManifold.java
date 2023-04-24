package org.mastodon.mesh.alg;

import org.mastodon.mesh.HalfEdge;
import org.mastodon.mesh.TriMesh;
import org.mastodon.mesh.Triangle;
import org.mastodon.mesh.Vertex;

public class TwoManifold
{

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
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		final Vertex vref2 = mesh.vertexRef();
		final HalfEdge eref = mesh.edgeRef();
		final Triangle tref1 = mesh.triangleRef();
		final Triangle tref2 = mesh.triangleRef();

		try
		{
			for ( final HalfEdge edge : mesh.edges() )
			{
				// More than two edges between the two vertices?
				final Vertex source = edge.getSource( vref0 );
				final Vertex target = edge.getTarget( vref1 );
				if ( mesh.getEdges( source, target, vref2 ).size() != 1 )
					return false;
				if ( mesh.getEdges( target, source, vref2 ).size() != 1 )
					return false;

				// Has one face.
				final Triangle triangle = edge.triangle( tref1 );
				if ( null == triangle )
					return false;

				// Twin has another face.
				final HalfEdge twin = edge.twin( eref );
				if ( twin == null )
					return false;
				final Triangle twinTriangle = twin.triangle( tref2 );
				if ( twinTriangle == null || triangle.equals( twinTriangle ) )
					return false;
			}

			return true;
		}
		finally
		{
			mesh.releaseRef( vref0 );
			mesh.releaseRef( vref1 );
			mesh.releaseRef( vref2 );
			mesh.releaseRef( eref );
			mesh.releaseRef( tref1 );
		}
	}
}
