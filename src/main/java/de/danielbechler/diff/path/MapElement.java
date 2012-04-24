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

package de.danielbechler.diff.path;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class MapElement extends PropertyPath.Element
{
	private final Object key;

	public MapElement(final Object key)
	{
		this.key = key;
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public Object getKey()
	{
		return key;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (!(element instanceof MapElement))
		{
			return false;
		}

		final MapElement that = (MapElement) element;

		if (key != null ? !key.equals(that.key) : that.key != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int calculateHashCode()
	{
		return key != null ? key.hashCode() : 0;
	}

	@Override
	public String asString()
	{
		return "key[" + Strings.toSingleLineString(key) + "]";
	}
}
