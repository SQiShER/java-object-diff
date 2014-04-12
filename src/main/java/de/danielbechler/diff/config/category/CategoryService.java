package de.danielbechler.diff.config.category;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.path.NodePathValueHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

/**
 *
 */
public class CategoryService implements CategoryConfiguration, CategoryResolver
{
	private final NodePathValueHolder<String[]> nodePathCategories = NodePathValueHolder.of(String[].class);
	private final Map<Class<?>, String[]> typeCategories = new HashMap<Class<?>, String[]>();

	private static Collection<String> categoriesFromNode(final DiffNode node)
	{
		return node.getCategories();
	}

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
		final Collection<String> allCategories = new HashSet<String>();
		final List<String[]> accumulatedValues = nodePathCategories.accumulatedValuesForNodePath(node.getPath());
		for (final String[] categoriesForElement : accumulatedValues)
		{
			allCategories.addAll(asList(categoriesForElement));
		}
		return allCategories;
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
