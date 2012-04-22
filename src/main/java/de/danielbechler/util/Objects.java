package de.danielbechler.util;

/** @author Daniel Bechler */
public class Objects
{
	private Objects()
	{
	}

	public static boolean isEqual(final Object a, final Object b)
	{
		if (a != null)
		{
			return a.equals(b);
		}
		else if (b != null)
		{
			return b.equals(a);
		}
		return true;
	}
}
