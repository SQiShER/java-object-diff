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

package de.danielbechler.diff;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;

/**
 * Careful: This class has not yet been tested very thoroughly and serves more as an example for your own
 * implementations.
 *
 * @author Daniel Bechler
 */
@SuppressWarnings("UnusedDeclaration")
public final class ObjectMerger
{
	private final ObjectDiffer objectDiffer;

	public ObjectMerger()
	{
		this.objectDiffer = ObjectDifferBuilder.buildDefault();
	}

	public ObjectMerger(final ObjectDiffer objectDiffer)
	{
		this.objectDiffer = objectDiffer;
	}

	@SuppressWarnings({"unchecked"})
	public <T> T merge(final T modified, final T base, final T head)
	{
		final DiffNode.Visitor visitor = new MergingDifferenceVisitor<T>(head, modified);
		final DiffNode difference = objectDiffer.compare(modified, base);
		difference.visit(visitor);
		return head;
	}

	private static final class MergingDifferenceVisitor<T> implements DiffNode.Visitor
	{
		private final T head;
		private final T modified;

		public MergingDifferenceVisitor(final T head, final T modified)
		{
			this.head = head;
			this.modified = modified;
		}

		public void node(final DiffNode node, final Visit visit)
		{
			if (node.getState() == DiffNode.State.ADDED)
			{
				node.canonicalSet(head, node.canonicalGet(modified));
			}
			else if (node.getState() == DiffNode.State.REMOVED)
			{
				node.canonicalUnset(head);
			}
			else if (node.getState() == DiffNode.State.CHANGED)
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
