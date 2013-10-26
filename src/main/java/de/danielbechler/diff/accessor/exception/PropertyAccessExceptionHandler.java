package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.*;

/** @author Daniel Bechler */
public interface PropertyAccessExceptionHandler
{
	DiffNode onPropertyWriteException(PropertyWriteException ex, DiffNode propertyNode);

	DiffNode onPropertyReadException(PropertyReadException ex, DiffNode propertyNode);
}
