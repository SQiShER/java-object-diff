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

package de.danielbechler.diff.category;

import de.danielbechler.diff.ObjectDifferBuilder;
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
public class CategoryService implements CategoryConfigurer, CategoryResolver
{
	private final NodePathValueHolder<String[]> nodePathCategories = NodePathValueHolder.of(String[].class);
	private final Map<Class<?>, String[]> typeCategories = new HashMap<Class<?>, String[]>();
	private final ObjectDifferBuilder objectDifferBuilder;

	public CategoryService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
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

	private static Collection<String> categoriesFromNode(final DiffNode node)
	{
		return node.getCategories();
	}

	public Of ofNode(final NodePath nodePath)
	{
		return new Of()
		{
			public CategoryConfigurer toBe(final String... categories)
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
			public CategoryConfigurer toBe(final String... categories)
			{
				typeCategories.put(type, categories);
				return CategoryService.this;
			}
		};
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}
}
