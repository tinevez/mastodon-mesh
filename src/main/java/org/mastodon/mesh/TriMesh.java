package org.mastodon.mesh;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TriMesh extends GraphImp< VertexPool, HalfEdgePool, Vertex, HalfEdge, ByteMappedElement >
{

	private final TrianglePool facePool;

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
		facePool = new TrianglePool( nTriangles, vertexPool, this );
		edgePool.setLinkedFacePool( facePool );
	}

	public TrianglePool triangles()
	{
		return facePool;
	}

	public FaceAdder faceAdder()
	{
		return new FaceAdder( this );
	}

	@Override
	public String toString()
	{
		return String.format( "TriMesh %d vertices, %d edges, %d faces",
				vertexPool.size(), edgePool.size(), facePool.size() );
	}
}
