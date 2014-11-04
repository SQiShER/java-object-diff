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

import de.danielbechler.diff.category.CategoryResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

class CategoryInclusionResolver implements InclusionResolver
{
	private final CategoryResolver categoryResolver;
	private final Map<String, Inclusion> categoryInclusions = new HashMap<String, Inclusion>();
	private boolean containsIncluded;
	private boolean containsExcluded;

	public CategoryInclusionResolver(final CategoryResolver categoryResolver)
	{
		Assert.notNull(categoryResolver, "categoryResolver");
		this.categoryResolver = categoryResolver;
	}

	public Inclusion getInclusion(final DiffNode node)
	{
		if (isInactive())
		{
			return DEFAULT;
		}
		final Set<String> categories = categoryResolver.resolveCategories(node);
		Inclusion resolvedInclusion = DEFAULT;
		for (final String category : categories)
		{
			final Inclusion configuredInclusion = categoryInclusions.get(category);
			if (configuredInclusion == EXCLUDED)
			{
				return EXCLUDED;
			}
			if (configuredInclusion == INCLUDED)
			{
				resolvedInclusion = INCLUDED;
			}
		}
		return resolvedInclusion;
	}

	private boolean isInactive()
	{
		return !containsIncluded && !containsExcluded;
	}

	public boolean enablesStrictIncludeMode()
	{
		return containsIncluded;
	}

	public void setInclusion(final String category, final Inclusion inclusion)
	{
		categoryInclusions.put(category, inclusion);
		containsIncluded = categoryInclusions.containsValue(INCLUDED);
		containsExcluded = categoryInclusions.containsValue(EXCLUDED);
	}
}
