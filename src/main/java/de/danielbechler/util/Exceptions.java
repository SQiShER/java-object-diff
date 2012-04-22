package de.danielbechler.util;

/** @author Daniel Bechler */
public class Exceptions
{
	private Exceptions()
	{
	}

	public static RuntimeException escalate(final Exception e)
	{
		if (e instanceof RuntimeException)
		{
			return (RuntimeException) e;
		}
		return new RuntimeException(e);
	}

	public static RuntimeException escalate(final String message, final Exception e)
	{
		return new RuntimeException(message, e);
	}
}
