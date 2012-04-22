package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class CollectionElement<T> implements PropertyPath.Element
{
	private final T item;

	public CollectionElement(final T item)
	{
		this.item = item;
	}

	public T getItem()
	{
		return item;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof CollectionElement))
		{
			return false;
		}

		final CollectionElement that = (CollectionElement) o;

		if (item != null ? !item.equals(that.item) : that.item != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return item != null ? item.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return "item[" + item + "]";
	}
}
