package de.danielbechler.diff.path;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class NamedPropertyElement extends PropertyPath.Element
{
	private final String propertyName;

	public NamedPropertyElement(final String propertyName)
	{
		Assert.hasText(propertyName, "propertyName");
		this.propertyName = propertyName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (!(element instanceof NamedPropertyElement))
		{
			return false;
		}

		final NamedPropertyElement that = (NamedPropertyElement) element;

		if (!propertyName.equals(that.propertyName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int calculateHashCode()
	{
		return propertyName.hashCode();
	}

	@Override
	public String asString()
	{
		return propertyName;
	}
}
