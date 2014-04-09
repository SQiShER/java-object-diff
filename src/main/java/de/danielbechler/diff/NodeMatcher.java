package de.danielbechler.diff;

import de.danielbechler.diff.node.DiffNode;

/**
 * @author Daniel Bechler
 */
public interface NodeMatcher
{
	boolean matches(DiffNode node);
}
