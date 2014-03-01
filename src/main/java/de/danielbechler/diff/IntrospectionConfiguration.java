package de.danielbechler.diff;

/**
 * Allows to replace the default bean introspector with a custom implementation. The default introspector internally
 * uses the `java.beans.Introspector` which has some limitations. The most important one being that it only operates on
 * getters and setters. In case field introspection is needed a custom introspector must be used. An introspector can
 * be
 * set as global default or on a per-property basis. It is also possible to turn off introspection for specific
 * properties in which case they will simply be compared via `equals` method.
 *
 * @author Daniel Bechler
 */
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
