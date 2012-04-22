package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;

/** @author Daniel Bechler */
@SuppressWarnings({"unchecked"})
public final class RootAccessor<T> extends AbstractAccessor<T>
{
	public RootAccessor()
	{
	}

	public String getPropertyName()
	{
		return "";
	}

	public PropertyPath getPath()
	{
		return new PropertyPath(toPathElement());
	}

	public T get(final Object target)
	{
		return (T) target;
	}

	public void set(final Object target, final Object value)
	{
		throw new UnsupportedOperationException();
	}

	public void unset(final Object target, final Object value)
	{
		throw new UnsupportedOperationException();
	}

	public PropertyPath.Element toPathElement()
	{
		return RootElement.getInstance();
	}
}
