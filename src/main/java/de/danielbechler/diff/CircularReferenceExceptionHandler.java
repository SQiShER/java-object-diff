package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface CircularReferenceExceptionHandler
{
	void onCircularReferenceException(DiffNode node);
}
