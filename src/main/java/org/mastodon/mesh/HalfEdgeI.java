package org.mastodon.mesh;

import org.mastodon.graph.Edge;

public interface HalfEdgeI< E extends HalfEdgeI< E, V, T >, V extends VertexI< E >, T extends TriangleI< V > > extends Edge< V >
{

	E next( final E ref );

	E previous( final E ref );

	E twin( final E ref );

	T triangle( final T ref );
}
