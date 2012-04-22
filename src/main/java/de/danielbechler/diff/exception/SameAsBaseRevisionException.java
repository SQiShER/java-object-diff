package de.danielbechler.diff.exception;

/** @author Daniel Bechler */
public final class SameAsBaseRevisionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SameAsBaseRevisionException()
	{
	}

	@Override
	public Throwable fillInStackTrace()
	{
		return null;
	}
}
