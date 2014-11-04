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

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

class NodePathInclusionResolver implements InclusionResolver
{
	private final ValueNode<Inclusion> inclusions = new ValueNode<Inclusion>();
	private boolean containsIncluded;
	private boolean containsExcluded;

	public Inclusion getInclusion(final DiffNode node)
	{
		if (isInactive())
		{
			return DEFAULT;
		}
		return resolveInclusion(inclusions.getNodeForPath(node.getPath()));
	}

	public boolean enablesStrictIncludeMode()
	{
		return containsIncluded;
	}

	private boolean isInactive()
	{
		return !containsIncluded && !containsExcluded;
	}

	public void setInclusion(final NodePath nodePath, final Inclusion inclusion)
	{
		inclusions.getNodeForPath(nodePath).setValue(inclusion);
		containsIncluded = inclusions.containsValue(INCLUDED);
		containsExcluded = inclusions.containsValue(EXCLUDED);
	}

	private Inclusion resolveInclusion(final ValueNode<Inclusion> inclusionNode)
	{
		// When the node has been explicitly excluded it's clear what to do
		if (inclusionNode.getValue() == EXCLUDED)
		{
			return EXCLUDED;
		}

		// Since excluding a parent node wins over an explicit inclusion
		// of the current node we need to check that first (in fact it shouldn't
		// even be possible to get to this point, if the parent is excluded)
		final Inclusion parentInclusion = resolveParentInclusion(inclusionNode);
		if (parentInclusion == INCLUDED || parentInclusion == EXCLUDED)
		{
			return parentInclusion;
		}

		// Only after the parent has been checked we can honor the explicit
		// inclusion of this node by checking whether itself or any of its
		// children is included
		if (inclusionNode.containsValue(INCLUDED))
		{
			return INCLUDED;
		}

		return DEFAULT;
	}

	private Inclusion resolveParentInclusion(final ValueNode<Inclusion> inclusionNode)
	{
		final ValueNode<Inclusion> parentWithInclusion = inclusionNode.getClosestParentWithValue();
		if (parentWithInclusion != null)
		{
			return resolveInclusion(parentWithInclusion);
		}
		return DEFAULT;
	}
}
