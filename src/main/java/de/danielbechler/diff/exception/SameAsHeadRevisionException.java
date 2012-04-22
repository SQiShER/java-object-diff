package de.danielbechler.diff.exception;

/** @author Daniel Bechler */
public final class SameAsHeadRevisionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SameAsHeadRevisionException()
	{
	}

	@Override
	public Throwable fillInStackTrace()
	{
		return null;
	}
}
