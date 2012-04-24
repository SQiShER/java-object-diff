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
