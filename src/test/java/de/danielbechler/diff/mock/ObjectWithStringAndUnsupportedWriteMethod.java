package de.danielbechler.diff.mock;

/** @author Daniel Bechler */
public class ObjectWithStringAndUnsupportedWriteMethod extends ObjectWithString
{
	public ObjectWithStringAndUnsupportedWriteMethod()
	{
	}

	public ObjectWithStringAndUnsupportedWriteMethod(final String value)
	{
		super(value);
	}

	@Override
	public void setValue(final String value)
	{
		throw new UnsupportedOperationException();
	}
}
