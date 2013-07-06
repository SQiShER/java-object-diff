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
	private final NodeInspector nodeInspector;
	private Introspector introspector = new StandardIntrospector();
	private BeanPropertyComparisonDelegator beanPropertyComparisonDelegator;
	private DefaultNodeFactory defaultNodeFactory = new DefaultNodeFactory();

	public BeanDiffer(final DifferDelegator delegator, final NodeInspector nodeInspector)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(nodeInspector, "configuration");
		this.beanPropertyComparisonDelegator = new BeanPropertyComparisonDelegator(delegator, nodeInspector);
		this.nodeInspector = nodeInspector;
	}

	public final Node compare(final Node parentNode, final Instances instances)
	{
		final Node beanNode = defaultNodeFactory.createNode(parentNode, instances);
		if (nodeInspector.isIgnored(beanNode))
		{
			beanNode.setState(Node.State.IGNORED);
		}
		else if (instances.areNull() || instances.areSame())
		{
			beanNode.setState(Node.State.UNTOUCHED);
		}
		else if (instances.hasBeenAdded())
		{
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(Node.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(Node.State.REMOVED);
		}
		else
		{
			compareUsingAppropriateMethod(beanNode, instances);
		}
		return beanNode;
	}

	private void compareUsingAppropriateMethod(final Node beanNode, final Instances instances)
	{
		if (nodeInspector.isCompareToOnly(beanNode))
		{
			compareUsingCompareTo(beanNode, instances);
		}
		else if (nodeInspector.isEqualsOnly(beanNode))
		{
			compareUsingEquals(beanNode, instances);
		}
        else if (nodeInspector.isIntrospectible(beanNode))
        {
            compareUsingIntrospection(beanNode, instances);
        }
	}

	private void compareUsingIntrospection(final Node beanNode, final Instances beanInstances)
	{
		final Class<?> beanType = beanInstances.getType();
		final Iterable<Accessor> propertyAccessors = introspector.introspect(beanType);
		for (final Accessor propertyAccessor : propertyAccessors)
		{
			final Node propertyNode = beanPropertyComparisonDelegator.compare(beanNode, beanInstances, propertyAccessor);
			if (nodeInspector.isReturnable(propertyNode))
			{
				beanNode.addChild(propertyNode);
			}
		}
	}

    @SuppressWarnings({"MethodMayBeStatic"})
    private void compareUsingCompareTo(final Node beanNode, final Instances instances)
    {
        if (instances.areEqualByComparison())
        {
            beanNode.setState(Node.State.UNTOUCHED);
        }
        else
        {
            beanNode.setState(Node.State.CHANGED);
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

	@TestOnly
	void setIntrospector(final Introspector introspector)
	{
		Assert.notNull(introspector, "introspector");
		this.introspector = introspector;
	}

	@TestOnly
	void setBeanPropertyComparisonDelegator(final BeanPropertyComparisonDelegator beanPropertyComparisonDelegator)
	{
		Assert.notNull(beanPropertyComparisonDelegator, "beanPropertyComparisonDelegator");
		this.beanPropertyComparisonDelegator = beanPropertyComparisonDelegator;
	}

	@TestOnly
	public void setDefaultNodeFactory(final DefaultNodeFactory defaultNodeFactory)
	{
		Assert.notNull(defaultNodeFactory, "defaultNodeFactory");
		this.defaultNodeFactory = defaultNodeFactory;
	}
}
