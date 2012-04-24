package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
@SuppressWarnings({"MethodMayBeStatic"})
public class PrintingVisitor implements Node.Visitor
{
	private final Object base;
	private final Object modified;

	public PrintingVisitor(final Object base, final Object modified)
	{
		this.base = base;
		this.modified = modified;
	}

	public void accept(final Node difference, final Visit visit)
	{
		final String text = differenceToString(difference, base, modified);
		print(text);
	}

	protected void print(final String text)
	{
		System.out.println(text);
	}

	protected String differenceToString(final Node difference, final Object base, final Object modified)
	{
		return String.format("%s (%s). Before: %s; After: %s",
							 difference.getPropertyPath(),
							 difference.getState(),
							 difference.canonicalGet(base),
							 difference.canonicalGet(modified));
	}
}
