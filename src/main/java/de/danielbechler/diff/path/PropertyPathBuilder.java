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
@SuppressWarnings({"UnusedDeclaration"})
public final class PropertyPathBuilder
{
	private final List<Element> elements = new LinkedList<Element>();

	public PropertyPathBuilder withPropertyPath(final PropertyPath propertyPath)
	{
		if (propertyPath != null)
		{
			elements.addAll(propertyPath.getElements());
		}
		return this;
	}

	public PropertyPathBuilder withElement(final Element element)
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
