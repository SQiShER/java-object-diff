package de.danielbechler.diff.path;

import java.util.*;

/** @author Daniel Bechler */
public final class PropertyPath
{
	private final List<Element> elements;

	public PropertyPath(final Element... selectors)
	{
		this(Arrays.asList(selectors));
	}

	public PropertyPath(final Collection<Element> selectors)
	{
		elements = new ArrayList<Element>(selectors);
	}

	public PropertyPath(final PropertyPath parentSelectorPath, final Element selector)
	{
		elements = new ArrayList<Element>(parentSelectorPath.getElements().size() + 1);
		elements.addAll(parentSelectorPath.elements);
		elements.add(selector);
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public boolean matches(final PropertyPath path)
	{
		return path.equals(this);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final PropertyPath that = (PropertyPath) o;

		if (!elements.equals(that.elements))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return elements.hashCode();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext())
		{
			final Element selector = iterator.next();
			if (!(selector instanceof RootElement))
			{
				sb.append(selector);
				if (iterator.hasNext())
				{
					sb.append('.');
				}
			}
		}
		return sb.toString();
	}

	public boolean isParentOf(final PropertyPath selectorPath)
	{
		final Iterator<Element> iterator1 = elements.iterator();
		final Iterator<Element> iterator2 = selectorPath.getElements().iterator();
		while (iterator1.hasNext() && iterator2.hasNext())
		{
			final Element next1 = iterator1.next();
			final Element next2 = iterator2.next();
			if (!next1.equals(next2))
			{
				return false;
			}
		}
		return true;
	}

	/** @author Daniel Bechler */
	public static interface Element
	{
		boolean equals(Object o);

		int hashCode();
	}
}
