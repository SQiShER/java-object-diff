package de.danielbechler.diff.mock;

/** @author Daniel Bechler */
public class ObjectWithString
{
	private String value;

	public ObjectWithString()
	{
	}

	public ObjectWithString(final String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}
}
