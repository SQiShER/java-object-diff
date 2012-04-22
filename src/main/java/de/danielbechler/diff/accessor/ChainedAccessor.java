package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public final class ChainedAccessor<T> implements ChainingAccessor<T>
{
	private final Accessor<T> childAccessor;
	private final Accessor<?> parentAccessor;

	public ChainedAccessor(final Accessor<?> parentAccessor, final Accessor<T> childAccessor)
	{
		Assert.notNull(childAccessor, "Missing argument [accessor]");
		if (childAccessor instanceof ChainingAccessor)
		{
			throw new IllegalArgumentException("Child accessors must not be chained");
		}
		this.childAccessor = childAccessor;
		Assert.notNull(parentAccessor, "Missing argument [parentAccessor]");
		this.parentAccessor = parentAccessor;
	}

	public void set(final Object target, final Object value)
	{
		childAccessor.set(parentAccessor.get(target), value);
	}

	public T get(final Object target)
	{
		final Object parent = parentAccessor.get(target);
		if (childAccessor instanceof ChainingAccessor)
		{
			final ChainingAccessor<T> chainingAccessor = (ChainingAccessor<T>) childAccessor;
			chainingAccessor.getDetachedChildAccessor();
			return chainingAccessor.get(parent);
		}
		else
		{
			return childAccessor.get(parent);
		}
	}

	public void unset(final Object target, final Object value)
	{
		childAccessor.unset(parentAccessor.get(target), value);
	}

	public String getPropertyName()
	{
		return childAccessor.getPropertyName();
	}

	public PropertyPath.Element toPathElement()
	{
		return childAccessor.toPathElement();
	}

	public PropertyPath getPath()
	{
		return new PropertyPath(parentAccessor.getPath(), toPathElement());
	}

	public Set<String> getCategories()
	{
		final Set<String> categories = new TreeSet<String>();
		categories.addAll(childAccessor.getCategories());

		// inherit parent categories
		if (parentAccessor != null)
		{
			categories.addAll(parentAccessor.getCategories());
		}

		return categories;
	}

	public Accessor<?> getParentAccessor()
	{
		return parentAccessor;
	}

	public Accessor<T> getDetachedChildAccessor()
	{
		if (childAccessor instanceof ChainingAccessor)
		{
			return ((ChainingAccessor<T>) childAccessor).getDetachedChildAccessor();
		}
		return childAccessor;
	}
}
