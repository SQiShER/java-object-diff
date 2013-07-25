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

package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;
import de.danielbechler.util.Collections;

import java.util.*;

/**
 * Used to find differences between {@link Map Maps}
 *
 * @author Daniel Bechler
 */
final class MapDiffer implements Differ<MapNode>
{
	private final DifferDelegator delegator;
	private final NodeInspector nodeInspector;
	private MapNodeFactory mapNodeFactory = new MapNodeFactory();

	public MapDiffer(final DifferDelegator delegator, final NodeInspector nodeInspector)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(nodeInspector, "nodeInspector");
		this.delegator = delegator;
		this.nodeInspector = nodeInspector;
	}

	public final MapNode compare(final Node parentNode, final Instances instances)
	{
		final MapNode mapNode = mapNodeFactory.createMapNode(parentNode, instances);
		indexAll(mapNode, instances);
		if (nodeInspector.isIgnored(mapNode))
		{
			mapNode.setState(Node.State.IGNORED);
		}
		else if (nodeInspector.isEqualsOnly(mapNode))
		{
			if (instances.areEqual())
			{
				mapNode.setState(Node.State.UNTOUCHED);
			}
			else
			{
				mapNode.setState(Node.State.CHANGED);
			}
		}
		else if (nodeInspector.isWithMethodEquals(mapNode)){
			String method = nodeInspector.getWithMethodEqualsMethod(mapNode);
			if (instances.areMethodResultEqual(method))
			{
				mapNode.setState(Node.State.UNTOUCHED);
			}
			else
			{
				mapNode.setState(Node.State.CHANGED);
			}
		}
		else if (instances.hasBeenAdded())
		{
			compareEntries(mapNode, instances, instances.getWorking(Map.class).keySet());
			mapNode.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			compareEntries(mapNode, instances, instances.getBase(Map.class).keySet());
			mapNode.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			mapNode.setState(Node.State.UNTOUCHED);
		}
		else
		{
			compareEntries(mapNode, instances, findAddedKeys(instances));
			compareEntries(mapNode, instances, findRemovedKeys(instances));
			compareEntries(mapNode, instances, findKnownKeys(instances));
		}
		return mapNode;
	}

	private static void indexAll(final MapNode node, final Instances instances)
	{
		indexKeys(node, instances.getWorking(Map.class));
		indexKeys(node, instances.getBase(Map.class));
		indexKeys(node, instances.getFresh(Map.class));
	}

	private static void indexKeys(final MapNode mapNode, final Map map)
	{
		if (map != null)
		{
			final Set<?> keys = map.keySet();
			for (final Object key : keys)
			{
				mapNode.indexKey(key);
			}
		}
	}

	private void compareEntries(final MapNode mapNode, final Instances mapInstances, final Iterable<?> keys)
	{
		for (final Object key : keys)
		{
			final Node node = compareEntry(mapNode, mapInstances, key);
			if (nodeInspector.isReturnable(node))
			{
				mapNode.addChild(node);
			}
		}
	}

	private Node compareEntry(final MapNode mapNode, final Instances mapInstances, final Object key)
	{
		final Accessor mapEntryAccessor = mapNode.accessorForKey(key);
		final Instances mapEntryInstances = mapInstances.access(mapEntryAccessor);
		return delegator.delegate(mapNode, mapEntryInstances);
	}

	private static Collection<?> findAddedKeys(final Instances instances)
	{
		final Set<?> source = instances.getWorking(Map.class).keySet();
		final Set<?> filter = instances.getBase(Map.class).keySet();
		return Collections.filteredCopyOf(source, filter);
	}

	private static Collection<?> findRemovedKeys(final Instances instances)
	{
		final Set<?> source = instances.getBase(Map.class).keySet();
		final Set<?> filter = instances.getWorking(Map.class).keySet();
		return Collections.filteredCopyOf(source, filter);
	}

	private static Iterable<?> findKnownKeys(final Instances instances)
	{
		final Set<?> keys = instances.getWorking(Map.class).keySet();
		final Collection<?> changed = Collections.setOf(keys);
		changed.removeAll(findAddedKeys(instances));
		changed.removeAll(findRemovedKeys(instances));
		return changed;
	}

	@TestOnly
	public void setMapNodeFactory(final MapNodeFactory mapNodeFactory)
	{
		this.mapNodeFactory = mapNodeFactory;
	}
}
