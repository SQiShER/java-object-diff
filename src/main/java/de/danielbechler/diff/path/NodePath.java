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

import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.diff.selector.CollectionItemElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.diff.selector.MapKeyElementSelector;
import de.danielbechler.diff.selector.RootElementSelector;
import de.danielbechler.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Bechler
 */
public final class NodePath implements Comparable<NodePath>
{
	private final List<ElementSelector> elementSelectors;

	private NodePath(final List<ElementSelector> elementSelectors)
	{
		this.elementSelectors = Collections.unmodifiableList(elementSelectors);
	}

	public boolean isParentOf(final NodePath nodePath)
	{
		final List<ElementSelector> otherElementSelectors = nodePath.getElementSelectors();
		if (elementSelectors.size() < otherElementSelectors.size())
		{
			return otherElementSelectors.subList(0, elementSelectors.size()).equals(elementSelectors);
		}
		return false;
	}

	public List<ElementSelector> getElementSelectors()
	{
		return elementSelectors;
	}

	public boolean isChildOf(final NodePath nodePath)
	{
		final List<ElementSelector> otherElementSelectors = nodePath.getElementSelectors();
		if (elementSelectors.size() > otherElementSelectors.size())
		{
			return elementSelectors.subList(0, otherElementSelectors.size()).equals(otherElementSelectors);
		}
		return false;
	}

	public ElementSelector getLastElementSelector()
	{
		return elementSelectors.get(elementSelectors.size() - 1);
	}

	@Override
	public int hashCode()
	{
		return elementSelectors.hashCode();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final NodePath that = (NodePath) o;

		if (!elementSelectors.equals(that.elementSelectors))
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<ElementSelector> iterator = elementSelectors.iterator();
		ElementSelector previousElementSelector = null;
		while (iterator.hasNext())
		{
			final ElementSelector elementSelector = iterator.next();
			if (elementSelector instanceof RootElementSelector)
			{
				sb.append("/");
			}
			else if (elementSelector instanceof CollectionItemElementSelector || elementSelector instanceof MapKeyElementSelector)
			{
				sb.append(elementSelector);
			}
			else if (previousElementSelector instanceof RootElementSelector)
			{
				sb.append(elementSelector);
			}
			else
			{
				sb.append('/');
				sb.append(elementSelector);
			}
			previousElementSelector = elementSelector;
		}
		return sb.toString();
	}

	public int compareTo(final NodePath that)
	{
		final int distance = getElementSelectors().size() - that.getElementSelectors().size();
		if (distance == 0)
		{
			return matches(that) ? 0 : 1;
		}
		else if (distance > 0)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

	public boolean matches(final NodePath nodePath)
	{
		return nodePath.equals(this);
	}

	public static AppendableBuilder startBuildingFrom(final NodePath nodePath)
	{
		Assert.notNull(nodePath, "propertyPath");
		return new AppendableBuilderImpl(new ArrayList<ElementSelector>(nodePath.getElementSelectors()));
	}

	public static NodePath with(final String propertyName, final String... additionalPropertyNames)
	{
		return startBuilding().propertyName(propertyName, additionalPropertyNames).build();
	}

	public static AppendableBuilder startBuilding()
	{
		final List<ElementSelector> elementSelectors1 = new LinkedList<ElementSelector>();
		elementSelectors1.add(RootElementSelector.getInstance());
		return new AppendableBuilderImpl(elementSelectors1);
	}

	public static NodePath withRoot()
	{
		return startBuilding().build();
	}

	public static interface AppendableBuilder
	{
		AppendableBuilder element(ElementSelector elementSelector);

		AppendableBuilder propertyName(String name, String... names);

		<T> AppendableBuilder collectionItem(T item);

		<K> AppendableBuilder mapKey(K key);

		NodePath build();
	}

	private static final class AppendableBuilderImpl implements AppendableBuilder
	{
		private final List<ElementSelector> elementSelectors;

		public AppendableBuilderImpl(final List<ElementSelector> elementSelectors)
		{
			Assert.notEmpty(elementSelectors, "elementSelectors");
			this.elementSelectors = new LinkedList<ElementSelector>(elementSelectors);
		}

		public AppendableBuilder element(final ElementSelector elementSelector)
		{
			Assert.notNull(elementSelector, "elementSelector");
			elementSelectors.add(elementSelector);
			return this;
		}

		public AppendableBuilder propertyName(final String name, final String... names)
		{
			elementSelectors.add(new BeanPropertyElementSelector(name));
			for (final String s : names)
			{
				elementSelectors.add(new BeanPropertyElementSelector(s));
			}
			return this;
		}

		public <T> AppendableBuilder collectionItem(final T item)
		{
			elementSelectors.add(new CollectionItemElementSelector(item));
			return this;
		}

		public <K> AppendableBuilder mapKey(final K key)
		{
			Assert.notNull(key, "key");
			elementSelectors.add(new MapKeyElementSelector(key));
			return this;
		}

		public NodePath build()
		{
			if (elementSelectors.isEmpty())
			{
				throw new IllegalStateException("A property path cannot be empty");
			}
			else if (!(elementSelectors.get(0) instanceof RootElementSelector))
			{
				throw new IllegalStateException("A property path must start with a root element");
			}
			else if (elementCount(RootElementSelector.class) > 1)
			{
				throw new IllegalStateException("A property path cannot contain multiple root elements");
			}
			return new NodePath(elementSelectors);
		}

		private int elementCount(final Class<? extends ElementSelector> type)
		{
			assert type != null : "Type must not be null";
			int count = 0;
			for (final ElementSelector elementSelector : elementSelectors)
			{
				if (type.isAssignableFrom(elementSelector.getClass()))
				{
					count++;
				}
			}
			return count;
		}
	}
}
