package org.mastodon.mesh.alg.simplify;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.graph.ref.GraphImp;
import org.mastodon.mesh.obj.core.TriMesh;
import org.mastodon.mesh.obj.core.Triangle;
import org.mastodon.mesh.obj.core.Vertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolCollectionWrapper;

public class STriMesh extends GraphImp< SVertexPool, SHalfEdgePool, SVertex, SHalfEdge, ByteMappedElement >
{

	final STrianglePool trianglePool;

	public STriMesh()
	{
		this( 1000 );
	}

	public STriMesh( final int initialCapacity )
	{
		this( initialCapacity, initialCapacity );
	}

	public STriMesh( final int nVertices, final int nTriangles )
	{
		this( nVertices, nTriangles, 3 * nTriangles );
	}

	public STriMesh( final int nVertices, final int nTriangles, final int nEdges )
	{
		super( new SHalfEdgePool( nEdges, new SVertexPool( nVertices ) ) );
		trianglePool = new STrianglePool( nTriangles, vertexPool, this );
		edgePool.setLinkedFacePool( trianglePool );
	}

	public PoolCollectionWrapper< STriangle > triangles()
	{
		return trianglePool.asRefCollection();
	}

	public STriangleAdder triangleAdder()
	{
		return new STriangleAdder( this );
	}

	@Override
	public String toString()
	{
		return String.format( "TriMesh %d vertices, %d edges, %d faces",
				vertexPool.size(), edgePool.size(), trianglePool.size() );
	}

	public STriangle triangleRef()
	{
		return trianglePool.createRef();
	}

	public void releaseRef( final STriangle ref )
	{
		trianglePool.releaseRef( ref );
	}

	public static final STriMesh from(final TriMesh in)
	{
		final STriMesh out = new STriMesh( in.vertices().size(), in.triangles().size(), in.edges().size() );

		final SVertex ovref0 = out.vertexRef();
		final SVertex ovref1 = out.vertexRef();
		final SVertex ovref2 = out.vertexRef();
		try
		{
			final IntRefMap< SVertex > map = RefMaps.createIntRefMap( out.vertices(), -1, in.vertices().size() );
			for ( final Vertex inV : in.vertices() )
			{
				final SVertex ov = out.addVertex( ovref0 );
				ov.setPosition( inV );
				ov.setQ( 0. );
				map.put( inV.getInternalPoolIndex(), ov );
			}

			final STriangleAdder oAdder = out.triangleAdder();
			for ( final Triangle inT : in.triangles() )
			{
				final SVertex ov0 = map.get( inT.v0(), ovref0 );
			}

			return out;
		}
		finally
		{
			out.releaseRef( ovref0 );
		}
	}
}
