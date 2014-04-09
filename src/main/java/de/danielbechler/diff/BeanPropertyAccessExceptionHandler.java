package de.danielbechler.diff;

import de.danielbechler.diff.node.DiffNode;

/**
 * @author Daniel Bechler
 */
@SuppressWarnings("UnusedDeclaration")
public interface BeanPropertyAccessExceptionHandler
{
	DiffNode onPropertyWriteException(BeanPropertyWriteException ex, DiffNode propertyNode);

	DiffNode onPropertyReadException(BeanPropertyReadException ex, DiffNode propertyNode);
}
