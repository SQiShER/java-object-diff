package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class CollectionElement extends PropertyPath.Element
{
	private final Object item;

	public CollectionElement(final Object item)
	{
		this.item = item;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Object getItem()
	{
		return item;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (!(element instanceof CollectionElement))
		{
			return false;
		}

		final CollectionElement that = (CollectionElement) element;

		if (item != null ? !item.equals(that.item) : that.item != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int calculateHashCode()
	{
		return item != null ? item.hashCode() : 0;
	}

	@Override
	public String asString()
	{
		return "item[" + item + "]";
	}
}
