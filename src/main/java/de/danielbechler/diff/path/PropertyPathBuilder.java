/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff.path;

import java.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"UnusedDeclaration"})
public final class PropertyPathBuilder
{
	private final List<PropertyPath.Element> elements = new LinkedList<PropertyPath.Element>();

	public PropertyPathBuilder withPropertyPath(final PropertyPath propertyPath)
	{
		if (propertyPath != null)
		{
			elements.addAll(propertyPath.getElements());
		}
		return this;
	}

	public PropertyPathBuilder withElement(final PropertyPath.Element element)
	{
		elements.add(element);
		return this;
	}

	public PropertyPathBuilder withPropertyName(final String... names)
	{
		for (final String name : names)
		{
			elements.add(new NamedPropertyElement(name));
		}
		return this;
	}

	public <T> PropertyPathBuilder withCollectionItem(final T item)
	{
		elements.add(new CollectionElement(item));
		return this;
	}

	public <K> PropertyPathBuilder withMapKey(final K key)
	{
		elements.add(new MapElement(key));
		return this;
	}

	public PropertyPathBuilder withRoot()
	{
		elements.add(0, RootElement.getInstance());
		return this;
	}

	public PropertyPath build()
	{
		return new PropertyPath(elements);
	}
}
