package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;

import java.util.*;

/** @author Daniel Bechler */
public class MapNode<K, V> extends DefaultNode<Map<K, V>>
{
	private final List<K> referenceKeys = new LinkedList<K>();

	public MapNode(final Accessor<?> accessor)
	{
		super(accessor);
	}

	public final int indexKey(final K key)
	{
		if (!isIndexed(key))
		{
			referenceKeys.add(key);
		}
		return indexOf(key);
	}

	public final void indexKeys(final Map<K, V> map)
	{
		if (map != null)
		{
			for (final K key : map.keySet())
			{
				indexKey(key);
			}
		}
	}

	public final void indexKeys(final Map<K, V> map, final Map<K, V>... additionalMaps)
	{
		indexKeys(map);
		for (final Map<K, V> additionalMap : additionalMaps)
		{
			indexKeys(additionalMap);
		}
	}

	@SuppressWarnings({"unchecked"})
	public Accessor<V> accessorForKey(final K key)
	{
		if (!isIndexed(key))
		{
			throw new ItemNotIndexedException(key);
		}
		final Accessor<V> accessor = new MapEntryAccessor<K, V>(referenceKeys, indexOf(key));
		final Accessor<Map<K, V>> parentAccessor = getAccessor();
		if (parentAccessor != null)
		{
			return new ChainedAccessor<V>(parentAccessor, accessor);
		}
		return accessor;
	}

	private boolean isIndexed(final K key)
	{
		return referenceKeys.contains(key);
	}

	private int indexOf(final K key)
	{
		return referenceKeys.indexOf(key);
	}

	@Override
	public final boolean isMapDifference()
	{
		return true;
	}

	@Override
	public final MapNode<?, ?> toMapDifference()
	{
		return this;
	}
}
