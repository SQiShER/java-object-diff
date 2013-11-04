package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface IntrospectionConfiguration
{
	IntrospectionConfiguration setDefaultIntrospector(Introspector introspector);

	Of ofType(Class<?> type);

	Of ofNode(NodePath path);

	public static interface Of
	{
		IntrospectionConfiguration toUse(Introspector introspector);

		IntrospectionConfiguration toBeEnabled();

		IntrospectionConfiguration toBeDisabled();
	}

}
