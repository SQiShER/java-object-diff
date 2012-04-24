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
