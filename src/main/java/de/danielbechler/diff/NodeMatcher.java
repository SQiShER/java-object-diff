package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface NodeMatcher
{
	boolean matches(DiffNode node);
}
