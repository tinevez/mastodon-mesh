package org.mastodon.mesh.alg;

import java.util.Iterator;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefDeque;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.algorithm.util.Graphs;
import org.mastodon.mesh.HalfEdge;
import org.mastodon.mesh.Meshes;
import org.mastodon.mesh.TriMesh;
import org.mastodon.mesh.Triangle;
import org.mastodon.mesh.TriangleAdder;
import org.mastodon.mesh.Vertex;

public class MeshConnectedComponents
{

	/**
	 * Returns the number of connected components in this mesh.
	 * 
	 * @param mesh
	 *            the mesh.
	 * @return the number of connected components.
	 */
	public static int n( final TriMesh mesh )
	{
		// Simple version that does not keep CCs in memory.
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		try
		{
			int nCC = 0;
			final RefSet< Vertex > visited = RefCollections.createRefSet( mesh.vertices() );
			final RefDeque< Vertex > queue = RefCollections.createRefDeque( mesh.vertices() );

			final Iterator< Vertex > it = mesh.vertices().iterator();
			while ( it.hasNext() )
			{
				// Get a starting point that was never visited.
				final Vertex start = it.next();
				if ( visited.contains( start ) )
					continue;

				// Iterate from starting point.
				nCC++;
				queue.clear();
				queue.add( start );
				while ( !queue.isEmpty() )
				{
					final Vertex v = queue.pop( vref1 );
					if ( visited.contains( v ) )
						continue;

					visited.add( v );
					for ( final HalfEdge e : v.edges() )
					{
						final Vertex o = Graphs.getOppositeVertex( e, v, vref1 );
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
	public static final Iterator< TriMesh > iterator( final TriMesh mesh )
	{
		return new MyCCIterator( mesh );
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
		return new Iterable< TriMesh >()
		{

			@Override
			public Iterator< TriMesh > iterator()
			{
				return new MyCCIterator( mesh );
			}
		};
	}

	private static final class MyCCIterator implements Iterator< TriMesh >
	{

		private final TriMesh mesh;

		private final RefSet< Vertex > visited;

		private final Iterator< Vertex > it;

		private TriMesh next;

		private MyCCIterator( final TriMesh mesh )
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
			final Vertex vref0 = mesh.vertexRef();
			final Vertex vref1 = mesh.vertexRef();
			final Triangle tref = mesh.triangleRef();
			try
			{
				final RefDeque< Vertex > queue = RefCollections.createRefDeque( mesh.vertices() );
				final RefSet< Triangle > cc = RefCollections.createRefSet( mesh.triangles() );
				while ( it.hasNext() )
				{
					// Get a starting point that was never visited.
					final Vertex start = it.next();
					if ( visited.contains( start ) )
						continue;

					// Iterate from starting point.
					queue.clear();
					queue.add( start );
					while ( !queue.isEmpty() )
					{
						final Vertex v = queue.pop( vref1 );
						if ( visited.contains( v ) )
							continue;

						visited.add( v );
						for ( final HalfEdge e : v.edges() )
						{
							final Vertex o = Graphs.getOppositeVertex( e, v, vref1 );
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

		private TriMesh makeCC( final RefSet< Triangle > cc )
		{
			final TriMesh out = new TriMesh( cc.size() );

			final Vertex ivref = mesh.vertexRef();
			final Vertex ovref0 = out.vertexRef();
			final Vertex ovref1 = out.vertexRef();
			final Vertex ovref2 = out.vertexRef();
			final Triangle otref = out.triangleRef();
			final TriangleAdder triangleAdder = out.triangleAdder();

			try
			{
				// Map of Vid in IN mesh to Vid in OUT mesh.
				final IntRefMap< Vertex > vMap = RefMaps.createIntRefMap( out.vertices(), -1 );

				// Copy vertices.
				final RefSet< Vertex > inVertices = Meshes.verticesOf( cc, mesh );
				for ( final Vertex vIn : inVertices )
				{
					final Vertex vOut = out.addVertex( ovref0 );
					vOut.init( vIn.x(), vIn.y(), vIn.z() );
					vMap.put( vIn.getInternalPoolIndex(), vOut );
				}

				// Copy faces.
				for ( final Triangle inT : cc )
				{
					final int id0 = inT.getVertex0( ivref ).getInternalPoolIndex();
					final Vertex ov0 = vMap.get( id0, ovref0 );

					final int id1 = inT.getVertex1( ivref ).getInternalPoolIndex();
					final Vertex ov1 = vMap.get( id1, ovref1 );

					final int id2 = inT.getVertex2( ivref ).getInternalPoolIndex();
					final Vertex ov2 = vMap.get( id2, ovref2 );

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
