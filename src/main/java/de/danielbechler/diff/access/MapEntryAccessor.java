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

package de.danielbechler.diff.access;

import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.diff.selector.MapKeyElementSelector;
import de.danielbechler.util.Assert;

import java.util.Map;

/**
 * @author Daniel Bechler
 */
public final class MapEntryAccessor implements Accessor
{
	private final Object referenceKey;

	public MapEntryAccessor(final Object referenceKey)
	{
		Assert.notNull(referenceKey, "referenceKey");
		this.referenceKey = referenceKey;
	}

	public Object getKey(final Map<?, ?> target)
	{
		final Map<Object, Object> map = objectToMap(target);
		if (map == null)
		{
			return null;
		}
		final Object referenceKey = this.referenceKey;
		for (final Object key : map.keySet())
		{
			if (key == referenceKey || key.equals(referenceKey))
			{
				return key;
			}
		}
		return null;
	}

	@Override
	public int hashCode()
	{
		return referenceKey.hashCode();
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
		return referenceKey.equals(((MapEntryAccessor) o).referenceKey);
	}

	@Override
	public String toString()
	{
		return "map key " + getElementSelector();
	}

	public ElementSelector getElementSelector()
	{
		return new MapKeyElementSelector(referenceKey);
	}

	public Object get(final Object target)
	{
		final Map<?, ?> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			return targetMap.get(referenceKey);
		}
		return null;
	}

	public void set(final Object target, final Object value)
	{
		final Map<Object, Object> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.put(referenceKey, value);
		}
	}

	public void unset(final Object target)
	{
		final Map<?, ?> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.remove(referenceKey);
		}
	}

	private static Map<Object, Object> objectToMap(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		if (object instanceof Map)
		{
			//noinspection unchecked
			return (Map<Object, Object>) object;
		}
		throw new IllegalArgumentException(object.getClass().toString());
	}
}
