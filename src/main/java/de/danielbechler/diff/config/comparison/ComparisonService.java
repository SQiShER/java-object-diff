/*
 * Copyright 2013 Daniel Bechler
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

package de.danielbechler.diff.config.comparison;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.path.NodePath;
import de.danielbechler.diff.node.path.NodePathValueHolder;
import de.danielbechler.util.Classes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ComparisonService implements ComparisonConfiguration, ComparisonStrategyResolver, PrimitiveDefaultValueModeResolver
{
	private final NodePathValueHolder<ComparisonStrategy> nodePathComparisonStrategies = NodePathValueHolder.of(ComparisonStrategy.class);
	private final Map<Class<?>, ComparisonStrategy> typeComparisonStrategyMap = new HashMap<Class<?>, ComparisonStrategy>();

	private PrimitiveDefaultValueMode primitiveDefaultValueMode = PrimitiveDefaultValueMode.UNASSIGNED;

	public ComparisonStrategy resolveComparisonStrategy(final DiffNode node)
	{
		final ComparisonStrategy comparisonStrategy = nodePathComparisonStrategies.valueForNodePath(node.getPath());
		if (comparisonStrategy != null)
		{
			return comparisonStrategy;
		}

		if (typeComparisonStrategyMap.containsKey(node.getValueType()))
		{
			return typeComparisonStrategyMap.get(node.getValueType());
		}

		if (Classes.isComparableType(node.getValueType()))
		{
			return new ComparableComparisonStrategy();
		}

		if (Classes.isSimpleType(node.getValueType()))
		{
			return new EqualsOnlyComparisonStrategy();
		}

		if (node.getComparisonStrategy() != null)
		{
			return node.getComparisonStrategy();
		}

		return null;
	}

	public PrimitiveDefaultValueMode resolvePrimitiveDefaultValueMode(final DiffNode node)
	{
		return primitiveDefaultValueMode;
	}

	public Of ofNode(final NodePath nodePath)
	{
		return new OfNodePath(nodePath);
	}

	public Of ofType(final Class<?> type)
	{
		return new OfType(type);
	}

	public OfPrimitiveTypes ofPrimitiveTypes()
	{
		return new OfPrimitiveTypesImpl();
	}

	private abstract static class AbstractOf implements Of
	{
		public ComparisonConfiguration toUseEqualsMethod()
		{
			return toUse(new EqualsOnlyComparisonStrategy());
		}

		public ComparisonConfiguration toUseEqualsMethodOfValueProvidedByMethod(final String propertyName)
		{
			return toUse(new EqualsOnlyComparisonStrategy(propertyName));
		}

		public ComparisonConfiguration toUseCompareToMethod()
		{
			return toUse(new ComparableComparisonStrategy());
		}
	}

	private class OfType extends AbstractOf
	{
		private final Class<?> type;

		public OfType(final Class<?> type)
		{
			this.type = type;
		}

		public ComparisonConfiguration toUse(final ComparisonStrategy comparisonStrategy)
		{
			typeComparisonStrategyMap.put(type, comparisonStrategy);
			return ComparisonService.this;
		}
	}

	private class OfNodePath extends AbstractOf
	{
		private final NodePath nodePath;

		public OfNodePath(final NodePath nodePath)
		{
			this.nodePath = nodePath;
		}

		public ComparisonConfiguration toUse(final ComparisonStrategy comparisonStrategy)
		{
			nodePathComparisonStrategies.put(nodePath, comparisonStrategy);
			return ComparisonService.this;
		}
	}

	private class OfPrimitiveTypesImpl implements OfPrimitiveTypes
	{
		public ComparisonConfiguration toTreatDefaultValuesAs(final PrimitiveDefaultValueMode mode)
		{
			primitiveDefaultValueMode = mode;
			return null;
		}
	}
}
