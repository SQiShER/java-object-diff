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

package de.danielbechler.diff.path;

import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.diff.selector.RootElementSelector;
import de.danielbechler.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Bechler
 * @see de.danielbechler.diff.inclusion.ValueNode
 * @deprecated The ConfigNode provides a much more powerful way to store values for NodePaths.
 */
@Deprecated
public class NodePathValueHolder<T>
{
	private final Map<ElementSelector, NodePathValueHolder<T>> elementValueHolders = new HashMap<ElementSelector, NodePathValueHolder<T>>();
	private T value;

	public static <T> NodePathValueHolder<T> of(final Class<T> type)
	{
		Assert.notNull(type, "type");
		return new NodePathValueHolder<T>();
	}

	public NodePathValueHolder<T> put(final NodePath nodePath, final T value)
	{
		put(nodePath.getElementSelectors(), value);
		return this;
	}

	private void put(final List<ElementSelector> elementSelectors, final T value)
	{
		if (elementSelectors.isEmpty())
		{
			return;
		}
		final ElementSelector elementSelector = elementSelectors.get(0);
		NodePathValueHolder<T> nodePathValueHolder = valueHolderForElementSelector(elementSelector);
		if (nodePathValueHolder == null)
		{
			nodePathValueHolder = new NodePathValueHolder<T>();
			elementValueHolders.put(elementSelector, nodePathValueHolder);
		}
		if (elementSelectors.size() == 1)
		{
			nodePathValueHolder.value = value;
		}
		else
		{
			final List<ElementSelector> nodePathElementsTail = new ArrayList<ElementSelector>(elementSelectors.size() - 1);
			nodePathElementsTail.addAll(elementSelectors.subList(1, elementSelectors.size()));
			nodePathValueHolder.put(nodePathElementsTail, value);
		}
	}

	private T visit(final List<T> accumulator, final Iterator<ElementSelector> elementIterator)
	{
		if (value != null)
		{
			accumulator.add(value);
		}
		if (elementIterator.hasNext())
		{
			final ElementSelector selector = elementIterator.next();
			final NodePathValueHolder<T> valueHolder = valueHolderForElementSelector(selector);
			if (valueHolder != null)
			{
				return valueHolder.visit(accumulator, elementIterator);
			}
			return null;
		}
		return value;
	}

	private NodePathValueHolder<T> valueHolderForElementSelector(final ElementSelector elementSelector)
	{
		return elementValueHolders.get(elementSelector);
	}

	public T valueForNodePath(final NodePath nodePath)
	{
		return visit(new LinkedList<T>(), nodePath.getElementSelectors().iterator());
	}

	public List<T> accumulatedValuesForNodePath(final NodePath nodePath)
	{
		final List<T> accumulator = new LinkedList<T>();
		visit(accumulator, nodePath.getElementSelectors().iterator());
		return accumulator;
	}

	public boolean containsValue(final T value)
	{
		if (value == null && this.value == null)
		{
			return true;
		}
		else if (value != null && value.equals(this.value))
		{
			return true;
		}
		else
		{
			for (final NodePathValueHolder<T> valueHolder : elementValueHolders.values())
			{
				if (valueHolder.containsValue(value))
				{
					return true;
				}
			}
			return false;
		}
	}

	public void collect(final Collector<T> collector)
	{
		collect(null, collector);
	}

	private void collect(final NodePath nodePath, final Collector<T> collector)
	{
		if (nodePath != null && value != null)
		{
			collector.it(nodePath, value);
		}
		for (final Map.Entry<ElementSelector, NodePathValueHolder<T>> entry : elementValueHolders.entrySet())
		{
			final NodePath childNodePath;
			final ElementSelector elementSelector = entry.getKey();
			final NodePathValueHolder<T> valueHolder = entry.getValue();
			if (elementSelector == RootElementSelector.getInstance())
			{
				childNodePath = NodePath.withRoot();
			}
			else
			{
				childNodePath = NodePath.startBuildingFrom(nodePath).element(elementSelector).build();
			}

			if (valueHolder != null)
			{
				valueHolder.collect(childNodePath, collector);
			}
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder stringBuilder = new StringBuilder();
		collect(new Collector<T>()
		{
			public void it(final NodePath path, final T value)
			{
				stringBuilder.append(path.toString()).append(" => ").append(value).append('\n');
			}
		});
		return stringBuilder.toString();
	}

	public void hasChildMatchingValue(NodePath nodePath, T inclusion)
	{

	}

	public static interface Collector<T>
	{
		void it(NodePath path, T value);
	}
}
