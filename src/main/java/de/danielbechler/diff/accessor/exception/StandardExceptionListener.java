package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.*;
import org.slf4j.*;

/**
 * Handler for recoverable exceptional states which logs the warning or info messages into log. The exception
 * handler is notified when the library catches recoverable exceptions or is in a recoverable but exceptional
 * state.
 */
public class StandardExceptionListener implements ExceptionListener
{
	private static final Logger logger = LoggerFactory.getLogger(StandardExceptionListener.class);

	public void onCircularReferenceException(final DiffNode node)
	{
		final String message = "Detected circular reference in node at path {}. "
				+ "Going deeper would cause an infinite loop, so I'll stop looking at "
				+ "this instance along the current path.";
		logger.warn(message, node.getPath());
	}

	public DiffNode onPropertyWriteException(final PropertyWriteException ex, final DiffNode propertyNode)
	{
		logger.info("Couldn't set new value '{}' for property '{}'", ex.getNewValue(), ex.getPropertyName());
		throw ex;
	}

	public DiffNode onPropertyReadException(final PropertyReadException ex, final DiffNode propertyNode)
	{
		throw ex;
	}
}
