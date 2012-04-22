package de.danielbechler.diff.mock;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class NestableCollectionSafeObject
{
	private final String key;

	private String value;
	private NestableCollectionSafeObject item;

	public NestableCollectionSafeObject(final String key)
	{
		Assert.hasText(key, "key");
		this.key = key;
	}

	public NestableCollectionSafeObject(final String key, final String value)
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

	public NestableCollectionSafeObject getItem()
	{
		return item;
	}

	public NestableCollectionSafeObject setItem(final NestableCollectionSafeObject item)
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

		final NestableCollectionSafeObject item = (NestableCollectionSafeObject) o;

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
}
