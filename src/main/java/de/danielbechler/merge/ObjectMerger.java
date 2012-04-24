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

		public void accept(final Node difference, final Visit visit)
		{
			if (difference.getState() == Node.State.ADDED)
			{
				difference.canonicalSet(head, difference.canonicalGet(modified));
			}
			else if (difference.getState() == Node.State.REMOVED)
			{
				difference.canonicalUnset(head);
			}
			else if (difference.getState() == Node.State.REPLACED)
			{
				difference.canonicalSet(head, difference.canonicalGet(modified));
			}
			else if (difference.getState() == Node.State.UNTOUCHED)
			{
				// not touched - nothing to do
			}
			else if (difference.getState() == Node.State.CHANGED)
			{
				if (difference.hasChildren())
				{
					difference.visitChildren(this);
					visit.dontGoDeeper();
				}
				else
				{
					difference.canonicalSet(head, difference.canonicalGet(modified));
				}
			}
		}
	}
}
