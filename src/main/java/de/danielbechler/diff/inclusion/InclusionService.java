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
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Assert;

import java.util.Collection;
import java.util.LinkedList;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

@SuppressWarnings("OverlyComplexAnonymousInnerClass")
public class InclusionService implements InclusionConfigurer, IsIgnoredResolver
{
	private final ObjectDifferBuilder rootConfiguration;
	private final CategoryResolver categoryResolver;
	private final Collection<InclusionResolver> inclusionResolvers = new LinkedList<InclusionResolver>();
	private TypeInclusionResolver typeInclusionResolver;
	private TypePropertyConfigInclusionResolver typePropertyConfigInclusionResolver;
	private CategoryInclusionResolver categoryInclusionResolver;
	private NodePathInclusionResolver nodePathInclusionResolver;
	private PropertyNameInclusionResolver propertyNameInclusionResolver;

	public InclusionService(final CategoryResolver categoryResolver, final ObjectDifferBuilder rootConfiguration)
	{
		Assert.notNull(rootConfiguration, "rootConfiguration");
		Assert.notNull(categoryResolver, "categoryResolver");
		this.rootConfiguration = rootConfiguration;
		this.categoryResolver = categoryResolver;
		addAlwaysOnInclusionResolvers();
	}

	private void addAlwaysOnInclusionResolvers()
	{
		inclusionResolvers.add(new TypePropertyAnnotationInclusionResolver());
	}

	Collection<InclusionResolver> getInclusionResolvers()
	{
		return inclusionResolvers;
	}

	public boolean isIgnored(final DiffNode node)
	{
		if (node.isRootNode())
		{
			return false;
		}
		boolean strictIncludeModeEnabled = false;
		boolean isExplicitlyIncluded = false;
		for (final InclusionResolver inclusionResolver : inclusionResolvers)
		{
			if (inclusionResolver.enablesStrictIncludeMode())
			{
				strictIncludeModeEnabled = true;
			}
			switch (getInclusion(node, inclusionResolver))
			{
				case EXCLUDED:
					return true;
				case INCLUDED:
					isExplicitlyIncluded = true;
					break;
			}
		}
		if (strictIncludeModeEnabled && !isExplicitlyIncluded)
		{
			return true;
		}
		return false;
	}

	private static Inclusion getInclusion(final DiffNode node, final InclusionResolver inclusionResolver)
	{
		final Inclusion inclusion = inclusionResolver.getInclusion(node);
		return inclusion != null ? inclusion : DEFAULT;
	}

	public ToInclude include()
	{
		return new ToInclude()
		{
			public ToInclude category(final String category)
			{
				setCategoryInclusion(INCLUDED, category);
				return this;
			}

			public ToInclude type(final Class<?> type)
			{
				setTypeInclusion(INCLUDED, type);
				return this;
			}

			public ToInclude node(final NodePath nodePath)
			{
				setNodePathInclusion(INCLUDED, nodePath);
				return this;
			}

			public ToInclude propertyName(final String propertyName)
			{
				setPropertyNameInclusion(INCLUDED, propertyName);
				return this;
			}

			public ToInclude propertyNameOfType(final Class<?> type, final String... propertyNames)
			{
				setPropertyNameOfTypeInclusion(INCLUDED, type, propertyNames);
				return this;
			}

			public InclusionConfigurer also()
			{
				return InclusionService.this;
			}

			public ObjectDifferBuilder and()
			{
				return rootConfiguration;
			}
		};
	}

	void setCategoryInclusion(final Inclusion inclusion, final String category)
	{
		if (categoryInclusionResolver == null)
		{
			categoryInclusionResolver = newCategoryInclusionResolver();
			inclusionResolvers.add(categoryInclusionResolver);
		}
		categoryInclusionResolver.setInclusion(category, inclusion);
	}

	void setTypeInclusion(final Inclusion inclusion, final Class<?> type)
	{
		if (typeInclusionResolver == null)
		{
			typeInclusionResolver = newTypeInclusionResolver();
			inclusionResolvers.add(typeInclusionResolver);
		}
		typeInclusionResolver.setInclusion(type, inclusion);
	}

	void setNodePathInclusion(final Inclusion inclusion, final NodePath nodePath)
	{
		if (nodePathInclusionResolver == null)
		{
			nodePathInclusionResolver = newNodePathInclusionResolver();
			inclusionResolvers.add(nodePathInclusionResolver);
		}
		nodePathInclusionResolver.setInclusion(nodePath, inclusion);
	}

	void setPropertyNameInclusion(final Inclusion inclusion, final String propertyName)
	{
		if (propertyNameInclusionResolver == null)
		{
			propertyNameInclusionResolver = newPropertyNameInclusionResolver();
			inclusionResolvers.add(propertyNameInclusionResolver);
		}
		propertyNameInclusionResolver.setInclusion(propertyName, inclusion);
	}

	private void setPropertyNameOfTypeInclusion(final Inclusion inclusion, final Class<?> type, final String... propertyNames)
	{
		Assert.notNull(type, "type");
		for (final String propertyName : propertyNames)
		{
			Assert.hasText(propertyName, "propertyName in propertyNames");
			if (typePropertyConfigInclusionResolver == null)
			{
				typePropertyConfigInclusionResolver = newTypePropertyConfigInclusionResolver();
				inclusionResolvers.add(typePropertyConfigInclusionResolver);
			}
			typePropertyConfigInclusionResolver.setInclusion(type, propertyName, inclusion);
		}
	}

	CategoryInclusionResolver newCategoryInclusionResolver()
	{
		return new CategoryInclusionResolver(categoryResolver);
	}

	TypeInclusionResolver newTypeInclusionResolver()
	{
		return new TypeInclusionResolver();
	}

	NodePathInclusionResolver newNodePathInclusionResolver()
	{
		return new NodePathInclusionResolver();
	}

	PropertyNameInclusionResolver newPropertyNameInclusionResolver()
	{
		return new PropertyNameInclusionResolver();
	}

	TypePropertyConfigInclusionResolver newTypePropertyConfigInclusionResolver()
	{
		return new TypePropertyConfigInclusionResolver();
	}

	public ToExclude exclude()
	{
		return new ToExclude()
		{
			public ToExclude category(final String category)
			{
				setCategoryInclusion(EXCLUDED, category);
				return this;
			}

			public ToExclude type(final Class<?> type)
			{
				setTypeInclusion(EXCLUDED, type);
				return this;
			}

			public ToExclude node(final NodePath nodePath)
			{
				setNodePathInclusion(EXCLUDED, nodePath);
				return this;
			}

			public ToExclude propertyName(final String propertyName)
			{
				setPropertyNameInclusion(EXCLUDED, propertyName);
				return this;
			}

			public ToExclude propertyNameOfType(final Class<?> type, final String... propertyNames)
			{
				setPropertyNameOfTypeInclusion(EXCLUDED, type, propertyNames);
				return this;
			}

			public InclusionConfigurer also()
			{
				return InclusionService.this;
			}

			public ObjectDifferBuilder and()
			{
				return rootConfiguration;
			}
		};
	}

	public InclusionConfigurer resolveUsing(final InclusionResolver inclusionResolver)
	{
		Assert.notNull(inclusionResolver, "inclusionResolver");
		inclusionResolvers.add(inclusionResolver);
		return this;
	}

	public ObjectDifferBuilder and()
	{
		return rootConfiguration;
	}
}