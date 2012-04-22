package de.danielbechler.diff.path;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class NamedPropertyElement implements PropertyPath.Element
{
	private final String propertyName;

	public NamedPropertyElement(final String propertyName)
	{
		Assert.hasText(propertyName, "propertyName");
		this.propertyName = propertyName;
	}

//	@Override
//	public boolean matches(final IDifference<?> difference)
//	{
//		return difference != null && difference.getAccessor().getPropertySelector().equals(this);
//	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof NamedPropertyElement))
		{
			return false;
		}

		final NamedPropertyElement that = (NamedPropertyElement) o;

		if (!propertyName.equals(that.propertyName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return propertyName.hashCode();
	}

	@Override
	public String toString()
	{
		return propertyName;
	}
}
