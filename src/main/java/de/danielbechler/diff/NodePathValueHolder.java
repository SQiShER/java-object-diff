package de.danielbechler.diff;

import de.danielbechler.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Bechler
 */
class NodePathValueHolder<T>
{
	private final Map<Element, NodePathValueHolder<T>> elementValueHolders = new HashMap<Element, NodePathValueHolder<T>>();
	private T value;

	public static <T> NodePathValueHolder<T> of(final Class<T> type)
	{
		Assert.notNull(type, "type");
		return new NodePathValueHolder<T>();
	}

	public NodePathValueHolder<T> put(final NodePath nodePath, final T value)
	{
		put(nodePath.getElements(), value);
		return this;
	}

	private void put(final List<Element> nodePathElements, final T value)
	{
		if (nodePathElements.isEmpty())
		{
			return;
		}
		final Element element = nodePathElements.get(0);
		NodePathValueHolder<T> nodePathValueHolder = elementValueHolders.get(element);
		if (nodePathValueHolder == null)
		{
			nodePathValueHolder = new NodePathValueHolder<T>();
			elementValueHolders.put(element, nodePathValueHolder);
		}
		if (nodePathElements.size() == 1)
		{
			nodePathValueHolder.value = value;
		}
		else
		{
			final List<Element> nodePathElementsTail = new ArrayList<Element>(nodePathElements.size() - 1);
			nodePathElementsTail.addAll(nodePathElements.subList(1, nodePathElements.size()));
			nodePathValueHolder.put(nodePathElementsTail, value);
		}
	}

	private T visit(final List<T> accumulator, final Iterator<Element> elementIterator)
	{
		if (value != null)
		{
			accumulator.add(value);
		}
		if (elementIterator.hasNext())
		{
			final Element element = elementIterator.next();
			final NodePathValueHolder<T> valueHolder = elementValueHolders.get(element);
			if (valueHolder != null)
			{
				return valueHolder.visit(accumulator, elementIterator);
			}
			return null;
		}
		else
		{
			return value;
		}
	}

	public T valueForNodePath(final NodePath nodePath)
	{
		return visit(new LinkedList<T>(), nodePath.getElements().iterator());
	}

	public List<T> accumulatedValuesForNodePath(final NodePath nodePath)
	{
		final List<T> accumulator = new LinkedList<T>();
		visit(accumulator, nodePath.getElements().iterator());
		return accumulator;
	}

	public boolean containsValue(final T value)
	{
		if (value == null && this.value == null)
		{
			return true;
		}
		else if (value != null && value.equals(this.value))
		{
			return true;
		}
		else
		{
			for (final NodePathValueHolder<T> valueHolder : elementValueHolders.values())
			{
				if (valueHolder.containsValue(value))
				{
					return true;
				}
			}
			return false;
		}
	}
}
