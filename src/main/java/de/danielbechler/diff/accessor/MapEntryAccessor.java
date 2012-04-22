package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"unchecked"})
public final class MapEntryAccessor<K, V> extends AbstractAccessor<V>
{
	private final List<K> referenceKeys;
	private final int index;

	public MapEntryAccessor(final List<K> referenceKeys, final int index)
	{
		Assert.notNull(referenceKeys, "Missing argument [referenceKeys]");
		this.referenceKeys = referenceKeys;
		if (index < 0 || index > referenceKeys.size() - 1)
		{
			throw new IllegalArgumentException("Index not found in given Collection");
		}
		this.index = index;
	}

	public String getPropertyName()
	{
		return "[" + Integer.toString(index) + "]";
	}

	public PropertyPath.Element toPathElement()
	{
		return new MapElement(getReferenceKey());
	}

	public PropertyPath getPath()
	{
		return new PropertyPath(toPathElement());
	}

	public void set(final Object target, final Object value)
	{
		final Map targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.put(getReferenceKey(), value);
		}
	}

	public V get(final Object target)
	{
		final Map targetMap = objectToMap(target);
		if (targetMap != null)
		{
			return (V) targetMap.get(getReferenceKey());
		}
		return null;
	}

	private static Map objectToMap(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		if (!(object instanceof Map))
		{
			throw new IllegalArgumentException(object.getClass().toString());
		}
		return (Map) object;
	}

	private K getReferenceKey()
	{
		return referenceKeys.get(index);
	}

	public void unset(final Object target, final Object value)
	{
		final Map targetMap = objectToMap(target);
		if (targetMap != null)
		{
			targetMap.remove(getReferenceKey());
		}
	}
}
