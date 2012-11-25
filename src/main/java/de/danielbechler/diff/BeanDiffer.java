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
final class BeanDiffer implements Differ<Node>
{
	private Introspector introspector = new StandardIntrospector();
	private final DifferDelegator delegator;
	private final Configuration configuration;

	public BeanDiffer(final DifferDelegator delegator, final Configuration configuration)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(configuration, "configuration");
		this.delegator = delegator;
		this.configuration = configuration;
	}

	@Override
	public final Node compare(final Node parentNode, final Instances instances)
	{
		Node node = newNode(parentNode, instances);
		if (configuration.isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (instances.areNull())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			node = compareBean(parentNode, instances);
		}
		return node;
	}

	private static Node newNode(final Node parentNode, final Instances instances)
	{
		return new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
	}

	private Node compareBean(final Node parentNode, final Instances instances)
	{
		final Node node = newNode(parentNode, instances);
		if (instances.hasBeenAdded())
		{
			node.setState(Node.State.ADDED);
			compareWithAppropriateMethod(node, instances);
			node.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			node.setState(Node.State.REMOVED);
			compareWithAppropriateMethod(node, instances);
			node.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			node.setState(Node.State.UNTOUCHED);
		}
		else
		{
			compareWithAppropriateMethod(node, instances);
		}
		return node;
	}

	private void compareWithAppropriateMethod(final Node node, final Instances instances)
	{
		if (configuration.isIntrospectible(node))
		{
			compareProperties(node, instances);
		}
		else if (configuration.isEqualsOnly(node))
		{
			compareEquality(node, instances);
		}
	}

	@SuppressWarnings({"MethodMayBeStatic"})
	private void compareEquality(final Node node, final Instances instances)
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

	private void compareProperties(final Node parentNode, final Instances instances)
	{
		for (final Accessor accessor : introspector.introspect(instances.getType()))
		{
			Node propertyNode = new DefaultNode(parentNode, accessor, null);
			if (configuration.isIgnored(propertyNode))
			{
				// this check is here to prevent the invocation of the accessor of ignored properties
				propertyNode.setState(Node.State.IGNORED);
			}
			else
			{
				propertyNode = delegator.delegate(parentNode, instances.access(accessor));
			}
			if (configuration.isReturnable(propertyNode))
			{
				parentNode.addChild(propertyNode);
			}
		}
	}

	void setIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "introspector");
		this.introspector = introspector;
	}
}
