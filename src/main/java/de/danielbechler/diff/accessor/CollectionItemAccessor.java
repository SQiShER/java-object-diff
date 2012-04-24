package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public final class CollectionItemAccessor extends AbstractAccessor
{
	private final Object referenceItem;

	public CollectionItemAccessor(final Object referenceItem)
	{
		this.referenceItem = referenceItem;
	}

	public PropertyPath.Element getPathElement()
	{
		return new CollectionElement(referenceItem);
	}

	public void set(final Object target, final Object value)
	{
		final Collection<Object> targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return;
		}
		final Object previous = get(target);
		if (previous != null)
		{
			targetCollection.remove(previous);
		}
		targetCollection.add(value);
	}

	public Object get(final Object target)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return null;
		}
		for (final Object item : targetCollection)
		{
			if (item.equals(referenceItem))
			{
				return item;
			}
		}
		return null;
	}

	private static Collection<Object> objectAsCollection(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		else if (object instanceof Collection)
		{
			//noinspection unchecked
			return (Collection<Object>) object;
		}
		throw new IllegalArgumentException(object.getClass().toString());
	}

	public void unset(final Object target)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection != null)
		{
			targetCollection.remove(referenceItem);
		}
	}
}
