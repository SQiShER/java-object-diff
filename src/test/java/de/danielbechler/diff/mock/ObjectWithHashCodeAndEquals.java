package de.danielbechler.diff.mock;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class ObjectWithHashCodeAndEquals
{
	private final String key;

	private String value;
	private ObjectWithHashCodeAndEquals item;

	public ObjectWithHashCodeAndEquals(final String key)
	{
		Assert.hasText(key, "key");
		this.key = key;
	}

	public ObjectWithHashCodeAndEquals(final String key, final String value)
	{
		this(key);
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public ObjectWithHashCodeAndEquals getItem()
	{
		return item;
	}

	public ObjectWithHashCodeAndEquals setItem(final ObjectWithHashCodeAndEquals item)
	{
		this.item = item;
		return this;
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

		final ObjectWithHashCodeAndEquals item = (ObjectWithHashCodeAndEquals) o;

		if (!key.equals(item.key))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return key.hashCode();
	}

	@Override
	public String toString()
	{
		return key + ":" + value;
	}
}
