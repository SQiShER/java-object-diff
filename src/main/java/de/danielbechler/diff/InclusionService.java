package de.danielbechler.diff;

import de.danielbechler.diff.bean.BeanPropertyElementSelector;
import de.danielbechler.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static de.danielbechler.diff.Inclusion.EXCLUDED;
import static de.danielbechler.diff.Inclusion.INCLUDED;

/**
 *
 */
class InclusionService implements InclusionConfiguration, IsIgnoredResolver
{
	private final CategoryResolver categoryResolver;
	private final ConfigNode nodeInclusions = new ConfigNode();
	private final Map<Class<?>, Inclusion> typeInclusions = new HashMap<Class<?>, Inclusion>();
	private final Map<String, Inclusion> categoryInclusions = new TreeMap<String, Inclusion>();

	@Deprecated
	private final Map<String, Inclusion> propertyNameInclusions = new TreeMap<String, Inclusion>();

	public InclusionService(final CategoryResolver categoryResolver)
	{
		Assert.notNull(categoryResolver, "categoryResolver");
		this.categoryResolver = categoryResolver;
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
			if (isExcludedByPath(node))
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

	@SuppressWarnings("TypeMayBeWeakened") // we don't want to weaken the type for consistency reasons
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
		return nodeInclusions.getNodeForPath(node.getPath()).isExcluded();
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
		if (node.getValueType() != null)
		{
			return typeInclusions.get(node.getValueType()) == INCLUDED;
		}
		return false;
	}

	private boolean isExcludedByType(final DiffNode node)
	{
		if (node.getValueType() != null)
		{
			return typeInclusions.get(node.getValueType()) == EXCLUDED;
		}
		return false;
	}

	public To toInclude()
	{
		return new ToImpl(INCLUDED);
	}

	public To toExclude()
	{
		return new ToImpl(EXCLUDED);
	}

	private class ToImpl implements To
	{
		private final Inclusion inclusion;

		public ToImpl(final Inclusion inclusion)
		{
			this.inclusion = inclusion;
		}

		public To categories(final String... categories)
		{
			for (final String category : categories)
			{
				categoryInclusions.put(category, inclusion);
			}
			return this;
		}

		public To types(final Class<?>... types)
		{
			for (final Class<?> type : types)
			{
				typeInclusions.put(type, inclusion);
			}
			return this;
		}

		public To node(final NodePath nodePath)
		{
			nodeInclusions.getNodeForPath(nodePath).setInclusion(inclusion);
			return this;
		}

		public To propertyNames(final String... propertyNames)
		{
			for (final String propertyName : propertyNames)
			{
				propertyNameInclusions.put(propertyName, inclusion);
			}
			return this;
		}
	}
}
