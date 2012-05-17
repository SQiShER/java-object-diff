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
 * Used to find differences between objects that were not handled by one of the other (specialized) {@link Differ Differs}.
 *
 * @author Daniel Bechler
 */
final class BeanDiffer extends AbstractDiffer
{
	private Introspector introspector = new StandardIntrospector();

	BeanDiffer()
	{
		setDelegate(new DelegatingObjectDifferImpl(this, null, null));
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

	public Node compare(final Node parentNode, final Instances instances)
	{
		final Node node = new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (getDelegate().isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (instances.getType() == null)
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			return compareBean(parentNode, instances);
		}
		return node;
	}

	private Node compareBean(final Node parentNode, final Instances instances)
	{
		final Node node = new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (instances.hasBeenAdded())
		{
			node.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			node.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			if (getDelegate().isEqualsOnly(node))
			{
				compareWithEquals(node, instances);
			}
			else
			{
				compareProperties(node, instances);
			}
		}
		return node;
	}

	private static void compareWithEquals(final Node node, final Instances instances)
	{
		if (!instances.areEqual())
		{
			node.setState(Node.State.CHANGED);
		}
	}

	private void compareProperties(final Node parentNode, final Instances instances)
	{
		for (final Accessor accessor : introspect(instances.getType()))
		{
			final Node node = new DefaultNode(parentNode, accessor, instances.getType());
			if (getDelegate().isIgnored(node))
			{
				if (getDelegate().isReturnable(node))
				{
					node.setState(Node.State.IGNORED);
					parentNode.addChild(node);
				}
				continue;
			}
			final Node child = getDelegate().delegate(parentNode, instances.access(accessor));
			if (child.hasChanges())
			{
				parentNode.setState(Node.State.CHANGED);
				parentNode.addChild(child);
			}
			else if (getConfiguration().isReturnable(child))
			{
				parentNode.addChild(child);
			}
		}
	}

	private Iterable<Accessor> introspect(final Class<?> type)
	{
		return introspector.introspect(type);
	}

	void setIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "introspector");
		this.introspector = introspector;
	}
}
