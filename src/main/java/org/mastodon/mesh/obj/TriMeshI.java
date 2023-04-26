package org.mastodon.mesh.obj;

import org.mastodon.collection.RefCollection;
import org.mastodon.graph.Graph;

public interface TriMeshI< 
			V extends VertexI< E >, 
			E extends HalfEdgeI< E, V, T >, 
			T extends TriangleI< V, E > > 
	extends Graph< V, E >
{

	public RefCollection< T > triangles();

	public TriangleAdderI< T, V > triangleAdder();

	public T triangleRef();

	public void releaseRef( final T ref );

	public T addTriangle( T ref );
}
