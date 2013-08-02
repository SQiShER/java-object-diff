package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.node.*;

/**
 * Handler for recoverable exceptional states. The exception handler is notified when the library catches
 * recoverable exceptions or is in a recoverable but exceptional state.
 */
public interface ExceptionListener
{
	/**
	 * Called when CircularReferenceException is caught.
	 *
	 * @param node The node which has been detected to cause a circular reference.
	 */
	void onCircularReferenceException(Node node);

	/**
     * Called when PropertyWriteException happens.
     *
     * @param type Type of the object which was accessed
     * @param propertyName Name of the property.
     * @param value Value which we tried to set.
     * @param e Cause of the exception.
     */
	void onPropertyWriteException(Class<?> type, String propertyName,
			Object value, Exception e);

	/**
     * Called when PropertyReadException happens.
     *
     * @param type Type of the object which was accessed
     * @param propertyName Name of the property.
     * @param e Cause of the exception.
     */
    Object onPropertyReadException(Class<?> type, String propertyName, Exception e);
}
