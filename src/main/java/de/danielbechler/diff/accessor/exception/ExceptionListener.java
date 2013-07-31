package de.danielbechler.diff.accessor.exception;

import de.danielbechler.diff.node.Node;

/**
 * Handler for recoverable exceptional states. The exception handler is notified
 * when the library catches recoverable exceptions or is in a recoverable but
 * exceptional state.
 */
public interface ExceptionListener {
	/**
	 * Called when CircularReferenceException is caught.
	 * 
	 * @param node
	 *            Current node.
	 */
	void onCircularReferenceException(Node node);
}
