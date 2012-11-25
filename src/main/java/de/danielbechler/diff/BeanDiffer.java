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
	private final Configuration configuration;
	private Introspector introspector = new StandardIntrospector();
	private BeanPropertyComparisonDelegator beanPropertyComparer;
	private DefaultNodeFactory defaultNodeFactory = new DefaultNodeFactory();

	public BeanDiffer(final DifferDelegator delegator, final Configuration configuration)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(configuration, "configuration");
		this.beanPropertyComparer = new BeanPropertyComparisonDelegator(delegator, configuration);
		this.configuration = configuration;
	}

	@Override
	public final Node compare(final Node parentNode, final Instances instances)
	{
		final Node beanNode = defaultNodeFactory.createNode(parentNode, instances);
		if (configuration.isIgnored(beanNode))
		{
			beanNode.setState(Node.State.IGNORED);
			return beanNode;
		}
		else if (instances.areNull())
		{
			beanNode.setState(Node.State.UNTOUCHED);
			return beanNode;
		}
		return compareBean(parentNode, instances);
	}

	private Node compareBean(final Node parentNode, final Instances instances)
	{
		final Node beanNode = defaultNodeFactory.createNode(parentNode, instances);
		if (instances.hasBeenAdded())
		{
			beanNode.setState(Node.State.ADDED);
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			beanNode.setState(Node.State.REMOVED);
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(Node.State.REMOVED);
		}
		else if (instances.areSame())
		{
			beanNode.setState(Node.State.UNTOUCHED);
		}
		else
		{
			compareUsingAppropriateMethod(beanNode, instances);
		}
		return beanNode;
	}

	private void compareUsingAppropriateMethod(final Node beanNode, final Instances instances)
	{
		if (configuration.isIntrospectible(beanNode))
		{
			compareUsingIntrospection(beanNode, instances);
		}
		else if (configuration.isEqualsOnly(beanNode))
		{
			compareUsingEquals(beanNode, instances);
		}
	}

	@SuppressWarnings({"MethodMayBeStatic"})
	private void compareUsingEquals(final Node beanNode, final Instances instances)
	{
		if (instances.areEqual())
		{
			beanNode.setState(Node.State.UNTOUCHED);
		}
		else
		{
			beanNode.setState(Node.State.CHANGED);
		}
	}

	private void compareUsingIntrospection(final Node beanNode, final Instances beanInstances)
	{
		final Class<?> beanType = beanInstances.getType();
		final Iterable<Accessor> propertyAccessors = introspector.introspect(beanType);
		for (final Accessor propertyAccessor : propertyAccessors)
		{
			final Node propertyNode = beanPropertyComparer.compare(beanNode, beanInstances, propertyAccessor);
			if (configuration.isReturnable(propertyNode))
			{
				beanNode.addChild(propertyNode);
			}
		}
	}

	@TestOnly
	void setIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "introspector");
		this.introspector = introspector;
	}

	@TestOnly
	void setBeanPropertyComparer(final BeanPropertyComparisonDelegator beanPropertyComparer)
	{
		Assert.notNull(beanPropertyComparer, "beanPropertyComparer");
		this.beanPropertyComparer = beanPropertyComparer;
	}

	@TestOnly
	public void setDefaultNodeFactory(final DefaultNodeFactory defaultNodeFactory)
	{
		Assert.notNull(defaultNodeFactory, "defaultNodeFactory");
		this.defaultNodeFactory = defaultNodeFactory;
	}
}
