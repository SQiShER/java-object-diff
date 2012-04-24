package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

/** @author Daniel Bechler */
public final class RootAccessor extends AbstractAccessor
{
	public RootAccessor()
	{
	}

	public String getPropertyName()
	{
		return "";
	}

	public Object get(final Object target)
	{
		return target;
	}

	public void set(final Object target, final Object value)
	{
		throw new UnsupportedOperationException();
	}

	public void unset(final Object target)
	{
		throw new UnsupportedOperationException();
	}

	public PropertyPath.Element getPathElement()
	{
		return RootElement.getInstance();
	}
}
