package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.*;
import org.slf4j.*;

public class DefaultPropertyAccessExceptionHandler implements PropertyAccessExceptionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultPropertyAccessExceptionHandler.class);

	public DiffNode onPropertyWriteException(final PropertyWriteException exception,
											 final DiffNode propertyNode)
	{
		final Object newValue = exception.getNewValue();
		final String propertyName = exception.getPropertyName();
		logger.info("Couldn't set new value '{}' for property '{}'", newValue, propertyName);
		throw exception;
	}

	public DiffNode onPropertyReadException(final PropertyReadException exception,
											final DiffNode propertyNode)
	{
		throw exception;
	}
}
