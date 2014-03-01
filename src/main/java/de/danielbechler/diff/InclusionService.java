package de.danielbechler.diff;

import de.danielbechler.diff.bean.*;
import de.danielbechler.util.*;

import java.util.*;

import static de.danielbechler.diff.Inclusion.EXCLUDED;
import static de.danielbechler.diff.Inclusion.INCLUDED;

/**
 *
 */
class InclusionService implements InclusionConfiguration, IsIgnoredResolver
{
	private final CategoryResolver categoryResolver;
	private final NodePathValueHolder<Inclusion> nodeInclusions = NodePathValueHolder.of(Inclusion.class);
	private final Map<Class<?>, Inclusion> typeInclusions = new HashMap<Class<?>, Inclusion>();
	private final Map<String, Inclusion> categoryInclusions = new TreeMap<String, Inclusion>();
	private final Map<String, Inclusion> propertyNameInclusions = new TreeMap<String, Inclusion>();

	public InclusionService(final CategoryResolver categoryResolver)
	{
		Assert.notNull(categoryResolver, "categoryResolver");
		this.categoryResolver = categoryResolver;
	}

	private static String getNodePropertyName(final DiffNode node)
	{
		final Element pathElement = node.getPathElement();
		if (pathElement instanceof BeanPropertyElement)
		{
			return ((BeanPropertyElement) pathElement).getPropertyName();
		}
		return null;
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
		final String propertyName = getNodePropertyName(node);
		if (propertyName != null)
		{
			return propertyNameInclusions.get(propertyName) == INCLUDED;
		}
		return false;
	}

	private boolean isIncludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, INCLUDED);
	}

	private boolean isIncludedByPath(final DiffNode node)
	{
		return getInclusionByPath(node.getPath(), INCLUDED) == INCLUDED;
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
		final String propertyName = getNodePropertyName(node);
		if (propertyName != null)
		{
			return propertyNameInclusions.get(propertyName) == EXCLUDED;
		}
		return false;
	}

	private boolean isExcludedByPath(final DiffNode node)
	{
		return getInclusionByPath(node.getPath(), EXCLUDED) == EXCLUDED;
	}

	private boolean isExcludedByCategory(final DiffNode node)
	{
		return hasCategoryWithInclusion(node, EXCLUDED);
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

	private Inclusion getInclusionByPath(final NodePath nodePath, final Inclusion inclusion)
	{
		final List<Inclusion> inclusions = nodeInclusions.accumulatedValuesForNodePath(nodePath);
		if (inclusions.contains(inclusion))
		{
			return inclusion;
		}
		else
		{
			return nodeInclusions.valueForNodePath(nodePath);
		}
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

		public To nodes(final NodePath... nodePaths)
		{
			for (final NodePath nodePath : nodePaths)
			{
				nodeInclusions.put(nodePath, inclusion);
			}
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
