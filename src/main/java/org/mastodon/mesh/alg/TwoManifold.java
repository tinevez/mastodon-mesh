package org.mastodon.mesh.alg;

import org.mastodon.mesh.obj.HalfEdgeI;
import org.mastodon.mesh.obj.TriMeshI;
import org.mastodon.mesh.obj.TriangleI;
import org.mastodon.mesh.obj.VertexI;

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
	public static < V extends VertexI< E >, E extends HalfEdgeI< E, V, T >, T extends TriangleI< V > > boolean isTwoManifold( final TriMeshI< V, E, T > mesh )
	{
		final V vref0 = mesh.vertexRef();
		final V vref1 = mesh.vertexRef();
		final V vref2 = mesh.vertexRef();
		final E eref = mesh.edgeRef();
		final T tref1 = mesh.triangleRef();
		final T tref2 = mesh.triangleRef();

		try
		{
			for ( final E edge : mesh.edges() )
			{
				// More than two edges between the two vertices?
				final V source = edge.getSource( vref0 );
				final V target = edge.getTarget( vref1 );
				if ( mesh.getEdges( source, target, vref2 ).size() != 1 )
					return false;
				if ( mesh.getEdges( target, source, vref2 ).size() != 1 )
					return false;

				// Has one face.
				final T triangle = edge.triangle( tref1 );
				if ( null == triangle )
					return false;

				// Twin has another face.
				final E twin = edge.twin( eref );
				if ( twin == null )
					return false;
				final T twinTriangle = twin.triangle( tref2 );
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
