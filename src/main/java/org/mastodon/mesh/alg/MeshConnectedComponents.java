package org.mastodon.mesh.alg;

import java.util.Iterator;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefDeque;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.algorithm.util.Graphs;
import org.mastodon.mesh.Meshes;
import org.mastodon.mesh.obj.HalfEdgeI;
import org.mastodon.mesh.obj.TriMeshI;
import org.mastodon.mesh.obj.TriangleAdderI;
import org.mastodon.mesh.obj.TriangleI;
import org.mastodon.mesh.obj.VertexI;
import org.mastodon.mesh.obj.core.TriMesh;
import org.mastodon.mesh.obj.core.Triangle;
import org.mastodon.mesh.obj.core.Vertex;

public class MeshConnectedComponents
{

	/**
	 * Returns the number of connected components in this mesh.
	 * 
	 * @param mesh
	 *            the mesh.
	 * @return the number of connected components.
	 */
	public static < V extends VertexI< E >, E extends HalfEdgeI< E, V, ? > > int n( final TriMeshI< V, E, ? > mesh )
	{
		// Simple version that does not keep CCs in memory.
		final V vref0 = mesh.vertexRef();
		final V vref1 = mesh.vertexRef();
		try
		{
			int nCC = 0;
			final RefSet< V > visited = RefCollections.createRefSet( mesh.vertices() );
			final RefDeque< V > queue = RefCollections.createRefDeque( mesh.vertices() );

			final Iterator< V > it = mesh.vertices().iterator();
			while ( it.hasNext() )
			{
				// Get a starting point that was never visited.
				final V start = it.next();
				if ( visited.contains( start ) )
					continue;

				// Iterate from starting point.
				nCC++;
				queue.clear();
				queue.add( start );
				while ( !queue.isEmpty() )
				{
					final V v = queue.pop( vref1 );
					if ( visited.contains( v ) )
						continue;

					visited.add( v );
					for ( final E e : v.edges() )
					{
						final V o = Graphs.getOppositeVertex( e, v, vref1 );
						if ( !visited.contains( o ) )
							queue.add( o );
					}
				}
			}
			return nCC;
		}
		finally
		{
			mesh.releaseRef( vref0 );
			mesh.releaseRef( vref1 );
		}
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
	public static final < V extends VertexI< E >, E extends HalfEdgeI< E, V, T >, T extends TriangleI< V > > Iterator< TriMesh > iterator( final TriMeshI< V, E, T > mesh )
	{
		return new MyCCIterator< V, E, T >( mesh );
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
	public static final < V extends VertexI< E >, E extends HalfEdgeI< E, V, T >, T extends TriangleI< V > > Iterable< TriMesh > iterable( final TriMeshI< V, E, T > mesh )
	{
		return new Iterable< TriMesh >()
		{

			@Override
			public Iterator< TriMesh > iterator()
			{
				return new MyCCIterator< V, E, T >( mesh );
			}
		};
	}

	private static final class MyCCIterator< V extends VertexI< E >, E extends HalfEdgeI< E, V, T >, T extends TriangleI< V > > implements Iterator< TriMesh >
	{

		private final TriMeshI< V, E, T > mesh;

		private final RefSet< V > visited;

		private final Iterator< V > it;

		private TriMesh next;

		private MyCCIterator( final TriMeshI< V, E, T > mesh )
		{
			this.mesh = mesh;
			this.visited = RefCollections.createRefSet( mesh.vertices() );
			this.it = mesh.vertices().iterator();
			this.next = prefetch();
		}

		@Override
		public boolean hasNext()
		{
			return it.hasNext();
		}

		@Override
		public TriMesh next()
		{
			final TriMesh out = next;
			next = prefetch();
			return out;
		}

		private TriMesh prefetch()
		{
			// Build the next connected component.
			final V vref0 = mesh.vertexRef();
			final V vref1 = mesh.vertexRef();
			final T tref = mesh.triangleRef();
			try
			{
				final RefDeque< V > queue = RefCollections.createRefDeque( mesh.vertices() );
				final RefSet< T > cc = RefCollections.createRefSet( mesh.triangles() );
				while ( it.hasNext() )
				{
					// Get a starting point that was never visited.
					final V start = it.next();
					if ( visited.contains( start ) )
						continue;

					// Iterate from starting point.
					queue.clear();
					queue.add( start );
					while ( !queue.isEmpty() )
					{
						final V v = queue.pop( vref1 );
						if ( visited.contains( v ) )
							continue;

						visited.add( v );
						for ( final E e : v.edges() )
						{
							final V o = Graphs.getOppositeVertex( e, v, vref1 );
							if ( !visited.contains( o ) )
							{
								queue.add( o );
								cc.add( e.triangle( tref ) );
							}
						}
					}
					return makeCC( cc );
				}
			}
			finally
			{
				mesh.releaseRef( vref0 );
				mesh.releaseRef( vref1 );
				mesh.releaseRef( tref );
			}
			return null;
		}

		private TriMesh makeCC( final RefSet< T > cc )
		{
			final TriMesh out = new TriMesh( cc.size() );

			final V ivref = mesh.vertexRef();
			final Vertex ovref0 = out.vertexRef();
			final Vertex ovref1 = out.vertexRef();
			final Vertex ovref2 = out.vertexRef();
			final Triangle otref = out.triangleRef();
			final TriangleAdderI< Triangle, Vertex > triangleAdder = out.triangleAdder();

			try
			{
				// Map of Vid in IN mesh to Vid in OUT mesh.
				final IntRefMap< Vertex > vMap = RefMaps.createIntRefMap( out.vertices(), -1 );

				// Copy vertices.
				final RefSet< V > inVertices = Meshes.verticesOf( cc, mesh );
				for ( final V vIn : inVertices )
				{
					final Vertex vOut = out.addVertex( ovref0 );
					vOut.setPosition( vIn );
					vMap.put( vIn.id(), vOut );
				}

				// Copy faces.
				for ( final T inT : cc )
				{
					final Vertex ov0 = vMap.get( inT.v0(), ovref0 );
					final Vertex ov1 = vMap.get( inT.v1(), ovref1 );
					final Vertex ov2 = vMap.get( inT.v2(), ovref2 );
					triangleAdder.add( ov0, ov1, ov2, otref );
				}
				return out;
			}
			finally
			{
				out.releaseRef( ovref0 );
				out.releaseRef( ovref1 );
				out.releaseRef( ovref2 );
				out.releaseRef( otref );
				triangleAdder.releaseRefs();
				mesh.releaseRef( ivref );
			}
		}
	}
}
