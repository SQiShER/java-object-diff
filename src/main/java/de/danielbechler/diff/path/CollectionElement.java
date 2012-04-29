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
		return "item[" + Strings.toSingleLineString(item) + "]";
	}
}
