package de.danielbechler.diff.accessor;

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractAccessor implements Accessor
{
	private Set<String> categories = new TreeSet<String>();
	private boolean equalsOnly;
	private boolean ignored;

	public final Set<String> getCategories()
	{
		return categories;
	}

	public final void setCategories(final Set<String> categories)
	{
		this.categories = categories;
	}

	public boolean isEqualsOnly()
	{
		return equalsOnly;
	}

	public void setEqualsOnly(final boolean equalsOnly)
	{
		this.equalsOnly = equalsOnly;
	}

	public boolean isIgnored()
	{
		return ignored;
	}

	public void setIgnored(final boolean ignored)
	{
		this.ignored = ignored;
	}
}
