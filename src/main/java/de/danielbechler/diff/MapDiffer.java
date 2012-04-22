package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.Collections;

import java.util.*;

/** @author Daniel Bechler */
public class MapDiffer extends AbstractObjectDiffer
{
	private final ObjectDiffer delegate;

	public MapDiffer()
	{
		this(new BeanDiffer());
	}

	public MapDiffer(final BeanDiffer delegate)
	{
		super(delegate);
		this.delegate = delegate;
	}

	public <K, V> MapNode<K, V> compare(final Map<K, V> modifiedMap, final Map<K, V> baseMap)
	{
		return compare(new Instances<Map<K, V>>(modifiedMap, baseMap, null), new RootAccessor<Map<K, V>>());
	}

	public <K, V> MapNode<K, V> compare(final Instances<Map<K, V>> instances, final Accessor<Map<K, V>> accessor)
	{
		final MapNode<K, V> node = new MapNode<K, V>(accessor);

		indexAll(instances, node);

		if (instances.isAdded())
		{
			addChildNodesIfTouched(instances, node, instances.getWorking().keySet());
			node.setType(DifferenceType.ADDED);
		}
		else if (instances.isRemoved())
		{
			addChildNodesIfTouched(instances, node, instances.getBase().keySet());
			node.setType(DifferenceType.REMOVED);
		}
		else if (instances.isNull())
		{
			node.setType(DifferenceType.UNTOUCHED);
		}
		else
		{
			addChildNodesIfTouched(instances, node, findAddedKeys(instances));
			addChildNodesIfTouched(instances, node, findRemovedKeys(instances));
			addChildNodesIfTouched(instances, node, findKnownKeys(instances));
		}
		return node;
	}

	@Deprecated
	public <K, V> MapNode<K, V> compare(final Map<K, V> modifiedMap,
										final Map<K, V> baseMap,
										final Map<K, V> defaultMap,
										final Accessor<Map<K, V>> accessor)
	{
		return compare(new Instances<Map<K, V>>(modifiedMap, baseMap, defaultMap), accessor);
	}

	@SuppressWarnings({"unchecked"})
	private static <K, V> void indexAll(final Instances<Map<K, V>> instances, final MapNode<K, V> node)
	{
		node.indexKeys(instances.getWorking(), instances.getBase(), instances.getFresh());
	}

	private <K, V> Collection<? extends K> addChildNodesIfTouched(final Instances<Map<K, V>> instances,
																  final MapNode<K, V> parent,
																  final Collection<? extends K> keys)
	{
		final Collection<K> addedKeys = new LinkedHashSet<K>(keys.size());
		for (final K key : keys)
		{
			if (addChildNodeIfTouched(key, instances, parent))
			{
				addedKeys.add(key);
			}
		}
		return addedKeys;
	}

	private <K, V> boolean addChildNodeIfTouched(final K key,
												 final Instances<Map<K, V>> instances,
												 final MapNode<K, V> parent)
	{
		final DiffNode<?> node = compareEntry(key, instances, parent);
		if (node.getType() != DifferenceType.UNTOUCHED)
		{
			parent.setType(DifferenceType.CHANGED);
			parent.addChild(node);
			return true;
		}
		return false;
	}

	private <K, V> DiffNode<?> compareEntry(final K key, final Instances<Map<K, V>> instances, final MapNode<K, V> parent)
	{
		final Map<K, V> working = instances.getWorking();
		final Map<K, V> base = instances.getBase();
		final Map<K, V> fresh = instances.getFresh();
		final Accessor<V> accessor = parent.accessorForKey(key);
		return delegate.compare(working, base, fresh, accessor);
	}

	private static <K, V> Collection<? extends K> findAddedKeys(final Instances<Map<K, V>> instances)
	{
		final Set<K> source = instances.getWorking().keySet();
		final Set<K> filter = instances.getBase().keySet();
		return Collections.filteredCopyOf(source, filter);
	}

	private static <K, V> Collection<? extends K> findRemovedKeys(final Instances<Map<K, V>> instances)
	{
		final Set<K> source = instances.getBase().keySet();
		final Set<K> filter = instances.getWorking().keySet();
		return Collections.filteredCopyOf(source, filter);
	}

	private static <K, V> Collection<K> findKnownKeys(final Instances<Map<K, V>> instances)
	{
		final Collection<K> changed = new LinkedHashSet<K>(instances.getWorking().keySet());
		changed.removeAll(findAddedKeys(instances));
		changed.removeAll(findRemovedKeys(instances));
		return changed;
	}
}
