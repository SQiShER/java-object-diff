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

package de.danielbechler.diff.node;

import de.danielbechler.util.Collections;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Daniel Bechler
 */
@SuppressWarnings("UnusedDeclaration")
public class CategoryFilteringVisitor extends AbstractFilteringVisitor
{
	private final Collection<String> include = new TreeSet<String>();
	private final Collection<String> exclude = new TreeSet<String>();

	private boolean includeAllNonExcluded;

	@Override
	protected boolean accept(final DiffNode node)
	{
		if (isExcluded(node))
		{
			return false;
		}
		if (isIncluded(node) || includeAllNonExcluded)
		{
			return true;
		}
		return false;
	}

	@Override
	protected void onDismiss(final DiffNode node, final Visit visit)
	{
		super.onDismiss(node, visit);
		visit.dontGoDeeper();
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private boolean isExcluded(final DiffNode node)
	{
		return Collections.containsAny(node.getCategories(), exclude);
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private boolean isIncluded(final DiffNode node)
	{
		return Collections.containsAny(node.getCategories(), include);
	}

	public final CategoryFilteringVisitor include(final String category)
	{
		include.add(category);
		exclude.remove(category);
		return this;
	}

	/**
	 * @deprecated This method is confusing. The name implies only nodes with the given category
	 * will be included, but that's not the case. Instead every previously included category will
	 * also be included. On top of that every excluded category will remain excluded. This method
	 * will be removed in future versions until the feature is explicitly requested.
	 */
	@Deprecated
	public final CategoryFilteringVisitor includeOnly(final String category)
	{
		include(category);
		includeAllNonExcluded(false);
		return this;
	}

	public final CategoryFilteringVisitor includeAllNonExcluded(final boolean value)
	{
		includeAllNonExcluded = value;
		return this;
	}

	public final CategoryFilteringVisitor exclude(final String category)
	{
		exclude.add(category);
		include.remove(category);
		return this;
	}
}
