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

import de.danielbechler.diff.bean.*;
import de.danielbechler.diff.collection.*;
import de.danielbechler.diff.map.*;
import de.danielbechler.util.*;

import java.util.*;
import java.util.Collections;

/**
 * @author Daniel Bechler
 */
public final class NodePath implements Comparable<NodePath>
{
	private final List<Element> elements;

	private NodePath(final List<Element> elements)
	{
		this.elements = Collections.unmodifiableList(elements);
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public boolean matches(final NodePath nodePath)
	{
		return nodePath.equals(this);
	}

	public boolean isParentOf(final NodePath nodePath)
	{
		final Iterator<Element> iterator1 = elements.iterator();
		final Iterator<Element> iterator2 = nodePath.getElements().iterator();
		while (iterator1.hasNext() && iterator2.hasNext())
		{
			final Element next1 = iterator1.next();
			final Element next2 = iterator2.next();
			if (!next1.equals(next2))
			{
				return false;
			}
		}
		return !iterator1.hasNext();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<Element> iterator = elements.iterator();
		Element previousElement = null;
		while (iterator.hasNext())
		{
			final Element element = iterator.next();
			if (element instanceof RootElement)
			{
				sb.append("/");
			}
			else if (element instanceof CollectionElement || element instanceof MapElement)
			{
				sb.append(element);
			}
			else if (previousElement instanceof RootElement)
			{
				sb.append(element);
			}
			else
			{
				sb.append('/');
				sb.append(element);
			}
			previousElement = element;
		}
		return sb.toString();
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

		if (!elements.equals(that.elements))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return elements.hashCode();
	}

	public static InitialBuilder createBuilder()
	{
		return new InitialBuilderImpl();
	}

	public static NodePath buildWith(final String propertyName, final String... additionalPropertyNames)
	{
		return createBuilder().withRoot().withPropertyName(propertyName, additionalPropertyNames).build();
	}

	public static NodePath buildRootPath()
	{
		return createBuilder().withRoot().build();
	}

	public int compareTo(final NodePath that)
	{
		if (this.getElements().size() <= that.getElements().size())
		{
			return -1;
		}
		else if (this.matches(that))
		{
			return 0;
		}
		else if (this.getElements().size() > that.getElements().size())
		{
			return 1;
		}
		else
		{
			return 1;
		}
	}

	/**
	 * @author Daniel Bechler
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	private static final class InitialBuilderImpl implements InitialBuilder
	{
		private InitialBuilderImpl()
		{
		}

		public AppendableBuilder withRoot()
		{
			final List<Element> elements = new ArrayList<Element>(1);
			elements.add(RootElement.getInstance());
			return new AppendableBuilderImpl(elements);
		}

		public AppendableBuilder withPropertyPath(final NodePath nodePath)
		{
			Assert.notNull(nodePath, "propertyPath");
			return new AppendableBuilderImpl(new ArrayList<Element>(nodePath.getElements()));
		}
	}

	private static final class AppendableBuilderImpl implements AppendableBuilder
	{
		private final List<Element> elements;

		public AppendableBuilderImpl(final List<Element> elements)
		{
			Assert.notEmpty(elements, "elements");
			this.elements = new ArrayList<Element>(elements);
		}

		public AppendableBuilder withElement(final Element element)
		{
			Assert.notNull(element, "element");
			elements.add(element);
			return this;
		}

		public AppendableBuilder withPropertyName(final String name, final String... names)
		{
			elements.add(new BeanPropertyElement(name));
			for (final String s : names)
			{
				elements.add(new BeanPropertyElement(s));
			}
			return this;
		}

		public <T> AppendableBuilder withCollectionItem(final T item)
		{
			elements.add(new CollectionElement(item));
			return this;
		}

		public <K> AppendableBuilder withMapKey(final K key)
		{
			Assert.notNull(key, "key");
			elements.add(new MapElement(key));
			return this;
		}

		public NodePath build()
		{
			if (elements.isEmpty())
			{
				throw new IllegalStateException("A property path cannot be empty");
			}
			else if (!(elements.get(0) instanceof RootElement))
			{
				throw new IllegalStateException("A property path must start with a root element");
			}
			else if (elementCount(RootElement.class) > 1)
			{
				throw new IllegalStateException("A property path cannot contain multiple root elements");
			}
			return new NodePath(elements);
		}

		private int elementCount(final Class<? extends Element> type)
		{
			assert type != null : "Type must not be null";
			int count = 0;
			for (final Element element : elements)
			{
				if (type.isAssignableFrom(element.getClass()))
				{
					count++;
				}
			}
			return count;
		}
	}

	public static interface InitialBuilder
	{
		AppendableBuilder withPropertyPath(NodePath nodePath);

		AppendableBuilder withRoot();
	}

	public static interface AppendableBuilder
	{
		AppendableBuilder withElement(Element element);

		AppendableBuilder withPropertyName(String name, String... names);

		<T> AppendableBuilder withCollectionItem(T item);

		<K> AppendableBuilder withMapKey(K key);

		NodePath build();
	}
}
