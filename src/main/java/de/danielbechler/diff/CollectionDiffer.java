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
import de.danielbechler.util.Collections;

import java.util.*;

/**
 * Used to find differences between {@link Collection Collections}.
 *
 * @author Daniel Bechler
 */
final class CollectionDiffer extends AbstractDiffer<CollectionNode>
{
	public CollectionDiffer(final DelegatingObjectDiffer delegate, final Configuration configuration)
	{
		super(delegate, configuration);
	}

	public CollectionNode compare(final Collection<?> working, final Collection<?> base)
	{
		return compare(Node.ROOT, Instances.of(new RootAccessor(), working, base));
	}

	@Override
	protected CollectionNode internalCompare(final Node parentNode, final Instances instances)
	{
		final CollectionNode node = newNode(parentNode, instances);
		if (getConfiguration().isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (instances.hasBeenAdded())
		{
			handleItems(node, instances, instances.getWorking(Collection.class));
			node.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			handleItems(node, instances, instances.getBase(Collection.class));
			node.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else if (getConfiguration().isEqualsOnly(node))
		{
			if (instances.areEqual())
			{
				node.setState(Node.State.UNTOUCHED);
			}
			else
			{
				node.setState(Node.State.CHANGED);
			}
		}
		else
		{
			handleItems(node, instances, findAddedItems(instances));
			handleItems(node, instances, findRemovedItems(instances));
			handleItems(node, instances, findKnownItems(instances));
		}
		return node;
	}

	@Override
	protected CollectionNode newNode(final Node parentNode, final Instances instances)
	{
		return new CollectionNode(parentNode, instances.getSourceAccessor(), instances.getType());
	}

	private void handleItems(final CollectionNode collectionNode,
							 final Instances instances,
							 final Iterable<?> items)
	{
		for (final Object item : items)
		{
			final Node child = compareItem(collectionNode, instances, item);
			if (getConfiguration().isReturnable(child))
			{
				collectionNode.addChild(child);
			}
		}
	}

	private Node compareItem(final CollectionNode node, final Instances instances, final Object item)
	{
		final Accessor itemAccessor = node.accessorForItem(item);
		final Instances itemInstances = instances.access(itemAccessor);
		return delegate(node, itemInstances);
	}

	private static Collection<?> findAddedItems(final Instances instances)
	{
		//noinspection unchecked
		return Collections.filteredCopyOf(instances.getWorking(Collection.class), instances.getBase(Collection.class));
	}

	private static Collection<?> findRemovedItems(final Instances instances)
	{
		//noinspection unchecked
		return Collections.filteredCopyOf(instances.getBase(Collection.class), instances.getWorking(Collection.class));
	}

	private static Iterable<?> findKnownItems(final Instances instances)
	{
		@SuppressWarnings({"unchecked"})
		final Collection<?> changed = new ArrayList<Object>(instances.getWorking(Collection.class));
		changed.removeAll(findAddedItems(instances));
		changed.removeAll(findRemovedItems(instances));
		return changed;
	}
}
