package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class MapElement<K> implements PropertyPath.Element
{
	private final K key;

	public MapElement(final K key)
	{
		this.key = key;
	}

//	@Override
//	public boolean matches(final IDifference<?> difference)
//	{
//		return difference != null && difference.getAccessor().getPropertySelector().equals(this);
//	}

	public K getKey()
	{
		return key;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof MapElement))
		{
			return false;
		}

		final MapElement that = (MapElement) o;

		if (key != null ? !key.equals(that.key) : that.key != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return key != null ? key.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return "key[" + key + "]";
	}
}
