package de.danielbechler.diff.accessor.exception;

/** @author Daniel Bechler */
public final class ItemNotIndexedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ItemNotIndexedException(final Object item)
	{
		super("Item has not been indexed: " + item.toString());
	}
}
