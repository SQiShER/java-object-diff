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

package de.danielbechler.diff.comparison;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.introspection.ObjectDiffEqualsOnlyType;
import de.danielbechler.diff.introspection.ObjectDiffProperty;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.path.NodePathValueHolder;
import de.danielbechler.util.Classes;

import java.util.HashMap;
import java.util.Map;

public class ComparisonService implements ComparisonConfigurer, ComparisonStrategyResolver, PrimitiveDefaultValueModeResolver
{
	private static final ComparisonStrategy COMPARABLE_COMPARISON_STRATEGY = new ComparableComparisonStrategy();
	private static final ComparisonStrategy EQUALS_ONLY_COMPARISON_STRATEGY = new EqualsOnlyComparisonStrategy();

	private final NodePathValueHolder<ComparisonStrategy> nodePathComparisonStrategies = NodePathValueHolder.of(ComparisonStrategy.class);
	private final Map<Class<?>, ComparisonStrategy> typeComparisonStrategyMap = new HashMap<Class<?>, ComparisonStrategy>();
	private final ObjectDifferBuilder objectDifferBuilder;

	private PrimitiveDefaultValueMode primitiveDefaultValueMode = PrimitiveDefaultValueMode.UNASSIGNED;

	public ComparisonService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
	}

	public ComparisonStrategy resolveComparisonStrategy(final DiffNode node)
	{
		final ComparisonStrategy comparisonStrategy = nodePathComparisonStrategies.valueForNodePath(node.getPath());
		if (comparisonStrategy != null)
		{
			return comparisonStrategy;
		}

		final Class<?> valueType = node.getValueType();
		if (typeComparisonStrategyMap.containsKey(valueType))
		{
			return typeComparisonStrategyMap.get(valueType);
		}

		if (Classes.isSimpleType(valueType))
		{
			// if the simple type implements comparable we use that, since its contract
			// dictates that compareTo == zero carries the same semantics as equals
			if (Classes.isComparableType(valueType))
			{
				return COMPARABLE_COMPARISON_STRATEGY;
			}
			else
			{
				return EQUALS_ONLY_COMPARISON_STRATEGY;
			}
		}

		final ObjectDiffPropertyComparisonStrategyResolver comparisonStrategyResolver = ObjectDiffPropertyComparisonStrategyResolver.instance;

		final ObjectDiffProperty objectDiffProperty = node.getPropertyAnnotation(ObjectDiffProperty.class);
		final ComparisonStrategy comparisonStrategyFromObjectDiffPropertyAnnotation = comparisonStrategyResolver.comparisonStrategyForAnnotation(objectDiffProperty);
		if (comparisonStrategyFromObjectDiffPropertyAnnotation != null)
		{
			return comparisonStrategyFromObjectDiffPropertyAnnotation;
		}

		if (valueType != null)
		{
			final ObjectDiffEqualsOnlyType objectDiffEqualsOnlyType = valueType.getAnnotation(ObjectDiffEqualsOnlyType.class);
			final ComparisonStrategy comparisonStrategyFromObjectDiffEqualsOnlyTypeAnnotation = comparisonStrategyResolver.comparisonStrategyForAnnotation(objectDiffEqualsOnlyType);
			if (comparisonStrategyFromObjectDiffEqualsOnlyTypeAnnotation != null)
			{
				return comparisonStrategyFromObjectDiffEqualsOnlyTypeAnnotation;
			}
		}

		if (valueType == Object.class)
		{
			return EQUALS_ONLY_COMPARISON_STRATEGY;
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

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}

	private abstract static class AbstractOf implements Of
	{
		public ComparisonConfigurer toUseEqualsMethod()
		{
			return toUse(new EqualsOnlyComparisonStrategy());
		}

		public ComparisonConfigurer toUseEqualsMethodOfValueProvidedByMethod(final String propertyName)
		{
			return toUse(new EqualsOnlyComparisonStrategy(propertyName));
		}

		public ComparisonConfigurer toUseCompareToMethod()
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

		public ComparisonConfigurer toUse(final ComparisonStrategy comparisonStrategy)
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

		public ComparisonConfigurer toUse(final ComparisonStrategy comparisonStrategy)
		{
			nodePathComparisonStrategies.put(nodePath, comparisonStrategy);
			return ComparisonService.this;
		}
	}

	private class OfPrimitiveTypesImpl implements OfPrimitiveTypes
	{
		public ComparisonConfigurer toTreatDefaultValuesAs(final PrimitiveDefaultValueMode mode)
		{
			primitiveDefaultValueMode = mode;
			return ComparisonService.this;
		}
	}
}
