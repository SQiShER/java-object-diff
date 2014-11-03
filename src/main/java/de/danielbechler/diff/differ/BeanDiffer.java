/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.filtering.IsReturnableResolver;
import de.danielbechler.diff.introspection.IsIntrospectableResolver;
import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.diff.introspection.TypeInfoResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;

/**
 * Used to find differences between objects that were not handled by one of the other (specialized) {@link
 * Differ Differs}.
 *
 * @author Daniel Bechler
 */
public final class BeanDiffer implements Differ
{
	private final IsIntrospectableResolver isIntrospectableResolver;
	private final IsReturnableResolver isReturnableResolver;
	private final ComparisonStrategyResolver comparisonStrategyResolver;
	private final DifferDispatcher differDispatcher;
	private final TypeInfoResolver typeInfoResolver;

	public BeanDiffer(final DifferDispatcher differDispatcher,
					  final IsIntrospectableResolver introspectableResolver,
					  final IsReturnableResolver returnableResolver,
					  final ComparisonStrategyResolver comparisonStrategyResolver,
					  final TypeInfoResolver typeInfoResolver)
	{
		Assert.notNull(differDispatcher, "differDispatcher");
		this.differDispatcher = differDispatcher;

		Assert.notNull(introspectableResolver, "introspectableResolver");
		this.isIntrospectableResolver = introspectableResolver;

		Assert.notNull(returnableResolver, "returnableResolver");
		this.isReturnableResolver = returnableResolver;

		Assert.notNull(comparisonStrategyResolver, "comparisonStrategyResolver");
		this.comparisonStrategyResolver = comparisonStrategyResolver;

		Assert.notNull(typeInfoResolver, "typeInfoResolver");
		this.typeInfoResolver = typeInfoResolver;
	}

	public boolean accepts(final Class<?> type)
	{
		return !type.isPrimitive() && !type.isArray();
	}

	public final DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		final DiffNode beanNode = new DiffNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (instances.areNull() || instances.areSame())
		{
			beanNode.setState(DiffNode.State.UNTOUCHED);
		}
		else if (instances.hasBeenAdded())
		{
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(DiffNode.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			compareUsingAppropriateMethod(beanNode, instances);
			beanNode.setState(DiffNode.State.REMOVED);
		}
		else
		{
			compareUsingAppropriateMethod(beanNode, instances);
		}
		return beanNode;
	}

	private void compareUsingAppropriateMethod(final DiffNode beanNode, final Instances instances)
	{
		final ComparisonStrategy comparisonStrategy = comparisonStrategyResolver.resolveComparisonStrategy(beanNode);
		if (comparisonStrategy != null)
		{
			comparisonStrategy.compare(beanNode, instances.getType(), instances.getWorking(), instances.getBase());
		}
		else if (isIntrospectableResolver.isIntrospectable(beanNode))
		{
			compareUsingIntrospection(beanNode, instances);
		}
	}

	private void compareUsingIntrospection(final DiffNode beanNode, final Instances beanInstances)
	{
		final TypeInfo typeInfo = typeInfoResolver.typeInfoForNode(beanNode);
		beanNode.setValueTypeInfo(typeInfo);
		for (final PropertyAwareAccessor propertyAccessor : typeInfo.getAccessors())
		{
			final DiffNode propertyNode = differDispatcher.dispatch(beanNode, beanInstances, propertyAccessor);
			if (isReturnableResolver.isReturnable(propertyNode))
			{
				beanNode.addChild(propertyNode);
			}
		}
	}
}
