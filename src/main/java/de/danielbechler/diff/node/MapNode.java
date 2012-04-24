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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.accessor.exception.*;

import java.util.*;

/** @author Daniel Bechler */
public class MapNode extends DefaultNode
{
	private final List<Object> referenceKeys = new LinkedList<Object>();

	public MapNode(final Node parentNode, final Accessor accessor)
	{
		super(parentNode, accessor);
	}

	public final int indexKey(final Object key)
	{
		if (!isIndexed(key))
		{
			referenceKeys.add(key);
		}
		return indexOf(key);
	}

	public final void indexKeys(final Map<?, ?> map)
	{
		if (map != null)
		{
			for (final Object key : map.keySet())
			{
				indexKey(key);
			}
		}
	}

	public final void indexKeys(final Map<?, ?> map, final Map<?, ?>... additionalMaps)
	{
		indexKeys(map);
		for (final Map<?, ?> additionalMap : additionalMaps)
		{
			indexKeys(additionalMap);
		}
	}

	@SuppressWarnings({"unchecked"})
	public Accessor accessorForKey(final Object key)
	{
		if (!isIndexed(key))
		{
			throw new ItemNotIndexedException(key);
		}
		return new MapEntryAccessor(referenceKeys, indexOf(key));
	}

	private boolean isIndexed(final Object key)
	{
		return referenceKeys.contains(key);
	}

	private int indexOf(final Object key)
	{
		return referenceKeys.indexOf(key);
	}

	@Override
	public final boolean isMapDifference()
	{
		return true;
	}

	@Override
	public final MapNode toMapDifference()
	{
		return this;
	}
}
