package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public class PropertyReadException extends PropertyException
{
	private static final long serialVersionUID = 1L;

	public PropertyReadException(final Throwable cause)
	{
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return "Failed to invoke read method. ";
	}
}
