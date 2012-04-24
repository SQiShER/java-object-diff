package de.danielbechler.diff;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
abstract class AbstractDiffer implements Differ, Configurable
{
	private ObjectDiffer delegate;

	protected AbstractDiffer()
	{
	}

	protected AbstractDiffer(final ObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	public final ObjectDiffer getDelegate()
	{
		return delegate;
	}

	public final void setDelegate(final ObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	public final Configuration getConfiguration()
	{
		return delegate.getConfiguration();
	}
}
