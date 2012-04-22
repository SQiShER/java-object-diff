package de.danielbechler.diff.path;

import java.util.*;
import java.util.regex.*;

/** @author Daniel Bechler */
public class PropertyPathBuilder
{
	private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

	private final List<PropertyPath.Element> elements = new ArrayList<PropertyPath.Element>(10);

	public PropertyPathBuilder propertyName(final String... names)
	{
		for (final String name : names)
		{
			elements.add(new NamedPropertyElement(name));
		}
		return this;
	}

	public <T> PropertyPathBuilder collectionItem(final T item)
	{
		elements.add(new CollectionElement<T>(item));
		return this;
	}

	public <K> PropertyPathBuilder mapKey(final K key)
	{
		elements.add(new MapElement<K>(key));
		return this;
	}

	public PropertyPathBuilder root()
	{
		elements.add(RootElement.getInstance());
		return this;
	}

	public PropertyPath toPropertyPath()
	{
		return new PropertyPath(elements);
	}

	/**
	 * Builds an absolute selector path from the given property names. Property names containing dots (e.g. "foo.bar") will be
	 * split into multiple property path elements.
	 *
	 * @param propertyNames
	 *
	 * @return
	 */
	public static PropertyPath pathOf(final String... propertyNames)
	{
		final PropertyPathBuilder builder = new PropertyPathBuilder();
		builder.root();
		for (final String propertyName : propertyNames)
		{
			builder.propertyName(DOT_PATTERN.split(propertyName));
		}
		return builder.toPropertyPath();
	}
}
