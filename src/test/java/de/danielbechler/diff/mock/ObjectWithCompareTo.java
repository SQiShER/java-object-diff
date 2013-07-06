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

import de.danielbechler.util.Assert;

/** @author Daniel Bechler */
public class ObjectWithCompareTo implements Comparable<ObjectWithCompareTo>
{
	private final String key;

	private String value;
	private ObjectWithCompareTo item;

	public ObjectWithCompareTo(final String key)
	{
		Assert.hasText(key, "key");
		this.key = key;
	}

	public ObjectWithCompareTo(final String key, final String value)
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

	public ObjectWithCompareTo getItem()
	{
		return item;
	}

	public ObjectWithCompareTo setItem(final ObjectWithCompareTo item)
	{
		this.item = item;
		return this;
	}

    public int compareTo(ObjectWithCompareTo objectWithCompareTo) {
        return this.key.compareTo(objectWithCompareTo.key);
    }
}
