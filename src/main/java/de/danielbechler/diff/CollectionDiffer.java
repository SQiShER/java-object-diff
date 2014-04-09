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

import de.danielbechler.diff.node.Accessor;
import de.danielbechler.diff.node.CollectionItemAccessor;
import de.danielbechler.diff.config.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.config.comparison.ComparisonStrategy;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Collections;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Used to find differences between {@link Collection Collections}.
 *
 * @author Daniel Bechler
 */
public final class CollectionDiffer implements Differ
{
	private final DifferDispatcher differDispatcher;
	private final ComparisonStrategyResolver comparisonStrategyResolver;

	public CollectionDiffer(final DifferDispatcher differDispatcher,
							final ComparisonStrategyResolver comparisonStrategyResolver)
	{
		Assert.notNull(differDispatcher, "differDispatcher");
		this.differDispatcher = differDispatcher;

		Assert.notNull(comparisonStrategyResolver, "comparisonStrategyResolver");
		this.comparisonStrategyResolver = comparisonStrategyResolver;
	}

	public boolean accepts(final Class<?> type)
	{
		return Collection.class.isAssignableFrom(type);
	}

	public final DiffNode compare(final DiffNode parentNode, final Instances collectionInstances)
	{
		final DiffNode collectionNode = newNode(parentNode, collectionInstances);
		if (collectionInstances.hasBeenAdded())
		{
			final Collection addedItems = collectionInstances.getWorking(Collection.class);
			compareItems(collectionNode, collectionInstances, addedItems);
			collectionNode.setState(DiffNode.State.ADDED);
		}
		else if (collectionInstances.hasBeenRemoved())
		{
			final Collection<?> removedItems = collectionInstances.getBase(Collection.class);
			compareItems(collectionNode, collectionInstances, removedItems);
			collectionNode.setState(DiffNode.State.REMOVED);
		}
		else if (collectionInstances.areSame())
		{
			collectionNode.setState(DiffNode.State.UNTOUCHED);
		}
		else
		{
			final ComparisonStrategy comparisonStrategy = comparisonStrategyResolver.resolveComparisonStrategy(collectionNode);
			if (comparisonStrategy == null)
			{
				compareInternally(collectionNode, collectionInstances);
			}
			else
			{
				compareUsingComparisonStrategy(collectionNode, collectionInstances, comparisonStrategy);
			}
		}
		return collectionNode;
	}

	private static void compareUsingComparisonStrategy(final DiffNode collectionNode,
													   final Instances collectionInstances,
													   final ComparisonStrategy comparisonStrategy)
	{
		comparisonStrategy.compare(collectionNode, collectionInstances.getType(), collectionInstances.getWorking(Collection.class), collectionInstances.getBase(Collection.class));
	}

	private void compareInternally(final DiffNode collectionNode, final Instances collectionInstances)
	{
		compareItems(collectionNode, collectionInstances, addedItemsOf(collectionInstances));
		compareItems(collectionNode, collectionInstances, removedItemsOf(collectionInstances));
		compareItems(collectionNode, collectionInstances, knownItemsOf(collectionInstances));
	}

	private static DiffNode newNode(final DiffNode parentNode, final Instances collectionInstances)
	{
		final Accessor accessor = collectionInstances.getSourceAccessor();
		final Class<?> type = collectionInstances.getType();
		return new DiffNode(parentNode, accessor, type);
	}

	private void compareItems(final DiffNode collectionNode,
							  final Instances collectionInstances,
							  final Iterable<?> items)
	{
		for (final Object item : items)
		{
			final Accessor itemAccessor = new CollectionItemAccessor(item);
			differDispatcher.dispatch(collectionNode, collectionInstances, itemAccessor);
		}
	}

	private static Collection<?> addedItemsOf(final Instances instances)
	{
		final Collection<?> working = instances.getWorking(Collection.class);
		final Collection<?> base = instances.getBase(Collection.class);
		return Collections.filteredCopyOf(working, base);
	}

	private static Collection<?> removedItemsOf(final Instances instances)
	{
		final Collection<?> working = instances.getWorking(Collection.class);
		final Collection<?> base = instances.getBase(Collection.class);
		return Collections.filteredCopyOf(base, working);
	}

	private static Iterable<?> knownItemsOf(final Instances instances)
	{
		final Collection<?> working = instances.getWorking(Collection.class);
		final Collection<Object> changed = new ArrayList<Object>(working);
		changed.removeAll(addedItemsOf(instances));
		changed.removeAll(removedItemsOf(instances));
		return changed;
	}
}
