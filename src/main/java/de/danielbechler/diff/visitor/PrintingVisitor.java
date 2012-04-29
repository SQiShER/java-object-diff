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
		if (node.hasChanges() && node.getChildren().isEmpty())
		{
			final String text = differenceToString(node, base, working);
			print(text);
		}
	}

	protected void print(final String text)
	{
		System.out.println(text);
	}

	protected String differenceToString(final Node difference, final Object base, final Object modified)
	{
		return String.format("Property at path '%s' %s",
							 difference.getPropertyPath(),
							 translateState(difference.getState(),
											difference.canonicalGet(base),
											difference.canonicalGet(modified)));
	}

	private String translateState(final Node.State state, final Object base, final Object modified)
	{
		if (state == Node.State.IGNORED)
		{
			return "has been ignored";
		}
		else if (state == Node.State.CHANGED)
		{
			return String.format("has changed from [ %s ] to [ %s ]", Strings.toSingleLineString(base), Strings.toSingleLineString(modified));
		}
		else if (state == Node.State.ADDED)
		{
			return String.format("has been added => [ %s ]", Strings.toSingleLineString(modified));
		}
		else if (state == Node.State.REMOVED)
		{
			return String.format("with value [ %s ] has been removed", Strings.toSingleLineString(base));
		}
		else if (state == Node.State.REPLACED)
		{
			return String.format("with value [ %s ] has been replaced by [ %s ]", Strings.toSingleLineString(base), Strings.toSingleLineString(modified));
		}
		else if (state == Node.State.UNTOUCHED)
		{
			return "has not changed";
		}
		return '(' + state.name() + ')';
	}
}
