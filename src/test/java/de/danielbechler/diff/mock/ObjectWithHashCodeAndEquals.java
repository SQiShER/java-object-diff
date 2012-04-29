/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
