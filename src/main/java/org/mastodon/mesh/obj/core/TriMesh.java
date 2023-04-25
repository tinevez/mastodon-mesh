package org.mastodon.mesh.obj.core;

import org.mastodon.mesh.obj.base.AbstractTriMesh;
import org.mastodon.pool.ByteMappedElement;

public class TriMesh extends AbstractTriMesh< VertexPool, HalfEdgePool, TrianglePool, Vertex, HalfEdge, Triangle, ByteMappedElement >
{

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
		trianglePool = new TrianglePool( nTriangles, vertexPool );
		edgePool.setLinkedTrianglePool( trianglePool );
	}
}
