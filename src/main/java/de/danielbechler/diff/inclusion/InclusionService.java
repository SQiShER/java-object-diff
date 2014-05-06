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

package de.danielbechler.diff.inclusion;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.category.CategoryResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

/**
 *
 */
public class InclusionService implements InclusionConfigurer, IsIgnoredResolver
{
	private final CategoryResolver categoryResolver;
	private final ObjectDifferBuilder rootConfiguration;
	private final InclusionNode nodeInclusions = new InclusionNode();
	private final Map<Class<?>, Inclusion> typeInclusions = new HashMap<Class<?>, Inclusion>();
	private final Map<String, Inclusion> categoryInclusions = new HashMap<String, Inclusion>();
	private final Map<String, Inclusion> propertyNameInclusions = new HashMap<String, Inclusion>();
	private final ToInclude includeAndReturn = new ToIncludeAndReturnImpl();
	private final ToExclude excludeAndReturn = new ToExcludeAndReturnImpl();

	public InclusionService(final CategoryResolver categoryResolver, final ObjectDifferBuilder rootConfiguration)
	{
		Assert.notNull(categoryResolver, "categoryResolver");
		Assert.notNull(rootConfiguration, "rootConfiguration");
		this.categoryResolver = categoryResolver;
		this.rootConfiguration = rootConfiguration;
	}

	public boolean isIgnored(final DiffNode node)
	{
		return node.isExcluded() || !isIncluded(node) || isExcluded(node);
	}

	private boolean isIncluded(final DiffNode node)
	{
		if (hasInclusions(INCLUDED))
		{
			if (node.isRootNode())
			{
				return true;
			}
			else if (isIncludedByPath(node))
			{
				return true;
			}
			else if (isIncludedByCategory(node))
			{
				return true;
			}
			else if (isIncludedByType(node))
			{
				return true;
			}
			else if (isIncludedByPropertyName(node))
			{
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean isExcluded(final DiffNode node)
	{
		if (hasInclusions(EXCLUDED))
		{
			if (node.isExcluded())
			{
				return true;
			}
			else if (isExcludedByPath(node))
			{
				return true;
			}
			else if (isExcludedByCategory(node))
			{
				return true;
			}
			else if (isExcludedByType(node))
			{
				return true;
			}
			else if (isExcludedByPropertyName(node))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasInclusions(final Inclusion inclusion)
	{
		if (nodeInclusions.containsValue(inclusion))
		{
			return true;
		}
		if (typeInclusions.containsValue(inclusion))
		{
			return true;
		}
		if (categoryInclusions.containsValue(inclusion))
		{
			return true;
		}
		if (propertyNameInclusions.containsValue(inclusion))
		{
			return true;
		}
		return false;
	}

	private boolean isIncludedByPath(final DiffNode node)
	{
		return nodeInclusions.getNodeForPath(node.getPath()).isIncluded();
	}

	private boolean isIncludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, INCLUDED);
	}

	private boolean isIncludedByType(final DiffNode node)
	{
		final AtomicBoolean result = new AtomicBoolean(false);
		node.visitParents(new DiffNode.Visitor()
		{
			public void node(final DiffNode node, final Visit visit)
			{
				if (node.getValueType() != null)
				{
					if (typeInclusions.get(node.getValueType()) == INCLUDED)
					{
						result.set(true);
						visit.stop();
					}
				}
			}
		});
		if (node.getValueType() != null)
		{
			if (typeInclusions.get(node.getValueType()) == INCLUDED)
			{
				result.set(true);
			}
		}
		return result.get();
	}

	private boolean isIncludedByPropertyName(final DiffNode node)
	{
		if (isIncludedByOwnPropertyName(node))
		{
			return true;
		}
		else if (isIncludedByParentPropertyName(node))
		{
			return true;
		}
		return false;
	}

	private boolean isExcludedByPath(final DiffNode node)
	{
		final InclusionNode valueNode = nodeInclusions.getNodeForPath(node.getPath());
		if (valueNode.isExcluded() && !valueNode.containsValue(INCLUDED))
		{
			return true;
		}
		return false;
	}

	private boolean isExcludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, EXCLUDED);
	}

	private boolean isExcludedByType(final DiffNode node)
	{
		if (node.getValueType() != null)
		{
			return typeInclusions.get(node.getValueType()) == EXCLUDED;
		}
		return false;
	}

	private boolean isExcludedByPropertyName(final DiffNode node)
	{
		final String propertyName = node.getPropertyName();
		if (propertyName != null)
		{
			return propertyNameInclusions.get(propertyName) == EXCLUDED;
		}
		return false;
	}

	private boolean hasCategoryWithInclusion(final DiffNode node, final Inclusion inclusion)
	{
		for (final String category : categoryResolver.resolveCategories(node))
		{
			if (categoryInclusions.get(category) == inclusion)
			{
				return true;
			}
		}
		return false;
	}

	private boolean isIncludedByOwnPropertyName(final DiffNode node)
	{
		final String propertyName = node.getPropertyName();
		if (propertyName != null)
		{
			return propertyNameInclusions.get(propertyName) == INCLUDED;
		}
		return false;
	}

	private boolean isIncludedByParentPropertyName(final DiffNode node)
	{
		final List<ElementSelector> pathElementSelectors = node.getPath().getElementSelectors();
		for (final ElementSelector elementSelector : pathElementSelectors)
		{
			if (elementSelector instanceof BeanPropertyElementSelector)
			{
				final BeanPropertyElementSelector beanPropertyElement = (BeanPropertyElementSelector) elementSelector;
				final String propertyName = beanPropertyElement.getPropertyName();
				if (propertyName != null && propertyNameInclusions.get(propertyName) == INCLUDED)
				{
					return true;
				}
			}
		}
		return false;
	}

	public ToInclude include()
	{
		return includeAndReturn;
	}

	public ToExclude exclude()
	{
		return excludeAndReturn;
	}

	private class ToExcludeAndReturnImpl implements ToExcludeAndReturn
	{
		public ObjectDifferBuilder and()
		{
			return rootConfiguration;
		}

		public ToExcludeAndReturn category(final String category)
		{
			categoryInclusions.put(category, EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn type(final Class<?> type)
		{
			typeInclusions.put(type, EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn node(final NodePath nodePath)
		{
			nodeInclusions.getNodeForPath(nodePath).setValue(EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn propertyName(final String propertyName)
		{
			propertyNameInclusions.put(propertyName, EXCLUDED);
			return this;
		}

		public ToInclude include()
		{
			return InclusionService.this.include();
		}
	}

	private class ToIncludeAndReturnImpl implements ToIncludeAndReturn
	{
		public ObjectDifferBuilder and()
		{
			return rootConfiguration;
		}

		public ToIncludeAndReturn category(final String category)
		{
			categoryInclusions.put(category, INCLUDED);
			return this;
		}

		public ToIncludeAndReturn type(final Class<?> type)
		{
			typeInclusions.put(type, INCLUDED);
			return this;
		}

		public ToIncludeAndReturn node(final NodePath nodePath)
		{
			nodeInclusions.getNodeForPath(nodePath).setValue(INCLUDED);
			return this;
		}

		public ToIncludeAndReturn propertyName(final String propertyName)
		{
			propertyNameInclusions.put(propertyName, INCLUDED);
			return this;
		}

		public ToExclude exclude()
		{
			return InclusionService.this.exclude();
		}
	}
}