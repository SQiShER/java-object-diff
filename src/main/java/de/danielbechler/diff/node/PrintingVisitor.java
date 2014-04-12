/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.node;

import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Strings;

/**
 * @author Daniel Bechler
 */
public class PrintingVisitor implements DiffNode.Visitor
{
	private final Object working;
	private final Object base;

	public PrintingVisitor(final Object working, final Object base)
	{
		this.base = base;
		this.working = working;
	}

	public void node(final DiffNode node, final Visit visit)
	{
		if (filter(node))
		{
			final String text = differenceToString(node, base, working);
			print(text);
		}
	}

	protected boolean filter(final DiffNode node)
	{
		return (node.isRootNode() && !node.hasChanges())
				|| (node.hasChanges() && !node.hasChildren());
	}

	protected String differenceToString(final DiffNode node, final Object base, final Object modified)
	{
		final NodePath nodePath = node.getPath();
		final String stateMessage = translateState(node.getState(), node.canonicalGet(base), node.canonicalGet(modified));
		final String propertyMessage = String.format("Property at path '%s' %s", nodePath, stateMessage);
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(propertyMessage);
		if (node.isCircular())
		{
			stringBuilder.append(" (Circular reference detected: The property has already been processed at another position.)");
		}
		return stringBuilder.toString();
	}

	protected void print(final String text)
	{
		System.out.println(text);
	}

	private static String translateState(final DiffNode.State state, final Object base, final Object modified)
	{
		if (state == DiffNode.State.IGNORED)
		{
			return "has been ignored";
		}
		else if (state == DiffNode.State.CHANGED)
		{
			return String.format("has changed from [ %s ] to [ %s ]",
					Strings.toSingleLineString(base),
					Strings.toSingleLineString(modified));
		}
		else if (state == DiffNode.State.ADDED)
		{
			return String.format("has been added => [ %s ]", Strings.toSingleLineString(modified));
		}
		else if (state == DiffNode.State.REMOVED)
		{
			return String.format("with value [ %s ] has been removed", Strings.toSingleLineString(base));
		}
		else if (state == DiffNode.State.UNTOUCHED)
		{
			return "has not changed";
		}
		else if (state == DiffNode.State.CIRCULAR)
		{
			return "has already been processed at another position. (Circular reference!)";
		}
		return '(' + state.name() + ')';
	}
}
