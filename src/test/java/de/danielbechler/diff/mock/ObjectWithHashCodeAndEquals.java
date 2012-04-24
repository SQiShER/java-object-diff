/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

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
