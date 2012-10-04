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
import de.danielbechler.diff.introspect.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/**
 * Used to find differences between objects that were not handled by one of the other (specialized) {@link
 * Differ Differs}.
 *
 * @author Daniel Bechler
 */
final class BeanDiffer extends AbstractDiffer<Node>
{
	private Introspector introspector = new StandardIntrospector();

	BeanDiffer()
	{
		setDelegate(new DelegatingObjectDifferImpl(this, null, null, null));
	}

	BeanDiffer(final DelegatingObjectDiffer delegate)
	{
		super(delegate);
	}

	Node compare(final Object working, final Object base)
	{
		// Root call requires an existing working instance
		Assert.notNull(working, "working");

		// Comparison of different types is not (yet) supported
		Assert.equalTypesOrNull(working, base);

		return compare(Node.ROOT, Instances.of(new RootAccessor(), working, base));
	}

	@Override
	protected Node internalCompare(final Node parentNode, final Instances instances)
	{
		final Node node = newNode(parentNode, instances);
		if (getDelegate().isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (instances.areNull())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			return compareBean(parentNode, instances);
		}
		return node;
	}

	@Override
	protected Node newNode(final Node parentNode, final Instances instances)
	{
		return new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
	}

	private Node compareBean(final Node parentNode, final Instances instances)
	{
		final Node node = newNode(parentNode, instances);
		if (instances.hasBeenAdded())
		{
			ensureNodeState(node, Node.State.ADDED);
			compareBeanUsingAppropriateMethod(node, instances);
			ensureNodeState(node, Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			ensureNodeState(node, Node.State.REMOVED);
			compareBeanUsingAppropriateMethod(node, instances);
			ensureNodeState(node, Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			ensureNodeState(node, Node.State.UNTOUCHED);
		}
		else
		{
			compareBeanUsingAppropriateMethod(node, instances);
		}
		return node;
	}

	private static void ensureNodeState(final Node node, final Node.State state)
	{
		node.setState(state);
	}

	private void compareBeanUsingAppropriateMethod(final Node node, final Instances instances)
	{
		if (getDelegate().isIntrospectible(node))
		{
			compareProperties(node, instances);
		}
		else if (getDelegate().isEqualsOnly(node))
		{
			compareEquality(node, instances);
		}
	}

	@SuppressWarnings({"MethodMayBeStatic"})
	private void compareEquality(final Node node, final Instances instances)
	{
		if (!instances.areEqual())
		{
			ensureNodeState(node, Node.State.CHANGED);
		}
	}

	private void compareProperties(final Node parentNode, final Instances instances)
	{
		final DelegatingObjectDiffer delegate = getDelegate();
		for (final Accessor accessor : introspect(instances.getType()))
		{
			final Instances propertyInstances = instances.access(accessor);
			final Node propertyNode = delegate.delegate(parentNode, propertyInstances);
			if (delegate.isReturnable(propertyNode))
			{
				parentNode.addChild(propertyNode);
			}
		}
	}

	private Iterable<Accessor> introspect(final Class<?> type)
	{
		final Iterable<Accessor> accessorIterable = introspector.introspect(type);
		if (accessorIterable != null)
		{
			return accessorIterable;
		}
		return java.util.Collections.emptyList();
	}

	void setIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "introspector");
		this.introspector = introspector;
	}
}
