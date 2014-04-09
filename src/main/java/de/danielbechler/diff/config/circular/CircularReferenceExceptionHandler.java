package de.danielbechler.diff.config.circular;

import de.danielbechler.diff.node.DiffNode;

/**
 * @author Daniel Bechler
 */
public interface CircularReferenceExceptionHandler
{
	void onCircularReferenceException(DiffNode node);
}
