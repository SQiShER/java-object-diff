package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.node.*;
import org.slf4j.*;

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
}
