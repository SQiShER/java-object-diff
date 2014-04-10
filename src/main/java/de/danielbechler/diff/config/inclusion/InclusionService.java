package de.danielbechler.diff.config.inclusion;

import de.danielbechler.diff.config.category.CategoryResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.node.path.BeanPropertyElementSelector;
import de.danielbechler.diff.node.path.ElementSelector;
import de.danielbechler.diff.node.path.NodePath;
import de.danielbechler.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.danielbechler.diff.config.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.config.inclusion.Inclusion.INCLUDED;

/**
 *
 */
public class InclusionService<T> implements InclusionConfiguration<T>, IsIgnoredResolver
{
	private final CategoryResolver categoryResolver;
	private final T rootConfiguration;
	private final ConfigNode nodeInclusions = new ConfigNode();
	private final Map<Class<?>, Inclusion> typeInclusions = new HashMap<Class<?>, Inclusion>();
	private final Map<String, Inclusion> categoryInclusions = new HashMap<String, Inclusion>();
	private final Map<String, Inclusion> propertyNameInclusions = new HashMap<String, Inclusion>();
	private final ToInclude<T> includeAndReturn = new ToIncludeAndReturnImpl();
	private final ToExclude<T> excludeAndReturn = new ToExcludeAndReturnImpl();

	public InclusionService(final CategoryResolver categoryResolver, final T rootConfiguration)
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

	private boolean isIncludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, INCLUDED);
	}

	private boolean isIncludedByPath(final DiffNode node)
	{
		return nodeInclusions.getNodeForPath(node.getPath()).isIncluded();
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

	private boolean isExcludedByPropertyName(final DiffNode node)
	{
		final String propertyName = node.getPropertyName();
		if (propertyName != null)
		{
			return propertyNameInclusions.get(propertyName) == EXCLUDED;
		}
		return false;
	}

	private boolean isExcludedByPath(final DiffNode node)
	{
		final ConfigNode configNode = nodeInclusions.getNodeForPath(node.getPath());
		if (configNode.isExcluded() && !configNode.containsInclusion(INCLUDED))
		{
			return true;
		}
		return false;
	}

	private boolean isExcludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, EXCLUDED);
	}

	private boolean hasInclusions(final Inclusion inclusion)
	{
		if (nodeInclusions.containsInclusion(inclusion))
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

	private boolean isIncludedByType(final DiffNode node)
	{
		final AtomicBoolean result = new AtomicBoolean(false);
		node.visitParents(new DiffNode.Visitor()
		{
			public void accept(final DiffNode node, final Visit visit)
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

	private boolean isExcludedByType(final DiffNode node)
	{
		if (node.getValueType() != null)
		{
			return typeInclusions.get(node.getValueType()) == EXCLUDED;
		}
		return false;
	}

	public ToInclude<T> include()
	{
		return includeAndReturn;
	}

	public ToExclude<T> exclude()
	{
		return excludeAndReturn;
	}

	private class ToExcludeAndReturnImpl implements ToExcludeAndReturn<T>
	{
		public T and()
		{
			return rootConfiguration;
		}

		public ToExcludeAndReturn<T> category(final String category)
		{
			categoryInclusions.put(category, EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn<T> type(final Class<?> type)
		{
			typeInclusions.put(type, EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn<T> node(final NodePath nodePath)
		{
			nodeInclusions.getNodeForPath(nodePath).setInclusion(EXCLUDED);
			return this;
		}

		public ToExcludeAndReturn<T> propertyName(final String propertyName)
		{
			propertyNameInclusions.put(propertyName, EXCLUDED);
			return this;
		}

		public ToInclude<T> include()
		{
			return InclusionService.this.include();
		}
	}

	private class ToIncludeAndReturnImpl implements ToIncludeAndReturn<T>
	{
		public T and()
		{
			return rootConfiguration;
		}

		public ToExclude<T> exclude()
		{
			return InclusionService.this.exclude();
		}

		public ToIncludeAndReturn<T> category(final String category)
		{
			categoryInclusions.put(category, INCLUDED);
			return this;
		}

		public ToIncludeAndReturn<T> type(final Class<?> type)
		{
			typeInclusions.put(type, INCLUDED);
			return this;
		}

		public ToIncludeAndReturn<T> node(final NodePath nodePath)
		{
			nodeInclusions.getNodeForPath(nodePath).setInclusion(INCLUDED);
			return this;
		}

		public ToIncludeAndReturn<T> propertyName(final String propertyName)
		{
			propertyNameInclusions.put(propertyName, INCLUDED);
			return this;
		}
	}
}