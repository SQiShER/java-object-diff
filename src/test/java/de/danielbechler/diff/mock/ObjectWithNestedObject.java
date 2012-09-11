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
public class ObjectWithNestedObject
{
	private final String id;

	private ObjectWithNestedObject object;

	public ObjectWithNestedObject(final String id)
	{
		this(id, null);
	}

	public ObjectWithNestedObject(final String id, final ObjectWithNestedObject object)
	{
		Assert.hasText(id, "id");
		this.id = id;
		this.object = object;
	}

	public String getId()
	{
		return id;
	}

	public ObjectWithNestedObject getObject()
	{
		return object;
	}

	public void setObject(final ObjectWithNestedObject object)
	{
		this.object = object;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ObjectWithNestedObject))
		{
			return false;
		}

		final ObjectWithNestedObject that = (ObjectWithNestedObject) o;

		if (!id.equals(that.id))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public String toString()
	{
		return id;
	}
}
