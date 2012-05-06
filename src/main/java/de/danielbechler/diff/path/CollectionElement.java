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

package de.danielbechler.diff.path;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class CollectionElement extends Element
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
		return "[" + Strings.toSingleLineString(item) + "]";
	}
}
