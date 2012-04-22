package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public class PrintingVisitor implements DiffNode.Visitor
{
	private final Object base;
	private final Object modified;

	public PrintingVisitor(final Object base, final Object modified)
	{
		this.base = base;
		this.modified = modified;
	}

	@Override
	public void accept(final DiffNode<?> difference, final Visit visit)
	{
		print(differenceToString(difference, base, modified));
	}

	protected void print(final String text)
	{
		System.out.println(text);
	}

	@SuppressWarnings({"MethodMayBeStatic"})
	protected String differenceToString(final DiffNode<?> difference, final Object base, final Object modified)
	{
		return String.format("%s (%s). Before: %s; After: %s",
							 difference.getPropertyPath(),
							 difference.getType(),
							 difference.getCanonicalAccessor().get(base),
							 difference.getCanonicalAccessor().get(modified));
	}
}
