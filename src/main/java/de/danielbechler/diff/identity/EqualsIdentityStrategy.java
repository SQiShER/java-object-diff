package de.danielbechler.diff.identity;

import de.danielbechler.util.Objects;

import java.io.Serializable;

/**
 * Default implementation that uses Object.equals.
 */
public class EqualsIdentityStrategy implements IdentityStrategy, Serializable
{
	private static final EqualsIdentityStrategy instance = new EqualsIdentityStrategy();
	private static final long serialVersionUID = 2525831733680397224L;

	public boolean equals(final Object working, final Object base)
	{
		return Objects.isEqual(working, base);
	}

	public static EqualsIdentityStrategy getInstance()
	{
		return instance;
	}
}
