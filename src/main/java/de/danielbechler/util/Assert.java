package de.danielbechler.util;

import java.util.*;

/** @author Daniel Bechler */
public class Assert
{
	private Assert()
	{
	}

	public static void sameTypesOrNull(final Object... objects)
	{
		Class<?> previousType = null;
		for (final Object object : objects)
		{
			if (object != null)
			{
				if (previousType == null)
				{
					previousType = object.getClass();
				}
				else if (!object.getClass().equals(previousType))
				{
					throw new IllegalArgumentException("The given objects should be either null or of the same type ('" + previousType + "')");
				}
			}
		}
	}

	public static void notNull(final Object object, final String name)
	{
		notEmpty(name, "name");
		if (object == null)
		{
			throw new IllegalArgumentException("'" + name + "' must not be null");
		}
	}

	public static void notEmpty(final Collection<?> collection, final String name)
	{
		notEmpty(name, "name");
		if (Collections.isEmpty(collection))
		{
			throw new IllegalArgumentException("'" + name + "' must not be null or empty");
		}
	}

	/**
	 * Same as {@link #hasText(String, String)}.
	 *
	 * @see #hasText(String, String)
	 */
	public static void notEmpty(final String text, final String name) throws IllegalArgumentException
	{
		hasText(text, name);
	}

	/**
	 * Ensures that the given <code>value</code> contains characters.
	 *
	 * @param value The value to check.
	 * @param name  The name of the variable (used for the exception message).
	 *
	 * @throws IllegalArgumentException If the given value is <code>null</code> or doesn't contain any non-whitespace
	 *                                  characters.
	 */
	public static void hasText(final String value, final String name) throws IllegalArgumentException
	{
		if (Strings.isEmpty(name)) // Yo dawg, I heard you like assertions, so I put an assertion in your assertion
		{
			throw new IllegalArgumentException("'name' must not be null or empty");
		}
		if (Strings.isEmpty(value))
		{
			throw new IllegalArgumentException("'" + name + "' must not be null or empty");
		}
	}
}
