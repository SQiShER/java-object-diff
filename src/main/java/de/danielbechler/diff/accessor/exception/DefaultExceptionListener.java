package de.danielbechler.diff.accessor.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danielbechler.diff.node.Node;

/**
 * Handler for recoverable exceptional states which logs the warning or info messages into log. The exception handler is
 * notified when the library catches recoverable exceptions or is in a recoverable but exceptional state.
 */
public class DefaultExceptionListener implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionListener.class);

    public void onCircularReferenceException(final Node node) {
        final String message = "Detected circular reference in node at path {}. "
                + "Going deeper would cause an infinite loop, so I'll stop looking at "
                + "this instance along the current path.";
        logger.warn(message, node.getPropertyPath());
    }

    public void onPropertyWriteException(Class<?> type, String propertyName, Object value, Exception e) {
        logger.info("Couldn't set new value '{}' for property '{}'", value, propertyName);

        final PropertyWriteException ex = new PropertyWriteException(e);
        ex.setPropertyName(propertyName);
        ex.setTargetType(type);
        throw ex;
    }

    public Object onPropertyReadException(Class<?> type, String propertyName, Exception e) {
        final PropertyReadException ex = new PropertyReadException(e);
        ex.setPropertyName(propertyName);
        ex.setTargetType(type);
        throw ex;
    }
}
