package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.*;

/** @author Daniel Bechler */
@Deprecated
@EqualsOnlyType
public final class EqualsOnlyItem
{
	private final String value;

	public EqualsOnlyItem(final String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

//	@Override
//	public String toString()
//	{
//		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
//	}

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

		final EqualsOnlyItem that = (EqualsOnlyItem) o;

		if (value != null ? !value.equals(that.value) : that.value != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return value != null ? value.hashCode() : 0;
	}
}
