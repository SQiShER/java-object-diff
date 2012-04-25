package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;

import java.lang.String;
import java.lang.StringBuilder;
import java.lang.Override;

/** @author Daniel Bechler */
public class ToStringPrintingVisitor implements Node.Visitor
{
	@Override
	public void accept(final Node node, final Visit visit)
	{
		System.out.println(toIndentedString(node));
	}

	private static String toIndentedString(final Node node)
	{
		final int level = calculateLevel(node);
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++)
		{
			sb.append("    ");
		}
		sb.append(node.toString());
		return sb.toString();
	}

	private static int calculateLevel(final Node node)
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
