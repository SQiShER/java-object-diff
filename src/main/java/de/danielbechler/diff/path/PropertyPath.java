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

package de.danielbechler.diff.path;

import java.util.*;

/** @author Daniel Bechler */
public final class PropertyPath
{
	/** Enforces a proper implementation of hashCode(), equals() and toString(). */
	public abstract static class Element
	{
		public abstract boolean equals(final Element element);

		public abstract int calculateHashCode();

		public abstract String asString();

		@Override
		public final boolean equals(final Object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (obj instanceof Element)
			{
				return equals((Element) obj);
			}
			return false;
		}

		@Override
		public final int hashCode()
		{
			return calculateHashCode();
		}

		@Override
		public final String toString()
		{
			return asString();
		}
	}

	public static PropertyPath with(final String... propertyNames)
	{
		final PropertyPathBuilder builder = new PropertyPathBuilder();
		builder.withRoot();
		for (final String propertyName : propertyNames)
		{
			builder.withPropertyName(propertyName);
		}
		return builder.build();
	}

	public static PropertyPath with(final Element... elements)
	{
		final PropertyPathBuilder builder = new PropertyPathBuilder();
		builder.withRoot();
		for (final Element element : elements)
		{
			builder.withElement(element);
		}
		return builder.build();
	}

	private final List<Element> elements;

	public PropertyPath(final Element... selectors)
	{
		this(Arrays.asList(selectors));
	}

	public PropertyPath(final Collection<Element> selectors)
	{
		elements = new ArrayList<Element>(selectors);
	}

	public PropertyPath(final PropertyPath parentSelectorPath, final Element selector)
	{
		elements = new ArrayList<Element>(parentSelectorPath.getElements().size() + 1);
		elements.addAll(parentSelectorPath.elements);
		elements.add(selector);
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public boolean matches(final PropertyPath path)
	{
		return path.equals(this);
	}

	public boolean isParentOf(final PropertyPath selectorPath)
	{
		final Iterator<Element> iterator1 = elements.iterator();
		final Iterator<Element> iterator2 = selectorPath.getElements().iterator();
		while (iterator1.hasNext() && iterator2.hasNext())
		{
			final Element next1 = iterator1.next();
			final Element next2 = iterator2.next();
			if (!next1.equals(next2))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext())
		{
			final Element selector = iterator.next();
			if (selector instanceof RootElement)
			{
				sb.append('/');
			}
			else
			{
				sb.append(selector);
				if (iterator.hasNext())
				{
					sb.append('/');
				}
			}
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

		final PropertyPath that = (PropertyPath) o;

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
}
