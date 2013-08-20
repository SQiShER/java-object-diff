package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface CircularReferenceConfiguration
{
	CircularReferenceConfiguration matchCircularReferencesUsing(CircularReferenceMatchingMode matchingMode);

	CircularReferenceConfiguration handleCircularReferenceExceptionsUsing(CircularReferenceExceptionHandler exceptionHandler);
}
