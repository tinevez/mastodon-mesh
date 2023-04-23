package org.mastodon.mesh;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.mastodon.mesh.io.PLYMeshIO;

import net.imagej.mesh.Mesh;

public class TriMeshTest
{
	public static void main( final String[] args ) throws IOException, URISyntaxException
	{
		final URI meshURI = TriMesh.class.getResource( "cone.ply" ).toURI();

		System.out.println( "Loading to TriMesh" );
		final TriMesh tm1 = PLYMeshIO.open( meshURI.getPath() );
		System.out.println( tm1 );
		System.out.println();

		System.out.println( "Resaving to PLY file" );
		PLYMeshIO.save( tm1, "samples/cone2.ply" );
		System.out.println();

		System.out.println( "Loading to ImageJ Mesh" );
		final Mesh source = new net.imagej.mesh.io.ply.PLYMeshIO().open( meshURI.getPath() );
		System.out.println( String.format( "Mesh %d vertices, %d faces",
				source.vertices().size(), source.triangles().size() ) );
		System.out.println();

		System.out.println( "Converting from Mesh to TriMesh" );
		final TriMesh mesh = Meshes.from( source );
		System.out.println( mesh );
	}
}
