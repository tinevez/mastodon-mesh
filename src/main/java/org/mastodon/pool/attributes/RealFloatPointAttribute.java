package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.FloatArrayField;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;

/**
 * A version of {@link RealPointAttribute} that accepts a float array field.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <O>
 *            the type of pool object this attribute is defined on.
 */
public class RealFloatPointAttribute< O extends PoolObject< O, ?, ? > > extends AbstractAttribute< O >
{

	private final int offset;

	private final int n;

	public RealFloatPointAttribute( final FloatArrayField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
		this.n = layoutField.numElements();
	}

	public RealPointAttributeValue createAttributeValue( final O key )
	{
		return new Value( key );
	}

	public RealPointAttributeValue createQuietAttributeValue( final O key )
	{
		return new QuietValue( key );
	}

	abstract class AbstractValue implements RealPointAttributeValue
	{
		final O obj;

		public AbstractValue( final O obj )
		{
			this.obj = obj;
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void localize( final float[] position )
		{
			RealFloatPointAttribute.this.localize( obj, position );
		}

		@Override
		public void localize( final double[] position )
		{
			RealFloatPointAttribute.this.localize( obj, position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return RealFloatPointAttribute.this.getFloatPosition( obj, d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return RealFloatPointAttribute.this.getDoublePosition( obj, d );
		}
	}

	class Value extends AbstractValue
	{
		public Value( final O obj )
		{
			super( obj );
		}

		@Override
		public void move( final float distance, final int d )
		{
			RealFloatPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final double distance, final int d )
		{
			RealFloatPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			RealFloatPointAttribute.this.move( obj, localizable );
		}

		@Override
		public void move( final float[] distance )
		{
			RealFloatPointAttribute.this.move( obj, distance );
		}

		@Override
		public void move( final double[] distance )
		{
			RealFloatPointAttribute.this.move( obj, distance );
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			RealFloatPointAttribute.this.setPosition( obj, localizable );
		}

		@Override
		public void setPosition( final float[] position )
		{
			RealFloatPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final double[] position )
		{
			RealFloatPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final float position, final int d )
		{
			RealFloatPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void setPosition( final double position, final int d )
		{
			RealFloatPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void fwd( final int d )
		{
			RealFloatPointAttribute.this.fwd( obj, d );
		}

		@Override
		public void bck( final int d )
		{
			RealFloatPointAttribute.this.bck( obj, d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			RealFloatPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			RealFloatPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			RealFloatPointAttribute.this.move( obj, localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			RealFloatPointAttribute.this.move( obj, distance );
		}

		@Override
		public void move( final long[] distance )
		{
			RealFloatPointAttribute.this.move( obj, distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			RealFloatPointAttribute.this.setPosition( obj, localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			RealFloatPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			RealFloatPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			RealFloatPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			RealFloatPointAttribute.this.setPosition( obj, position, d );
		}
	}

	class QuietValue extends AbstractValue
	{
		public QuietValue( final O obj )
		{
			super( obj );
		}

		@Override
		public void move( final float distance, final int d )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final double distance, final int d )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, localizable );
		}

		@Override
		public void move( final float[] distance )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void move( final double[] distance )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, localizable );
		}

		@Override
		public void setPosition( final float[] position )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final double[] position )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final float position, final int d )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void setPosition( final double position, final int d )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void fwd( final int d )
		{
			RealFloatPointAttribute.this.fwdQuiet( obj, d );
		}

		@Override
		public void bck( final int d )
		{
			RealFloatPointAttribute.this.bckQuiet( obj, d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void move( final long[] distance )
		{
			RealFloatPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			RealFloatPointAttribute.this.setPositionQuiet( obj, position, d );
		}
	}

	public int numDimensions()
	{
		return n;
	}

	/*
	 * RealLocalizable methods with additional key argument.
	 */

	public void localize( final O key, final float[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) a.getDouble( offset + d * DOUBLE_SIZE );
	}

	public void localize( final O key, final double[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			position[ d ] = a.getDouble( offset + d * DOUBLE_SIZE );
	}

	public float getFloatPosition( final O key, final int d )
	{
		return ( float ) access( key ).getDouble( offset + d * DOUBLE_SIZE );
	}

	public double getDoublePosition( final O key, final int d )
	{
		return access( key ).getDouble( offset + d * DOUBLE_SIZE );
	}

	/*
	 * RealPositionable methods with additional key argument.
	 */

	public void fwd( final O key, final int d )
	{
		notifyBeforePropertyChange( key );
		fwdQuiet( key, d );
		notifyPropertyChanged( key );
	}

	public void bck( final O key, final int d )
	{
		notifyBeforePropertyChange( key );
		bckQuiet( key, d );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final double position, final int d )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position, d );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final int[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final long[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final float[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final double[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final RealLocalizable localizable )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, localizable );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final double distance, final int d )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance, d );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final int[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final long[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final float[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final double[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final RealLocalizable localizable )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, localizable );
		notifyPropertyChanged( key );
	}

	/*
	 * RealPositionable methods with additional key argument. "Quiet" versions
	 * that do not sent property change events.
	 */

	private static void addInPlace( final MappedElement a, final int index, final double increment )
	{
		a.putDouble( a.getDouble( index ) + increment, index );
	}

	public void fwdQuiet( final O key, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, 1 );
	}

	public void bckQuiet( final O key, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, -1 );
	}

	public void setPositionQuiet( final O key, final double position, final int d )
	{
		access( key ).putDouble( position, offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final int[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final long[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final float[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final double[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final RealLocalizable localizable )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( localizable.getDoublePosition( d ), offset + d * DOUBLE_SIZE );
	}

	public void moveQuiet( final O key, final double distance, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, distance );
	}

	public void moveQuiet( final O key, final int[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final long[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final float[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final double[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final RealLocalizable localizable )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, localizable.getDoublePosition( d ) );
	}
}
