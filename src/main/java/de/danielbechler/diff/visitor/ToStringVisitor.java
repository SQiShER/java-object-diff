package de.danielbechler.diff.visitor;

import de.danielbechler.diff.visitor.PrintingVisitor;

public class ToStringVisitor extends PrintingVisitor
{
	String	string;

	public ToStringVisitor(Object working, Object base)
	{
		super(working, base);
	}

	protected void print(final String text)
	{
		string = text;
	}

	public String getString()
	{
		return string;
	}
}
