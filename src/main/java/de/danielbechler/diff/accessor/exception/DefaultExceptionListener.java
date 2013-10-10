package de.danielbechler.diff.accessor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danielbechler.diff.node.Node;

/**
 * Handler for recoverable exceptional states which logs the warning or info messages into log. The exception
 * handler is notified when the library catches recoverable exceptions or is in a recoverable but exceptional
 * state.
 */
public class DefaultExceptionListener implements ExceptionListener
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionListener.class);

	public void onCircularReferenceException(final Node node)
	{
		final String message = "Detected circular reference in node at path {}. "
				+ "Going deeper would cause an infinite loop, so I'll stop looking at "
				+ "this instance along the current path.";
		logger.warn(message, node.getPropertyPath());
	}

	public Node onPropertyWriteException(final PropertyWriteException ex, final Node propertyNode)
	{
		logger.info("Couldn't set new value '{}' for property '{}'", ex.getNewValue(), ex.getPropertyName());
		throw ex;
	}

	public Node onPropertyReadException(final PropertyReadException ex, final Node propertyNode)
	{
		throw ex;
	}
}