package org.mastodon.mesh;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolCollectionWrapper;

public class TriMesh extends GraphImp< VertexPool, HalfEdgePool, Vertex, HalfEdge, ByteMappedElement >
{

	final TrianglePool trianglePool;

	public TriMesh()
	{
		this( 1000 );
	}

	public TriMesh( final int initialCapacity )
	{
		this( initialCapacity, initialCapacity );
	}

	public TriMesh( final int nVertices, final int nTriangles )
	{
		this( nVertices, nTriangles, 3 * nTriangles );
	}

	public TriMesh( final int nVertices, final int nTriangles, final int nEdges )
	{
		super( new HalfEdgePool( nEdges, new VertexPool( nVertices ) ) );
		trianglePool = new TrianglePool( nTriangles, vertexPool, this );
		edgePool.setLinkedFacePool( trianglePool );
	}

	public PoolCollectionWrapper< Triangle > triangles()
	{
		return trianglePool.asRefCollection();
	}

	public TriangleAdder triangleAdder()
	{
		return new TriangleAdder( this );
	}

	@Override
	public String toString()
	{
		return String.format( "TriMesh %d vertices, %d edges, %d faces",
				vertexPool.size(), edgePool.size(), trianglePool.size() );
	}

	public Triangle triangleRef()
	{
		return trianglePool.createRef();
	}

	public void releaseRef( final Triangle ref )
	{
		trianglePool.releaseRef( ref );

	}
}
