/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
