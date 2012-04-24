package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class MapElement extends PropertyPath.Element
{
	private final Object key;

	public MapElement(final Object key)
	{
		this.key = key;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Object getKey()
	{
		return key;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (!(element instanceof MapElement))
		{
			return false;
		}

		final MapElement that = (MapElement) element;

		if (key != null ? !key.equals(that.key) : that.key != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int calculateHashCode()
	{
		return key != null ? key.hashCode() : 0;
	}

	@Override
	public String asString()
	{
		return "key[" + key + "]";
	}
}
