package de.danielbechler.diff.accessor;

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractAccessor<T> implements Accessor<T>
{
	private Set<String> categories = new TreeSet<String>();

	public final Set<String> getCategories()
	{
		return categories;
	}

	public final void setCategories(final Set<String> categories)
	{
		this.categories = categories;
	}
}
