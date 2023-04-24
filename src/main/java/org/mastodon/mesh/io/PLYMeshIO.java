package org.mastodon.mesh.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.mesh.Triangle;
import org.mastodon.mesh.TriangleAdder;
import org.mastodon.mesh.TriMesh;
import org.mastodon.mesh.Vertex;
import org.scijava.util.FileUtils;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;
import org.smurn.jply.util.NormalMode;
import org.smurn.jply.util.NormalizingPlyReader;
import org.smurn.jply.util.TesselationMode;
import org.smurn.jply.util.TextureMode;

/**
 * A plugin for reading and writing
 * <a href= "https://en.wikipedia.org/wiki/PLY_(file_format)">PLY files</a>.
 * <p>
 * Adapted from {@link net.imagej.mesh.io.ply.PLYMeshIO}.
 *
 * @author Kyle Harrington (University of Idaho, Moscow)
 * @author Curtis Rueden
 * @author Jean-Yves Tinevez
 */
public class PLYMeshIO
{

	public static void read( final File plyFile, final TriMesh mesh ) throws IOException
	{
		try (final FileInputStream is = new FileInputStream( plyFile ))
		{
			read( is, mesh );
		}
	}

	public static void read( final InputStream plyIS, final TriMesh mesh ) throws IOException
	{
		final PlyReader pr = new PlyReaderFile( plyIS );
		final NormalizingPlyReader plyReader = new NormalizingPlyReader( pr, TesselationMode.TRIANGLES,
				NormalMode.ADD_NORMALS_CCW, TextureMode.PASS_THROUGH );

		try
		{
			read( plyReader, mesh );
		}
		finally
		{
			plyReader.close();
		}
	}

	public static TriMesh open( final InputStream plyIS ) throws IOException
	{
		final PlyReader pr = new PlyReaderFile( plyIS );
		final NormalizingPlyReader plyReader = new NormalizingPlyReader( pr, TesselationMode.TRIANGLES,
				NormalMode.ADD_NORMALS_CCW, TextureMode.PASS_THROUGH );
		try
		{
			final int nVertices = plyReader.getElementCount( "vertex" );
			final int nTriangles = plyReader.getElementCount( "face" );
			final TriMesh mesh = new TriMesh( nVertices, nTriangles );

			read( plyReader, mesh );
			return mesh;
		}
		finally
		{
			plyReader.close();
		}
	}

	private static final List< int[] > readTriangles( final ElementReader reader ) throws IOException
	{
		final List< int[] > triangles = new ArrayList<>( reader.getCount() );
		Element triangle = reader.readElement();
		while ( triangle != null )
		{
			final int[] indices = triangle.getIntList( "vertex_index" );
			triangles.add( indices );
			triangle = reader.readElement();
		}
		return triangles;
	}

	/**
	 * Populates the content of the specified mesh with the data read from the
	 * PLY reader. It it the caller responsibility to close the reader after
	 * calling this method.
	 *
	 * @param plyReader
	 *            the reader.
	 * @param mesh
	 *            the mesh to populate.
	 * @throws IOException
	 */
	private static final void read( final PlyReader plyReader, final TriMesh mesh ) throws IOException
	{
		// Data holders.
		IntRefMap< Vertex > vertexRowMap = null;
		List< int[] > triangles = null;

		// Iterate through the stream and add vertices.
		ElementReader reader;
		while ( ( reader = plyReader.nextElementReader() ) != null )
		{
			final String elementName = reader.getElementType().getName();

			if ( elementName.equals( "vertex" ) )
				vertexRowMap = readVertices( reader, mesh );
			else if ( elementName.equals( "face" ) )
				triangles = readTriangles( reader );

			reader.close();
		}

		// Test.
		if ( vertexRowMap == null )
			throw new IOException( "Could not find the 'vertex' element in file." );
		if ( triangles == null )
			throw new IOException( "Could not find the 'face' element in file." );

		// Add triangles to the mesh.
		final TriangleAdder adder = mesh.triangleAdder();
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		final Vertex vref2 = mesh.vertexRef();
		for ( final int[] triangle : triangles )
		{
			final Vertex v0 = vertexRowMap.get( triangle[ 0 ], vref0 );
			final Vertex v1 = vertexRowMap.get( triangle[ 1 ], vref1 );
			final Vertex v2 = vertexRowMap.get( triangle[ 2 ], vref2 );
			adder.add( v0, v1, v2 );
		}
		mesh.releaseRef( vref0 );
		mesh.releaseRef( vref1 );
		mesh.releaseRef( vref2 );
		adder.releaseRefs();
	}

	private static final IntRefMap< Vertex > readVertices( final ElementReader reader, final TriMesh mesh ) throws IOException
	{
		final IntRefMap< Vertex > rowToVertMap = RefMaps.createIntRefMap( mesh.vertices(), -1 );

		int vertCount = 0;
		Element vertex = reader.readElement();
		final Vertex vref = mesh.vertexRef();
		while ( vertex != null )
		{
			final float x = ( float ) vertex.getDouble( "x" );
			final float z = ( float ) vertex.getDouble( "z" );
			final float y = ( float ) vertex.getDouble( "y" );
//			final float nx = ( float ) vertex.getDouble( "nx" );
//			final float ny = ( float ) vertex.getDouble( "ny" );
//			final float nz = ( float ) vertex.getDouble( "nz" );
			// TODO we ignore vertex normal for now.
//			final float u = 0, v = 0; // TODO: texture coordinate

			final Vertex v = mesh.addVertex( vref );
			v.setPosition( x, 0 );
			v.setPosition( y, 1 );
			v.setPosition( z, 2 );

			rowToVertMap.put( vertCount++, v );
			vertex = reader.readElement();
		}
		mesh.releaseRef( vref );
		return rowToVertMap;
	}

	public static final byte[] writeBinary( final TriMesh mesh )
	{
		final int vertexBytes = 3 * 4 + 3 * 4 + 3 * 4;
		final int triangleBytes = 3 * 4 + 1;
		final String header = "ply\n" + //
				"format binary_little_endian 1.0\n" + //
				"comment This binary PLY mesh was created with imagej-mesh.\n";
		final String vertexHeader = "" + //
				"element vertex " + mesh.vertices().size() + "\n" + //
				"property float x\nproperty float y\nproperty float z\n" + //
				"property float nx\nproperty float ny\nproperty float nz\n" + //
				"property float u\nproperty float v\n";
		final String triangleHeader = "element face " + mesh.triangles().size()
				+ "\nproperty list uchar int vertex_index\n";
		final String endHeader = "end_header\n";
		final long bytes = header.getBytes().length + //
				vertexHeader.getBytes().length + triangleHeader.getBytes().length + endHeader.getBytes().length
				+ mesh.vertices().size() * vertexBytes + //
				mesh.triangles().size() * triangleBytes;
		if ( bytes > Integer.MAX_VALUE )
		{ throw new IllegalArgumentException( "Mesh data too large: " + bytes ); }
		final ByteBuffer buffer = ByteBuffer.allocate( ( int ) bytes ).order( ByteOrder.LITTLE_ENDIAN );

		buffer.put( header.getBytes() );
		buffer.put( vertexHeader.getBytes() );
		buffer.put( triangleHeader.getBytes() );
		buffer.put( endHeader.getBytes() );

		// Do not populate file if there are no vertices
		if ( mesh.vertices().size() == 0 )
			return buffer.array();

		// Write vertices
		final RefIntMap< Vertex > refToVertId = RefMaps.createRefIntMap( mesh.vertices(), -1, mesh.vertices().size() );
		int vertId = 0;
		for ( final Vertex v : mesh.vertices() )
		{
			buffer.putFloat( v.getFloatPosition( 0 ) );
			buffer.putFloat( v.getFloatPosition( 1 ) );
			buffer.putFloat( v.getFloatPosition( 2 ) );
			buffer.putFloat( 0f );
			buffer.putFloat( 0f );
			buffer.putFloat( 0f );
			buffer.putFloat( 0f );
			buffer.putFloat( 0f );
			refToVertId.put( v, vertId );
			++vertId;
		}

		// Write triangles
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		final Vertex vref2 = mesh.vertexRef();
		for ( final Triangle t : mesh.triangles() )
		{
			buffer.put( ( byte ) 3 );
			buffer.putInt( refToVertId.get( t.getVertex0( vref0 ) ) );
			buffer.putInt( refToVertId.get( t.getVertex1( vref1 ) ) );
			buffer.putInt( refToVertId.get( t.getVertex2( vref2 ) ) );
		}
		mesh.releaseRef( vref0 );
		mesh.releaseRef( vref1 );
		mesh.releaseRef( vref2 );

		return buffer.array();
	}

	public static final byte[] writeAscii( final TriMesh mesh ) throws IOException
	{
		final String header = "ply\nformat ascii 1.0\ncomment This binary PLY mesh was created with imagej-mesh.\n";
		final String vertexHeader = "element vertex " + mesh.vertices().size()
				+ "\nproperty float x\nproperty float y\nproperty float z\nproperty float nx\nproperty float ny\nproperty float nz\nproperty float u\n property float v\n";
		final String triangleHeader = "element face " + mesh.triangles().size()
				+ "\nproperty list uchar int vertex_index\n";
		final String endHeader = "end_header\n";

		// TODO: Fail fast more robustly if mesh is too large.
		// But need to modify the API to not return a byte[].
		if ( mesh.vertices().size() > Integer.MAX_VALUE )
		{
			throw new IllegalArgumentException( "Too many vertices: " + //
					mesh.vertices().size() );
		}
		if ( mesh.triangles().size() > Integer.MAX_VALUE )
		{
			throw new IllegalArgumentException( "Too many triangles: " + //
					mesh.triangles().size() );
		}

		final ByteArrayOutputStream os = new ByteArrayOutputStream();

		final Writer writer = new OutputStreamWriter( os, "UTF-8" );

		writer.write( header );
		writer.write( vertexHeader );
		writer.write( triangleHeader );
		writer.write( endHeader );

		// Do not populate file if there are no vertices
		if ( mesh.vertices().size() == 0 )
		{
			writer.flush();
			return os.toByteArray();
		}

		// Write vertices
		final RefIntMap< Vertex > refToVertId = RefMaps.createRefIntMap( mesh.vertices(), -1, mesh.vertices().size() );
		int vertId = 0;
		for ( final Vertex v : mesh.vertices() )
		{
			writer.write( Float.toString( v.getFloatPosition( 0 ) ) );
			writer.write( ' ' );
			writer.write( Float.toString( v.getFloatPosition( 1 ) ) );
			writer.write( ' ' );
			writer.write( Float.toString( v.getFloatPosition( 2 ) ) );
			writer.write( ' ' );
			writer.write( Float.toString( 0f ) );
			writer.write( ' ' );
			writer.write( Float.toString( 0f ) );
			writer.write( ' ' );
			writer.write( Float.toString( 0f ) );
			writer.write( ' ' );
			writer.write( Float.toString( 0f ) );
			writer.write( ' ' );
			writer.write( Float.toString( 0f ) );
			writer.write( '\n' );
			refToVertId.put( v, vertId );
			++vertId;
		}

		// Write triangles
		final Vertex vref0 = mesh.vertexRef();
		final Vertex vref1 = mesh.vertexRef();
		final Vertex vref2 = mesh.vertexRef();
		for ( final Triangle t : mesh.triangles() )
		{
			writer.write( "3 " );
			writer.write( Integer.toString( refToVertId.get( t.getVertex0( vref0 ) ) ) );
			writer.write( ' ' );
			writer.write( Integer.toString( refToVertId.get( t.getVertex1( vref1 ) ) ) );
			writer.write( ' ' );
			writer.write( Integer.toString( refToVertId.get( t.getVertex2( vref2 ) ) ) );
			writer.write( '\n' );
		}
		mesh.releaseRef( vref0 );
		mesh.releaseRef( vref1 );
		mesh.releaseRef( vref2 );
		writer.flush();
		return os.toByteArray();
	}

	/**
	 * Returns the number of vertices and faces declared in this file.
	 *
	 * @param source
	 *            the path to the file.
	 * @return a new <code>int[]</code> array with 2 elements:
	 *         <ol start="0">
	 *         <li>the number of vertices.
	 *         <li>the number of faces.
	 *         </ol>
	 * @throws IOException
	 */
	public static final int[] getNVerticesFaces( final String source ) throws IOException
	{
		final File file = new File( source );
		final PlyReaderFile reader = new PlyReaderFile( file );
		try
		{
			final int nVertices = reader.getElementCount( "vertex" );
			final int nTriangles = reader.getElementCount( "face" );
			return new int[] { nVertices, nTriangles };
		}
		finally
		{
			reader.close();
		}
	}

	public static TriMesh open( final String source ) throws IOException
	{
		final File file = new File( source );
		final int[] nels = getNVerticesFaces( source );
		final int nVertices = nels[ 0 ];
		final int nTriangles = nels[ 1 ];
		final TriMesh mesh = new TriMesh( nVertices, nTriangles );
		read( file, mesh );
		return mesh;
	}

	public static void save( final TriMesh data, final String destination ) throws IOException
	{
		final byte[] bytes = writeBinary( data );
		FileUtils.writeFile( new File( destination ), bytes );
	}
}
