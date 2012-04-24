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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.visitor.*;

import java.util.*;

/** @author Daniel Bechler */
public final class CollectionNode extends DefaultNode
{
	public CollectionNode(final Node parent, final Accessor accessor)
	{
		super(parent, accessor);
	}

	public Accessor accessorForItem(final Object item)
	{
		return new CollectionItemAccessor(item);
	}

	public Collection<Node> getAdditions()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.ADDED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<Node> getRemovals()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.REMOVED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<Node> getChanges()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.CHANGED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	@Override
	public boolean isCollectionDifference()
	{
		return true;
	}

	@Override
	public CollectionNode toCollectionDifference()
	{
		return this;
	}

}
