package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class RootElement implements PropertyPath.Element
{
	private static final RootElement instance = new RootElement();

	private RootElement()
	{
	}

	public static RootElement getInstance()
	{
		return instance;
	}

//	@Override
//	public boolean matches(final IDifference<?> difference)
//	{
//		return difference != null && difference.getAccessor().getPropertySelector() instanceof RootPropertySelector;
//	}

	@Override
	public String toString()
	{
		return "root";
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (getClass().equals(o.getClass()))
		{
			return true;
		}
		return false;
	}
}
