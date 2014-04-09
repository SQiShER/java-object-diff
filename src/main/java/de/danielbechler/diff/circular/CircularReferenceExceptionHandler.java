package de.danielbechler.diff.circular;

import de.danielbechler.diff.node.DiffNode;

/**
 * @author Daniel Bechler
 */
public interface CircularReferenceExceptionHandler
{
	void onCircularReferenceException(DiffNode node);
}
