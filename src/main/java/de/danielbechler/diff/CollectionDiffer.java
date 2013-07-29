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
 * Used to find differences between {@link Collection Collections}.
 *
 * @author Daniel Bechler
 */
final class CollectionDiffer implements Differ<CollectionNode>
{
	private final DifferDelegator delegator;
	private final NodeInspector nodeInspector;
	private CollectionNodeFactory collectionNodeFactory = new CollectionNodeFactory();
	private CollectionItemAccessorFactory collectionItemAccessorFactory = new CollectionItemAccessorFactory();

	public CollectionDiffer(final DifferDelegator delegator, final NodeInspector nodeInspector)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(nodeInspector, "nodeInspector");
		this.delegator = delegator;
		this.nodeInspector = nodeInspector;
	}

	public final CollectionNode compare(final Node parentNode, final Instances collectionInstances)
	{
		final CollectionNode collectionNode = collectionNodeFactory.create(parentNode, collectionInstances);
		if (nodeInspector.isIgnored(collectionNode))
		{
			collectionNode.setState(Node.State.IGNORED);
		}
		else if (nodeInspector.isEqualsOnly(collectionNode))
		{
			if (collectionInstances.areEqual())
			{
				collectionNode.setState(Node.State.UNTOUCHED);
			}
			else
			{
				collectionNode.setState(Node.State.CHANGED);
			}
		}
		else if (nodeInspector.hasEqualsOnlyValueProviderMethod(collectionNode)){
			String method = nodeInspector.getEqualsOnlyValueProviderMethod(collectionNode);
			if (collectionInstances.areMethodResultsEqual(method))
			{
				collectionNode.setState(Node.State.UNTOUCHED);
			}
			else
			{
				collectionNode.setState(Node.State.CHANGED);
			}
		}
		else if (collectionInstances.hasBeenAdded())
		{
			compareItems(collectionNode, collectionInstances, collectionInstances.getWorking(Collection.class));
			collectionNode.setState(Node.State.ADDED);
		}
		else if (collectionInstances.hasBeenRemoved())
		{
			compareItems(collectionNode, collectionInstances, collectionInstances.getBase(Collection.class));
			collectionNode.setState(Node.State.REMOVED);
		}
		else if (collectionInstances.areSame())
		{
			collectionNode.setState(Node.State.UNTOUCHED);
		}
		else
		{
			compareItems(collectionNode, collectionInstances, addedItemsOf(collectionInstances));
			compareItems(collectionNode, collectionInstances, removedItemsOf(collectionInstances));
			compareItems(collectionNode, collectionInstances, knownItemsOf(collectionInstances));
		}
		return collectionNode;
	}

	private void compareItems(final Node collectionNode, final Instances instances, final Iterable<?> items)
	{
		for (final Object item : items)
		{
			final Node child = compareItem(collectionNode, instances, item);
			if (nodeInspector.isReturnable(child))
			{
				collectionNode.addChild(child);
			}
		}
	}

	private Node compareItem(final Node collectionNode,
							 final Instances collectionInstances,
							 final Object collectionItem)
	{
		final CollectionItemAccessor itemAccessor = collectionItemAccessorFactory.createAccessorForItem(collectionItem);
		final Instances itemInstances = collectionInstances.access(itemAccessor);
		return delegator.delegate(collectionNode, itemInstances);
	}

	@SuppressWarnings("unchecked")
	private static Collection<?> addedItemsOf(final Instances instances)
	{
		return Collections.filteredCopyOf(instances.getWorking(Collection.class), instances.getBase(Collection.class));
	}

	@SuppressWarnings("unchecked")
	private static Collection<?> removedItemsOf(final Instances instances)
	{
		return Collections.filteredCopyOf(instances.getBase(Collection.class), instances.getWorking(Collection.class));
	}

	@SuppressWarnings("unchecked")
	private static Iterable<?> knownItemsOf(final Instances instances)
	{
		final Collection<?> changed = new ArrayList<Object>(instances.getWorking(Collection.class));
		changed.removeAll(addedItemsOf(instances));
		changed.removeAll(removedItemsOf(instances));
		return changed;
	}

	@TestOnly
	void setCollectionNodeFactory(final CollectionNodeFactory collectionNodeFactory)
	{
		this.collectionNodeFactory = collectionNodeFactory;
	}

	@TestOnly
	void setCollectionItemAccessorFactory(final CollectionItemAccessorFactory collectionItemAccessorFactory)
	{
		this.collectionItemAccessorFactory = collectionItemAccessorFactory;
	}
}
