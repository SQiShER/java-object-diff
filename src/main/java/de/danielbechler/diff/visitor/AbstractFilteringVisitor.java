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

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractFilteringVisitor implements Node.Visitor
{
	private final Collection<Node> matches = new ArrayList<Node>(30);

	protected abstract boolean accept(final Node node);

	protected void onAccept(final Node node, final Visit visit)
	{
		matches.add(node);
	}

	protected void onDismiss(final Node node, final Visit visit)
	{
	}

	public void accept(final Node node, final Visit visit)
	{
		if (accept(node))
		{
			onAccept(node, visit);
		}
		else
		{
			onDismiss(node, visit);
		}
	}

	public Collection<Node> getMatches()
	{
		return matches;
	}
}
