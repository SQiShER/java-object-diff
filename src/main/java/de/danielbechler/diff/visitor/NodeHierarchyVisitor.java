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

import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.Visit;
import de.danielbechler.util.Strings;

/**
 * Prints the hierarchy of the object graph in a human-readable form.
 *
 * @author Daniel Bechler
 */
public class NodeHierarchyVisitor implements DiffNode.Visitor
{
	public static final int UNLIMITED = -1;

	private final int maxDepth;

	@SuppressWarnings({"UnusedDeclaration"})
	public NodeHierarchyVisitor()
	{
		this(UNLIMITED);
	}

	public NodeHierarchyVisitor(final int maxDepth)
	{
		this.maxDepth = maxDepth;
	}

	private static int calculateDepth(final DiffNode node)
	{
		int count = 0;
		DiffNode parentNode = node.getParentNode();
		while (parentNode != null)
		{
			count++;
			parentNode = parentNode.getParentNode();
		}
		return count;
	}

	public void accept(final DiffNode node, final Visit visit)
	{
		if (maxDepth == 0)
		{
			visit.stop();
		}
		final int currentLevel = calculateDepth(node);
		if (maxDepth > 0)
		{
			if (currentLevel <= maxDepth)
			{
				print(node, currentLevel);
			}
			else
			{
				visit.dontGoDeeper();
			}
		}
		else if (maxDepth < 0)
		{
			print(node, currentLevel);
		}
	}

	protected void print(final DiffNode node, final int level)
	{
		final String nodeAsString = node.getPath() + " ===> " + node.toString();
		final String indentedNodeString = Strings.indent(level, nodeAsString);
		print(indentedNodeString);
	}

	@SuppressWarnings({"MethodMayBeStatic"})
	protected void print(final String text)
	{
		System.out.println(text);
	}
}
