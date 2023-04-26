package org.mastodon.mesh.obj.base;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.mesh.obj.TriMeshI;
import org.mastodon.mesh.obj.TriangleAdderI;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolCollectionWrapper;

public class AbstractTriMesh<
				VP extends AbstractVertexPool< V, E, T, M >,
				EP extends AbstractHalfEdgePool< E, V, TP, M >,
				TP extends AbstractTrianglePool< T, V, E, VP, EP, M >,
				V extends AbstractVertex< V, E, T, VP, M >,
				E extends AbstractHalfEdge< E, V, T, EP, TP, M >,
				T extends AbstractTriangle< T, V, E, TP, EP, M >,
				M extends MappedElement >
		extends GraphImp< VP, EP, V, E, M >
		implements TriMeshI< V, E, T >
{

	protected TP trianglePool;

	protected AbstractTriMesh( final EP edgePool )
	{
		super( edgePool );
	}

	@Override
	public PoolCollectionWrapper< T > triangles()
	{
		return trianglePool.asRefCollection();
	}

	@Override
	public String toString()
	{
		return String.format( "%s %d vertices, %d edges, %d triangles",
				getClass().getSimpleName(),
				vertexPool.size(), edgePool.size(), trianglePool.size() );
	}

	@Override
	public T triangleRef()
	{
		return trianglePool.createRef();
	}

	@Override
	public void releaseRef( final T ref )
	{
		trianglePool.releaseRef( ref );
	}

	@Override
	public TriangleAdderI< T, V > triangleAdder()
	{
		return new AbstractTriangleAdder<>( this );
	}

	@Override
	public T addTriangle( final T ref )
	{
		return trianglePool.create( ref );
	}
}
