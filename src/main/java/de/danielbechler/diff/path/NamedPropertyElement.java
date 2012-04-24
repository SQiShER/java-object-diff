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
public final class NamedPropertyElement extends PropertyPath.Element
{
	private final String propertyName;

	public NamedPropertyElement(final String propertyName)
	{
		Assert.hasText(propertyName, "propertyName");
		this.propertyName = propertyName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (!(element instanceof NamedPropertyElement))
		{
			return false;
		}

		final NamedPropertyElement that = (NamedPropertyElement) element;

		if (!propertyName.equals(that.propertyName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int calculateHashCode()
	{
		return propertyName.hashCode();
	}

	@Override
	public String asString()
	{
		return propertyName;
	}
}
