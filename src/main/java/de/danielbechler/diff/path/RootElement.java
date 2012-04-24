package de.danielbechler.diff.path;

/** @author Daniel Bechler */
public final class RootElement extends PropertyPath.Element
{
	private static final RootElement instance = new RootElement();

	private RootElement()
	{
	}

	public static RootElement getInstance()
	{
		return instance;
	}

	@Override
	public boolean equals(final PropertyPath.Element element)
	{
		if (this == element)
		{
			return true;
		}
		if (getClass().equals(element.getClass()))
		{
			return true;
		}
		return false;
	}

	@Override
	public int calculateHashCode()
	{
		return 0;
	}

	@Override
	public String asString()
	{
		return "root";
	}
}
