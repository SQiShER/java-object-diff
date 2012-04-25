/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
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
		final Node node = new DefaultNode(parentNode, instances.getSourceAccessor());
		if (getDelegate().isIgnored(node, instances))
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
		final Node difference = new DefaultNode(parentNode, instances.getSourceAccessor());
		if (instances.hasBeenAdded())
		{
			difference.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			difference.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			difference.setState(Node.State.UNTOUCHED);
		}
		else
		{
			if (getDelegate().isEqualsOnly(parentNode, instances))
			{
				compareWithEquals(difference, instances);
			}
			else
			{
				compareProperties(difference, instances);
			}
		}
		return difference;
	}

	private static void compareWithEquals(final Node parentNode, final Instances instances)
	{
		if (!instances.areEqual())
		{
			parentNode.setState(Node.State.CHANGED);
		}
	}

	private void compareProperties(final Node parentNode, final Instances instances)
	{
		for (final Accessor accessor : introspect(instances.getType()))
		{
			final Node child = getDelegate().delegate(parentNode, instances.access(accessor));
			if (child.hasChanges())
			{
				parentNode.setState(Node.State.CHANGED);
				parentNode.addChild(child);
			}
			else if (getConfiguration().isReturnUnchangedNodes())
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
