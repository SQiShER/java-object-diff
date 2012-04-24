package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public final class MapEntryAccessor extends AbstractAccessor
{
	private final List<?> referenceKeys;
	private final int index;

	public MapEntryAccessor(final List<?> referenceKeys, final int index)
	{
		Assert.notNull(referenceKeys, "referenceKeys");
		if (index < 0 || index > referenceKeys.size() - 1)
		{
			throw new IndexOutOfBoundsException("Index " + index + " is not within the valid range of the given List");
		}
		this.referenceKeys = referenceKeys;
		this.index = index;
	}

	public String getPropertyName()
	{
		return "[" + Integer.toString(index) + "]";
	}

	public PropertyPath.Element getPathElement()
	{
		return new MapElement(getReferenceKey());
	}

	public void set(final Object target, final Object value)
	{
		final Map<Object, Object> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.put(getReferenceKey(), value);
		}
	}

	public Object get(final Object target)
	{
		final Map<?, ?> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			return targetMap.get(getReferenceKey());
		}
		return null;
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

	private Object getReferenceKey()
	{
		return referenceKeys.get(index);
	}

	public void unset(final Object target)
	{
		final Map<?, ?> targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.remove(getReferenceKey());
		}
	}
}
