/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.util.Collections;

import java.util.*;

/** @author Daniel Bechler */
public abstract class CategoryFilteringVisitor extends AbstractFilteringVisitor
{
	private final Collection<String> include = new TreeSet<String>();
	private final Collection<String> exclude = new TreeSet<String>();

	private boolean includeAllNonExcluded;

	@Override
	protected boolean accept(final Node node)
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
	protected void onDismiss(final Node node, final Visit visit)
	{
		super.onDismiss(node, visit);
		visit.dontGoDeeper();
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private boolean isExcluded(final Node node)
	{
		return Collections.containsAny(node.getCategories(), exclude);
	}

	@SuppressWarnings({"TypeMayBeWeakened"})
	private boolean isIncluded(final Node node)
	{
		return Collections.containsAny(node.getCategories(), include);
	}

	public final CategoryFilteringVisitor include(final String category)
	{
		include.add(category);
		exclude.remove(category);
		return this;
	}

	@SuppressWarnings({"UnusedDeclaration"})
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

	@SuppressWarnings({"UnusedDeclaration"})
	public final CategoryFilteringVisitor exclude(final String category)
	{
		exclude.add(category);
		include.remove(category);
		return this;
	}
}
