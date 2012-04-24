package de.danielbechler.diff.accessor.exception;

/** @author Daniel Bechler */
public class PropertyException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private String propertyName;
	private Class<?> targetType;

	public PropertyException(final Throwable cause)
	{
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return String.format("Property '%s' on target of type %s.", propertyName, targetType);
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public void setPropertyName(final String propertyName)
	{
		this.propertyName = propertyName;
	}

	public Class<?> getTargetType()
	{
		return targetType;
	}

	public void setTargetType(final Class<?> targetType)
	{
		this.targetType = targetType;
	}
}
