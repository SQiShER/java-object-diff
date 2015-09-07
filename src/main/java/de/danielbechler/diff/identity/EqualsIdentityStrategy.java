package de.danielbechler.diff.identity;

import de.danielbechler.util.Objects;

/**
 * Default implementation that uses Object.equals.
 */
public class EqualsIdentityStrategy implements IdentityStrategy
{
	private static final EqualsIdentityStrategy instance = new EqualsIdentityStrategy();

	public boolean equals(final Object working, final Object base)
	{
		return Objects.isEqual(working, base);
	}

	public static EqualsIdentityStrategy getInstance()
	{
		return instance;
	}
}
