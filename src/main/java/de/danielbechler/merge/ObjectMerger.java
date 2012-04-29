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
