/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		if (depth > 0)
		{
			if (calculateDepth(node) <= depth)
			{
				System.out.println(toIndentedString(node));
			}
			else
			{
				visit.dontGoDeeper();
			}
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
