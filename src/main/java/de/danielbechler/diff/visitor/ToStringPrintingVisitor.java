package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;

import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;

/** @author Daniel Bechler */
public class ToStringPrintingVisitor implements Node.Visitor
{
	private int depth;

	public ToStringPrintingVisitor()
	{
	}

	public ToStringPrintingVisitor(final int depth)
	{
		this.depth = depth;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(final int depth)
	{
		this.depth = depth;
	}

	@Override
	public void accept(final Node node, final Visit visit)
	{
		if (depth > 0 && calculateDepth(node) <= depth)
		{
			System.out.println(toIndentedString(node));
		}
		else
		{
			visit.dontGoDeeper();
		}
	}

	private static String toIndentedString(final Node node)
	{
		final int level = calculateDepth(node);
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++)
		{
			sb.append("    ");
		}
		sb.append(node.toString());
		return sb.toString();
	}

	private static int calculateDepth(final Node node)
	{
		int count = 0;
		Node parentNode = node.getParentNode();
		while (parentNode != null)
		{
			count++;
			parentNode = parentNode.getParentNode();
		}
		return count;
	}
}
