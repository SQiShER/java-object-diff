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

package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public final class CollectionItemAccessor extends AbstractAccessor implements TypeAwareAccessor
{
	private final Object referenceItem;

	public CollectionItemAccessor(final Object referenceItem)
	{
		this.referenceItem = referenceItem;
	}

	public PropertyPath.Element getPathElement()
	{
		return new CollectionElement(referenceItem);
	}

	public void set(final Object target, final Object value)
	{
		final Collection<Object> targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return;
		}
		final Object previous = get(target);
		if (previous != null)
		{
			targetCollection.remove(previous);
		}
		targetCollection.add(value);
	}

	public Object get(final Object target)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return null;
		}
		for (final Object item : targetCollection)
		{
			if (item.equals(referenceItem))
			{
				return item;
			}
		}
		return null;
	}

	@Override
	public Class<?> getPropertyType()
	{
		return referenceItem != null ? referenceItem.getClass() : null;
	}

	private static Collection<Object> objectAsCollection(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		else if (object instanceof Collection)
		{
			//noinspection unchecked
			return (Collection<Object>) object;
		}
		throw new IllegalArgumentException(object.getClass().toString());
	}

	public void unset(final Object target)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection != null)
		{
			targetCollection.remove(referenceItem);
		}
	}
}
