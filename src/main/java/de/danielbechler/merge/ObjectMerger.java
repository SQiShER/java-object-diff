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

package de.danielbechler.merge;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.visitor.*;

/**
 * Careful: This class has not yet been tested very thoroughly and serves more as an example for your own implementations.
 *
 * @author Daniel Bechler
 */
public final class ObjectMerger
{
	private final ObjectDiffer objectDiffer;

	public ObjectMerger()
	{
		this.objectDiffer = ObjectDifferFactory.getInstance();
	}

	public ObjectMerger(final ObjectDiffer objectDiffer)
	{
		this.objectDiffer = objectDiffer;
	}

	@SuppressWarnings({"unchecked"})
	public <T> T merge(final T modified, final T base, final T head)
	{
		final Node.Visitor visitor = new MergingDifferenceVisitor<T>(head, modified);
		final Node difference = objectDiffer.compare(modified, base);
		difference.visit(visitor);
		return head;
	}

	private static final class MergingDifferenceVisitor<T> implements Node.Visitor
	{
		private final T head;
		private final T modified;

		public MergingDifferenceVisitor(final T head, final T modified)
		{
			this.head = head;
			this.modified = modified;
		}

		public void accept(final Node node, final Visit visit)
		{
			if (node.getState() == Node.State.ADDED)
			{
				node.canonicalSet(head, node.canonicalGet(modified));
			}
			else if (node.getState() == Node.State.REMOVED)
			{
				node.canonicalUnset(head);
			}
			else if (node.getState() == Node.State.REPLACED)
			{
				node.canonicalSet(head, node.canonicalGet(modified));
			}
			else if (node.getState() == Node.State.UNTOUCHED)
			{
				// not touched - nothing to do
			}
			else if (node.getState() == Node.State.CHANGED)
			{
				if (node.hasChildren())
				{
					node.visitChildren(this);
					visit.dontGoDeeper();
				}
				else
				{
					node.canonicalSet(head, node.canonicalGet(modified));
				}
			}
		}
	}
}
