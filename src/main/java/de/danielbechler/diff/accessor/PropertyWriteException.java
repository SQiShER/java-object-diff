package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public class PropertyWriteException extends PropertyException
{
	private static final long serialVersionUID = 1L;

	public PropertyWriteException(final Throwable cause)
	{
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return "Error while invoking write method. ";
	}
}
