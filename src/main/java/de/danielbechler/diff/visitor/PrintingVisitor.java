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
import de.danielbechler.diff.path.*;
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

	public void accept(final Node node, final Visit visit)
	{
		if (filter(node))
		{
			final String text = differenceToString(node, base, working);
			print(text);
		}
	}

	protected boolean filter(final Node node)
	{
		return (node.isRootNode() && !node.hasChanges())
				|| (node.hasChanges() && node.getChildren().isEmpty());
	}

	protected void print(final String text)
	{
		System.out.println(text);
	}

	protected String differenceToString(final Node node, final Object base, final Object modified)
	{
		final PropertyPath propertyPath = node.getPropertyPath();
		final String stateMessage = translateState(node.getState(), node.canonicalGet(base), node.canonicalGet(modified));
		final String propertyMessage = String.format("Property at path '%s' %s", propertyPath, stateMessage);
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(propertyMessage);
		if (node.isCircular())
		{
			stringBuilder.append(" (Circular reference detected: The property has already been processed at another position.)");
		}
		return stringBuilder.toString();
	}

	private String translateState(final Node.State state, final Object base, final Object modified)
	{
		if (state == Node.State.IGNORED)
		{
			return "has been ignored";
		}
		else if (state == Node.State.CHANGED)
		{
			return String.format("has changed from [ %s ] to [ %s ]",
					Strings.toSingleLineString(base),
					Strings.toSingleLineString(modified));
		}
		else if (state == Node.State.ADDED)
		{
			return String.format("has been added => [ %s ]", Strings.toSingleLineString(modified));
		}
		else if (state == Node.State.REMOVED)
		{
			return String.format("with value [ %s ] has been removed", Strings.toSingleLineString(base));
		}
		else if (state == Node.State.UNTOUCHED)
		{
			return "has not changed";
		}
		else if (state == Node.State.CIRCULAR)
		{
			return "has already been processed at another position. (Circular reference!)";
		}
		return '(' + state.name() + ')';
	}
}
