package de.danielbechler.diff.visitor;

/** @author Daniel Bechler */
public final class StopVisitationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public StopVisitationException()
	{
	}

	@Override
	public Throwable fillInStackTrace()
	{
		return null;
	}
}
