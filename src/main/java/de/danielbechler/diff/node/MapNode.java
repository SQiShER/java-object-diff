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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.accessor.exception.*;

import java.util.*;

/** @author Daniel Bechler */
public class MapNode extends DefaultNode
{
	private final List<Object> referenceKeys = new LinkedList<Object>();

	public MapNode(final Node parentNode, final Accessor accessor, final Class<?> valueType)
	{
		super(parentNode, accessor, valueType);
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
