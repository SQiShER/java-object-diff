package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"unchecked"})
public final class CollectionItemAccessor<T> extends AbstractAccessor<T>
{
	private final T referenceItem;

	public CollectionItemAccessor(final T referenceItem)
	{
		this.referenceItem = referenceItem;
	}

	@Override
	public String getPropertyName()
	{
		return "[" + referenceItem + "]";
	}

	@Override
	public PropertyPath.Element toPathElement()
	{
		return new CollectionElement<T>(referenceItem);
	}

	@Override
	public PropertyPath getPath()
	{
		return new PropertyPath(toPathElement());
	}

	@Override
	public void set(final Object target, final Object value)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return;
		}
		final T previous = get(target);
		if (previous != null)
		{
			targetCollection.remove(previous);
		}
		targetCollection.add(value);
	}

	@Override
	public T get(final Object target)
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
				return (T) item;
			}
		}
		return null;
	}

	private static Collection<?> objectAsCollection(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		else if (!(object instanceof Collection))
		{
			throw new IllegalArgumentException(object.getClass().toString());
		}
		return (Collection) object;
	}

	@Override
	public void unset(final Object target, final Object value)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection != null)
		{
			targetCollection.remove(referenceItem);
		}
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final CollectionItemAccessor that = (CollectionItemAccessor) o;

		if (!referenceItem.equals(that.referenceItem))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return referenceItem.hashCode();
	}
}
