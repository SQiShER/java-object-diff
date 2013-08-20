package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface IntrospectionConfiguration
{
	IntrospectionConfiguration includeChildrenOfNodeWithState(DiffNode.State state);

	IntrospectionConfiguration excludeChildrenOfNodeWithState(DiffNode.State state);
}
