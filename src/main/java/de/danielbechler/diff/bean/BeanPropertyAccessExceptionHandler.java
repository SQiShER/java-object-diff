package de.danielbechler.diff.bean;

import de.danielbechler.diff.*;

/** @author Daniel Bechler */
public interface BeanPropertyAccessExceptionHandler
{
	DiffNode onPropertyWriteException(BeanPropertyWriteException ex, DiffNode propertyNode);

	DiffNode onPropertyReadException(BeanPropertyReadException ex, DiffNode propertyNode);
}
