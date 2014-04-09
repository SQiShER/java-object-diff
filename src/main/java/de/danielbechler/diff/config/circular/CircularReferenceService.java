package de.danielbechler.diff.config.circular;

import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceService implements CircularReferenceConfiguration, CircularReferenceDetectorFactory, CircularReferenceExceptionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(CircularReferenceService.class);

	private CircularReferenceMatchingMode circularReferenceMatchingMode = CircularReferenceMatchingMode.EQUALITY_OPERATOR;
	private CircularReferenceExceptionHandler circularReferenceExceptionHandler = new CircularReferenceExceptionHandler()
	{
		public void onCircularReferenceException(final DiffNode node)
		{
			final String message = "Detected circular reference in node at path {}. "
					+ "Going deeper would cause an infinite loop, so I'll stop looking at "
					+ "this instance along the current path.";
			logger.warn(message, node.getPath());
		}
	};

	public CircularReferenceConfiguration matchCircularReferencesUsing(final CircularReferenceMatchingMode matchingMode)
	{
		this.circularReferenceMatchingMode = matchingMode;
		return this;
	}

	public CircularReferenceConfiguration handleCircularReferenceExceptionsUsing(final CircularReferenceExceptionHandler exceptionHandler)
	{
		this.circularReferenceExceptionHandler = exceptionHandler;
		return this;
	}

	public CircularReferenceDetector createCircularReferenceDetector()
	{
		if (circularReferenceMatchingMode == CircularReferenceMatchingMode.EQUALS_METHOD)
		{
			return new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD);
		}
		else if (circularReferenceMatchingMode == CircularReferenceMatchingMode.EQUALITY_OPERATOR)
		{
			return new CircularReferenceDetector(CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR);
		}
		throw new IllegalStateException();
	}

	public void onCircularReferenceException(final DiffNode node)
	{
		if (circularReferenceExceptionHandler != null)
		{
			circularReferenceExceptionHandler.onCircularReferenceException(node);
		}
	}
}
