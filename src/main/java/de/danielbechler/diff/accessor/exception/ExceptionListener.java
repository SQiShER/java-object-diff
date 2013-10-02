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
	 * @param e
	 *            PropertyWriteException itself.
	 * @param propertyNode
	 *            Node of the property that we were unable to write to.
	 * @return Resulting node.
	 */
	Node onPropertyWriteException(PropertyWriteException e, Node propertyNode);

	/**
	 * Called when PropertyReadException happens.
	 * 
	 * @param ex
	 *            The PropertyReadException itself.
	 * @param propertyNode
	 *            Node of the property that we were unable to read.
	 * @return Node as a result of wrong read.
	 */
	Node onPropertyReadException(PropertyReadException ex, Node propertyNode);
}