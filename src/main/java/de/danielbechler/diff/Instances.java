package de.danielbechler.diff;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class Instances<T>
{
	private final T working;
	private final T base;
	private final T fresh;

	public Instances(final T working, final T base, final T fresh)
	{
		Assert.sameTypesOrNull(working, base, fresh);
		this.working = working;
		this.base = base;
		this.fresh = fresh;
	}

	public T getWorking()
	{
		return working;
	}

	public T getBase()
	{
		return base;
	}

	public T getFresh()
	{
		return fresh;
	}

	public boolean isAdded()
	{
		return working != null && base == null;
	}

	public boolean isRemoved()
	{
		return base != null && working == null;
	}

	public boolean isNull()
	{
		return base == null && working == null;
	}
}
