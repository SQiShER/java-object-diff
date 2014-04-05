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

package de.danielbechler.diff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.danielbechler.diff.Inclusion.EXCLUDED;
import static de.danielbechler.diff.Inclusion.INCLUDED;

/**
 * Created by Daniel Bechler.
 */
public class ConfigNode
{
	private final Map<ElementSelector, ConfigNode> children = new HashMap<ElementSelector, ConfigNode>();
	private final ElementSelector elementSelector;
	private final ConfigNode parent;
	@Deprecated
	private ConfigNode prototype;
	private Inclusion inclusion;

	public ConfigNode()
	{
		this(RootElementSelector.getInstance(), null);
	}

	private ConfigNode(final ElementSelector elementSelector, final ConfigNode parent)
	{
		this.elementSelector = elementSelector;
		this.parent = parent;
	}

	public Inclusion getInclusion()
	{
		return inclusion;
	}

	public void setInclusion(final Inclusion inclusion)
	{
		this.inclusion = inclusion;
	}

	public ElementSelector getElementSelector()
	{
		return elementSelector;
	}

	public ConfigNode getParent()
	{
		return parent;
	}

	public ConfigNode getNodeForPath(final NodePath nodePath)
	{
		if (parent == null)
		{
			final List<ElementSelector> elementSelectors = nodePath.getElementSelectors();
			if (elementSelectors.size() != 1)
			{
				return getChild(elementSelectors.subList(1, elementSelectors.size()));
			}
			else
			{
				return this;
			}
		}
		else
		{
			return parent.getNodeForPath(nodePath);
		}
	}

	public ConfigNode getChild(final ElementSelector childSelector)
	{
		if (childSelector == RootElementSelector.getInstance())
		{
			throw new IllegalArgumentException("A child node can never be the root");
		}
		if (childSelector instanceof AnyElementSelector)
		{
			if (prototype == null)
			{
				prototype = new ConfigNode(childSelector, parent);
			}
			return prototype;
		}
		if (children.containsKey(childSelector))
		{
			return children.get(childSelector);
		}
		else
		{
			final ConfigNode childNode = new ConfigNode(childSelector, this);
			children.put(childSelector, childNode);
			return childNode;
		}
	}

	private ConfigNode getChild(final List<ElementSelector> childSelectors)
	{
		assert !childSelectors.isEmpty();
		if (childSelectors.contains(RootElementSelector.getInstance()))
		{
			throw new IllegalArgumentException("Child nodes can never match the RootElementSelector");
		}
		else if (childSelectors.size() == 1)
		{
			return getChild(childSelectors.get(0));
		}
		final ConfigNode child = getChild(childSelectors.get(0));
		return child.getChild(childSelectors.subList(1, childSelectors.size()));
	}

	public boolean hasChild(final ElementSelector childSelector)
	{
		return children.get(childSelector) != null;
	}

	public boolean isIncluded()
	{
		if (inclusion != EXCLUDED)
		{
			final ConfigNode parentWithInclusion = getClosestParentWithInclusion();
			if (parentWithInclusion != null)
			{
				return parentWithInclusion.getInclusion() != EXCLUDED;
			}
			if (!hasInclusion() && hasPrototype() && prototype.hasInclusion())
			{
				return prototype.inclusion == INCLUDED;
			}
			if (inclusion == INCLUDED)
			{
				return true;
			}
			if (hasIncludedChildren())
			{
				return true;
			}
		}
		return false;
	}

	public boolean isExcluded()
	{
		if (inclusion == EXCLUDED)
		{
			return true;
		}
		if (parent != null && parent.isExcluded())
		{
			return true;
		}
		return false;
	}

	public ConfigNode getClosestParentWithInclusion()
	{
		if (parent != null)
		{
			if (parent.hasInclusion())
			{
				return parent;
			}
			else
			{
				return parent.getClosestParentWithInclusion();
			}
		}
		return null;
	}

	private boolean hasIncludedChildren()
	{
		for (final ConfigNode child : children.values())
		{
			if (child.isIncluded())
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasInclusion()
	{
		return inclusion != null;
	}

	public boolean containsInclusion(final Inclusion inclusion)
	{
		if (this.inclusion == inclusion)
		{
			return true;
		}
		else
		{
			for (final ConfigNode child : children.values())
			{
				if (child.containsInclusion(inclusion))
				{
					return true;
				}
			}
		}
		return false;
	}

	public ConfigNode getPrototype()
	{
		return prototype;
	}

	public void setPrototype(final ConfigNode prototype)
	{
		this.prototype = prototype;
	}

	public boolean hasPrototype()
	{
		return prototype != null;
	}
}
