package org.mastodon.mesh;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.mastodon.mesh.alg.MeshConnectedComponents;
import org.mastodon.mesh.io.PLYMeshIO;

import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealTypeConverters;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Cast;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class MarchingCubeTest
{
	public static < T extends RealType< T > > void main( final String[] args ) throws IOException, URISyntaxException
	{
		System.out.println( "Opening image." );
		final String filePath = "samples/CElegans3D-smoothed-mask-orig-t7.tif";
//		final String filePath = "samples/CElegans3D-smoothed-mask-orig-t7-crop.tif";
//		final String filePath = "samples/Cube.tif";
		final List< SCIFIOImgPlus< ? > > imgs = new ImgOpener().openImgs( filePath );
		final ImgPlus< T > img = Cast.unchecked( imgs.get( 0 ) );
		final double[] pixelSizes = new double[] {
				img.averageScale( img.dimensionIndex( Axes.X ) ),
				img.averageScale( img.dimensionIndex( Axes.Y ) ),
				img.averageScale( img.dimensionIndex( Axes.Z ) ) };
		System.out.println( Util.printCoordinates( pixelSizes ) );

		// First channel is the smoothed version.
		System.out.println( "Marching cube on grayscale." );
		final RandomAccessibleInterval< T > smoothed;
		if ( img.dimensionIndex( Axes.CHANNEL ) >= 0 )
			smoothed = Views.hyperSlice( img, img.dimensionIndex( Axes.CHANNEL ), 0 );
		else
			smoothed = img;

		final double isoLevel = 250;
		TriMesh mesh1 = Meshes.marchingCubes( smoothed, isoLevel );
		System.out.println( "Before removing duplicates: " + mesh1 );
		mesh1 = Meshes.removeDuplicateVertices( mesh1, 2 );
		System.out.println( "After removing duplicates: " + mesh1 );
		System.out.println( "Scaling." );
		Meshes.scale( mesh1, pixelSizes );
		System.out.println( "Saving." );
		PLYMeshIO.save( mesh1, filePath + "-c1.ply" );
		System.out.println( "Done." );

		// Test topology.
		System.out.println( "Results:" );
		System.out.println( mesh1 );
		System.out.println( "Is two-manifold: " + Meshes.isTwoManifold( mesh1 ) );
		System.out.println( "N connected components: " + Meshes.nConnectedComponents( mesh1 ) );
		System.out.println( "Splitting in connected components:" );
		int i = 0;
		for ( final TriMesh cc : MeshConnectedComponents.iterable( mesh1 ) )
		{
			i++;
			System.out.println( " # " + i + ": " + cc );
			PLYMeshIO.save( cc, filePath + "-c1-" + i + ".ply" );
		}
		System.out.println();

		// Second channel is the mask.
		System.out.println( "Marching cube on the mask." );
		final RandomAccessibleInterval< T > c2;
		if ( img.dimensionIndex( Axes.CHANNEL ) >= 0 )
			c2 = Views.hyperSlice( img, img.dimensionIndex( Axes.CHANNEL ), 1 );
		else
			c2 = img;
		final RandomAccessibleInterval< BitType > mask = RealTypeConverters.convert( c2, new BitType() );
		TriMesh mesh2 = Meshes.marchingCubes( mask );
		mesh2 = Meshes.removeDuplicateVertices( mesh2, 0 );
		Meshes.scale( mesh2, pixelSizes );
		PLYMeshIO.save( mesh2, filePath + "-c2.ply" );
		System.out.println( "Done." );
	}
}
