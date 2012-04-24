package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
@SuppressWarnings({"MethodMayBeStatic"})
public class PrintingVisitor implements Node.Visitor
{
	private final Object working;
	private final Object base;

	public PrintingVisitor(final Object working, final Object base)
	{
		this.base = base;
		this.working = working;
	}

	public void accept(final Node difference, final Visit visit)
	{
		final String text = differenceToString(difference, base, working);
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
							 Strings.toSingleLineString(difference.canonicalGet(base)),
							 Strings.toSingleLineString(difference.canonicalGet(modified)));
	}
}
