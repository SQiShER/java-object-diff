/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.MapEntryAccessor;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Used to find differences between {@link Map Maps}
 *
 * @author Daniel Bechler
 */
public final class MapDiffer implements Differ
{
	private final ComparisonStrategyResolver comparisonStrategyResolver;
	private final DifferDispatcher differDispatcher;

	public MapDiffer(final DifferDispatcher differDispatcher, final ComparisonStrategyResolver comparisonStrategyResolver)
	{
		Assert.notNull(differDispatcher, "differDispatcher");
		this.differDispatcher = differDispatcher;
		this.comparisonStrategyResolver = comparisonStrategyResolver;
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

	public boolean accepts(final Class<?> type)
	{
		if (type != null)
		{
			return Map.class.isAssignableFrom(type);
		}
		return false;
	}

	public final DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		final DiffNode mapNode = new DiffNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (instances.hasBeenAdded())
		{
			compareEntries(mapNode, instances, instances.getWorking(Map.class).keySet());
			mapNode.setState(DiffNode.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			compareEntries(mapNode, instances, instances.getBase(Map.class).keySet());
			mapNode.setState(DiffNode.State.REMOVED);
		}
		else if (instances.areSame())
		{
			mapNode.setState(DiffNode.State.UNTOUCHED);
		}
		else if (comparisonStrategyResolver.resolveComparisonStrategy(mapNode) != null)
		{
			comparisonStrategyResolver.resolveComparisonStrategy(mapNode).compare(mapNode, instances.getType(), instances.getWorking(Map.class), instances.getBase(Map.class));
		}
		else
		{
			compareEntries(mapNode, instances, findAddedKeys(instances));
			compareEntries(mapNode, instances, findRemovedKeys(instances));
			compareEntries(mapNode, instances, findKnownKeys(instances));
		}
		return mapNode;
	}

	private void compareEntries(final DiffNode mapNode, final Instances mapInstances, final Iterable<?> keys)
	{
		for (final Object key : keys)
		{
			differDispatcher.dispatch(mapNode, mapInstances, new MapEntryAccessor(key));
		}
	}
}
