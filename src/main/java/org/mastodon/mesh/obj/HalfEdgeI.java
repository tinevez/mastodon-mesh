package org.mastodon.mesh.obj;

import org.mastodon.graph.Edge;

public interface HalfEdgeI< E extends HalfEdgeI< E, V, T >, V extends VertexI< E >, T extends TriangleI< V, E > > extends Edge< V >
{

	E next( final E ref );

	E previous( final E ref );

	E twin( final E ref );

	T triangle( final T ref );
}
