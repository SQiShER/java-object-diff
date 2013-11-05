package de.danielbechler.diff;

import java.util.*;

import static java.util.Arrays.*;
import static java.util.Collections.*;

/**
 *
 */
class CategoryService implements CategoryConfiguration, CategoryResolver
{
	private final NodePathValueHolder<String[]> nodePathCategories = NodePathValueHolder.of(String[].class);
	private final Map<Class<?>, String[]> typeCategories = new HashMap<Class<?>, String[]>();

	public Set<String> resolveCategories(final DiffNode node)
	{
		final Set<String> categories = new TreeSet<String>();
		categories.addAll(categoriesFromNodePathConfiguration(node));
		categories.addAll(categoriesFromTypeConfiguration(node));
		categories.addAll(categoriesFromNode(node));
		return categories;
	}

	private Collection<String> categoriesFromNodePathConfiguration(final DiffNode node)
	{
		final String[] categories = nodePathCategories.valueForNodePath(node.getPath());
		if (categories != null)
		{
			return asList(categories);
		}
		return emptySet();
	}

	private Collection<String> categoriesFromTypeConfiguration(final DiffNode node)
	{
		final Class<?> nodeType = node.getValueType();
		if (nodeType != null)
		{
			final String[] categories = typeCategories.get(nodeType);
			if (categories != null)
			{
				return asList(categories);
			}
		}
		return emptySet();
	}

	private static Collection<String> categoriesFromNode(final DiffNode node)
	{
		return node.getCategories();
	}

	public Of ofNode(final NodePath nodePath)
	{
		return new Of()
		{
			public CategoryConfiguration toBe(final String... categories)
			{
				nodePathCategories.put(nodePath, categories);
				return CategoryService.this;
			}
		};
	}

	public Of ofType(final Class<?> type)
	{
		return new Of()
		{
			public CategoryConfiguration toBe(final String... categories)
			{
				typeCategories.put(type, categories);
				return CategoryService.this;
			}
		};
	}
}
