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
import de.danielbechler.diff.selector.RootElementSelector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INTERNAL CLASS. DON'T USE UNLESS YOU ARE READY TO DEAL WITH API CHANGES
 */
public class ValueNode<V>
{
	protected final Map<ElementSelector, ValueNode<V>> children = new HashMap<ElementSelector, ValueNode<V>>();
	protected final ValueNode<V> parent;
	protected final ElementSelector elementSelector;
	protected V value;

	public ValueNode()
	{
		this(RootElementSelector.getInstance(), null);
	}

	protected ValueNode(final ElementSelector elementSelector, final ValueNode<V> parent)
	{
		this.elementSelector = elementSelector;
		this.parent = parent;
	}

	public ElementSelector getElementSelector()
	{
		return elementSelector;
	}

	public ValueNode<V> getParent()
	{
		return parent;
	}

	public ValueNode<V> getNodeForPath(final NodePath nodePath)
	{
		if (parent == null)
		{
			final List<ElementSelector> elementSelectors = nodePath.getElementSelectors();
			if (elementSelectors.size() != 1)
			{
				return getChild(elementSelectors.subList(1, elementSelectors.size()));
			}
			return this;
		}
		return parent.getNodeForPath(nodePath);
	}

	public ValueNode<V> getChild(final ElementSelector childSelector)
	{
		if (childSelector == RootElementSelector.getInstance())
		{
			throw new IllegalArgumentException("A child node can never be the root");
		}
		if (children.containsKey(childSelector))
		{
			return children.get(childSelector);
		}
		else
		{
			final ValueNode<V> childNode = newNode(childSelector);
			children.put(childSelector, childNode);
			return childNode;
		}
	}

	protected ValueNode<V> newNode(final ElementSelector childSelector)
	{
		return new ValueNode<V>(childSelector, this);
	}

	private ValueNode<V> getChild(final List<ElementSelector> childSelectors)
	{
		if (childSelectors.size() == 1)
		{
			return getChild(childSelectors.get(0));
		}
		else
		{
			final ValueNode<V> child = getChild(childSelectors.get(0));
			return child.getChild(childSelectors.subList(1, childSelectors.size()));
		}
	}

	public boolean hasChild(final ElementSelector childSelector)
	{
		return children.get(childSelector) != null;
	}

	public ValueNode<V> getClosestParentWithValue()
	{
		if (parent != null)
		{
			if (parent.hasValue())
			{
				return parent;
			}
			else
			{
				return parent.getClosestParentWithValue();
			}
		}
		return null;
	}

	public boolean hasValue()
	{
		return value != null;
	}

	public boolean containsValue(final V value)
	{
		if (this.value == value)
		{
			return true;
		}
		for (final ValueNode<V> child : children.values())
		{
			if (child.containsValue(value))
			{
				return true;
			}
		}
		return false;
	}

	public V getValue()
	{
		return value;
	}

	public void setValue(final V value)
	{
		this.value = value;
	}
}
