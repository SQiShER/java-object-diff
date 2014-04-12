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

package de.danielbechler.diff.selector;

import de.danielbechler.util.Strings;

/**
 * @author Daniel Bechler
 */
public final class MapKeyElementSelector extends ElementSelector
{
	private final Object key;

	public MapKeyElementSelector(final Object key)
	{
		this.key = key;
	}

	/**
	 * @deprecated Low-level API. Don't use in production code. May be removed in future versions.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	@Deprecated
	public Object getKey()
	{
		return key;
	}

	@Override
	public String toHumanReadableString()
	{
		return "{" + Strings.toSingleLineString(key) + "}";
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

		final MapKeyElementSelector that = (MapKeyElementSelector) o;

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
}
