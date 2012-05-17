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
final class CollectionDiffer extends AbstractDiffer
{
	public CollectionDiffer()
	{
		setDelegate(new DelegatingObjectDifferImpl(null, null, this));
	}

	public CollectionDiffer(final DelegatingObjectDiffer delegate)
	{
		super(delegate);
	}

	public CollectionNode compare(final Collection<?> working, final Collection<?> base)
	{
		return compare(Node.ROOT, Instances.of(new RootAccessor(), working, base));
	}

	public CollectionNode compare(final Node parentNode, final Instances instances)
	{
		final CollectionNode node = new CollectionNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (getDelegate().isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (instances.getWorking() != null && instances.getBase() == null)
		{
			handleItems(node, instances, instances.getWorking(Collection.class));
			node.setState(Node.State.ADDED);
		}
		else if (instances.getWorking() == null && instances.getBase() != null)
		{
			handleItems(node, instances, instances.getBase(Collection.class));
			node.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			handleItems(node, instances, findAddedItems(instances));
			handleItems(node, instances, findRemovedItems(instances));
			handleItems(node, instances, findKnownItems(instances));
		}
		return node;
	}

	private void handleItems(final CollectionNode collectionNode, final Instances instances, final Iterable<?> items)
	{
		for (final Object item : items)
		{
			final Node child = compareItem(collectionNode, instances, item);
			if (child.hasChanges())
			{
				collectionNode.addChild(child);
				collectionNode.setState(Node.State.CHANGED);
			}
			else if (getConfiguration().isReturnable(child))
			{
				collectionNode.addChild(child);
			}
		}
	}

	private Node compareItem(final CollectionNode collectionNode, final Instances instances, final Object item)
	{
		final Accessor accessor = collectionNode.accessorForItem(item);
		return getDelegate().delegate(collectionNode, instances.access(accessor));
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
