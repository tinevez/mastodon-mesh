package org.mastodon.mesh.util;

import org.mastodon.mesh.obj.HalfEdgeI;
import org.mastodon.mesh.obj.VertexI;

public class GeomUtil
{

	public static float dotProduct( final float x1, final float y1, final float z1, final float x2, final float y2, final float z2 )
	{
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public static void crossProduct( final float x1, final float y1, final float z1, final float x2, final float y2, final float z2, final float[] out )
	{
		out[ 0 ] = y1 * z2 - z1 * y2;
		out[ 1 ] = -x1 * z2 + z1 * x2;
		out[ 2 ] = x1 * y2 - y1 * x2;
	}

	public static final void normalize( final float[] v )
	{
		final float x = v[ 0 ];
		final float y = v[ 1 ];
		final float z = v[ 2 ];
		final float l2 = x * x + y * y + z * z;
		final float l = ( float ) Math.sqrt( l2 );
		v[ 0 ] /= l;
		v[ 1 ] /= l;
		v[ 2 ] /= l;
	}

	public static < V extends VertexI< ? > > double dotProduct( final V e1s, final V e1t, final V e2s, final V e2t )
	{
		final double dx1 = e1t.getDoublePosition( 0 ) - e1s.getDoublePosition( 0 );
		final double dy1 = e1t.getDoublePosition( 1 ) - e1s.getDoublePosition( 1 );
		final double dz1 = e1t.getDoublePosition( 2 ) - e1s.getDoublePosition( 2 );

		final double dx2 = e2t.getDoublePosition( 0 ) - e2s.getDoublePosition( 0 );
		final double dy2 = e2t.getDoublePosition( 1 ) - e2s.getDoublePosition( 1 );
		final double dz2 = e2t.getDoublePosition( 2 ) - e2s.getDoublePosition( 2 );

		return dotProduct( dx1, dy1, dz1, dx2, dy2, dz2 );
	}

	public static < V extends VertexI< ? > > void crossProduct( final V e1s, final V e1t, final V e2s, final V e2t, final double[] crossProduct )
	{
		final double dx1 = e1t.getDoublePosition( 0 ) - e1s.getDoublePosition( 0 );
		final double dy1 = e1t.getDoublePosition( 1 ) - e1s.getDoublePosition( 1 );
		final double dz1 = e1t.getDoublePosition( 2 ) - e1s.getDoublePosition( 2 );

		final double dx2 = e2t.getDoublePosition( 0 ) - e2s.getDoublePosition( 0 );
		final double dy2 = e2t.getDoublePosition( 1 ) - e2s.getDoublePosition( 1 );
		final double dz2 = e2t.getDoublePosition( 2 ) - e2s.getDoublePosition( 2 );

		crossProduct( dx1, dy1, dz1, dx2, dy2, dz2, crossProduct );
	}

	public static < V extends VertexI< ? > > void crossProduct( final V e1s, final V e1t, final V e2s, final V e2t, final float[] crossProduct )
	{
		final float dx1 = e1t.getFloatPosition( 0 ) - e1s.getFloatPosition( 0 );
		final float dy1 = e1t.getFloatPosition( 1 ) - e1s.getFloatPosition( 1 );
		final float dz1 = e1t.getFloatPosition( 2 ) - e1s.getFloatPosition( 2 );

		final float dx2 = e2t.getFloatPosition( 0 ) - e2s.getFloatPosition( 0 );
		final float dy2 = e2t.getFloatPosition( 1 ) - e2s.getFloatPosition( 1 );
		final float dz2 = e2t.getFloatPosition( 2 ) - e2s.getFloatPosition( 2 );

		crossProduct( dx1, dy1, dz1, dx2, dy2, dz2, crossProduct );
	}

	public static double dotProduct( final double x1, final double y1, final double z1, final double x2, final double y2, final double z2 )
	{
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public static void crossProduct( final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final double[] out )
	{
		out[ 0 ] = y1 * z2 - z1 * y2;
		out[ 1 ] = -x1 * z2 + z1 * x2;
		out[ 2 ] = x1 * y2 - y1 * x2;
	}

	public static final void normalize( final double[] v )
	{
		final double x = v[ 0 ];
		final double y = v[ 1 ];
		final double z = v[ 2 ];
		final double l2 = x * x + y * y + z * z;
		final double l = Math.sqrt( l2 );
		v[ 0 ] /= l;
		v[ 1 ] /= l;
		v[ 2 ] /= l;
	}

	public static < E extends HalfEdgeI< E, V, ? >, V extends VertexI< E > > void crossProduct( final E e1, final E e2, final V ref, final double[] crossProduct )
	{
		final V s = e1.getSource( ref );
		final double x1s = s.x();
		final double y1s = s.y();
		final double z1s = s.z();
		final V t = e1.getTarget( ref );
		final double x1t = t.x();
		final double y1t = t.y();
		final double z1t = t.z();
		final double dx1 = x1t - x1s;
		final double dy1 = y1t - y1s;
		final double dz1 = z1t - z1s;

		final V os = e2.getSource( ref );
		final double x2s = os.x();
		final double y2s = os.y();
		final double z2s = os.z();
		final V ot = e2.getTarget( ref );
		final double x2t = ot.x();
		final double y2t = ot.y();
		final double z2t = ot.z();
		final double dx2 = x2t - x2s;
		final double dy2 = y2t - y2s;
		final double dz2 = z2t - z2s;

		crossProduct( dx1, dy1, dz1, dx2, dy2, dz2, crossProduct );
	}

	public static final < E extends HalfEdgeI< E, V, ? >, V extends VertexI< E > > double dotProduct( final E e1, final E e2, final V ref )
	{
		final V s = e1.getSource( ref );
		final double x1s = s.x();
		final double y1s = s.y();
		final double z1s = s.z();
		final V t = e1.getTarget( ref );
		final double x1t = t.x();
		final double y1t = t.y();
		final double z1t = t.z();
		final double dx1 = x1t - x1s;
		final double dy1 = y1t - y1s;
		final double dz1 = z1t - z1s;

		final V os = e2.getSource( ref );
		final double x2s = os.x();
		final double y2s = os.y();
		final double z2s = os.z();
		final V ot = e2.getTarget( ref );
		final double x2t = ot.x();
		final double y2t = ot.y();
		final double z2t = ot.z();
		final double dx2 = x2t - x2s;
		final double dy2 = y2t - y2s;
		final double dz2 = z2t - z2s;

		return dotProduct( dx1, dy1, dz1, dx2, dy2, dz2 );
	}
}
