/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.selector;

import de.danielbechler.util.Strings;

public final class ListItemSelector extends ElementSelector
{
	private final Object item;
	private final int ordinal;

	public ListItemSelector(final Object item)
	{
		this(item, 0);
	}

	public ListItemSelector(final Object item, final int ordinal)
	{
		this.item = item;
		this.ordinal = ordinal;
	}

	@Override
	public String toHumanReadableString()
	{
		return "[" + Strings.toSingleLineString(item) + "]";
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

		final ListItemSelector that = (ListItemSelector) o;

		if (ordinal != that.ordinal)
		{
			return false;
		}
		if (item != null ? !item.equals(that.item) : that.item != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + ordinal;
		return result;
	}
}
