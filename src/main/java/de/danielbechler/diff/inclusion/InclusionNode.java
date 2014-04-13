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

import de.danielbechler.diff.path.NodePath;
import de.danielbechler.diff.selector.ElementSelector;

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

/**
 * Created by Daniel Bechler.
 */
class InclusionNode extends ValueNode<Inclusion>
{
	InclusionNode()
	{
	}

	private InclusionNode(final ElementSelector elementSelector, final InclusionNode parent)
	{
		super(elementSelector, parent);
	}

	public boolean isIncluded()
	{
		if (value != EXCLUDED)
		{
			final ValueNode<Inclusion> parentWithInclusion = getClosestParentWithValue();
			if (parentWithInclusion != null)
			{
				return parentWithInclusion.getValue() != EXCLUDED;
			}
			if (value == INCLUDED)
			{
				return true;
			}
			if (containsValue(INCLUDED))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isExcluded()
	{
		if (value == EXCLUDED)
		{
			return true;
		}
		if (parent != null && ((InclusionNode) parent).isExcluded())
		{
			return true;
		}
		return false;
	}

	@Override
	public InclusionNode getParent()
	{
		return (InclusionNode) super.getParent();
	}

	@Override
	public InclusionNode getNodeForPath(final NodePath nodePath)
	{
		return (InclusionNode) super.getNodeForPath(nodePath);
	}

	@Override
	public InclusionNode getChild(final ElementSelector childSelector)
	{
		return (InclusionNode) super.getChild(childSelector);
	}

	@Override
	protected InclusionNode newNode(final ElementSelector childSelector)
	{
		return new InclusionNode(childSelector, this);
	}

	@Override
	public InclusionNode getClosestParentWithValue()
	{
		return (InclusionNode) super.getClosestParentWithValue();
	}
}
