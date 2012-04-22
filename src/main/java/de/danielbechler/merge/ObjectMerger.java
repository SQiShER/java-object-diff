package de.danielbechler.merge;

import de.danielbechler.diff.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.visitor.*;

/** @author Daniel Bechler */
public final class ObjectMerger<T>
{
	private final ObjectDiffer objectDiffer;

	public ObjectMerger()
	{
		this(new BeanDiffer());
	}

	public ObjectMerger(final ObjectDiffer objectDiffer)
	{
		this.objectDiffer = objectDiffer;
	}

	public T merge(final T modified, final T base, final T head)
	{
		final DiffNode.Visitor visitor = new MergingDifferenceVisitor<T>(head, modified);
		final DiffNode<T> difference = objectDiffer.compare(modified, base);
		difference.visit(visitor);
		return head;
	}

	public ObjectDiffer getObjectDiffer()
	{
		return objectDiffer;
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

		public void accept(final DiffNode<?> difference, final Visit visit)
		{
			if (difference.getType() == DifferenceType.ADDED)
			{
				difference.getCanonicalAccessor().set(head, difference.getCanonicalAccessor().get(modified));
			}
			else if (difference.getType() == DifferenceType.REMOVED)
			{
				difference.getCanonicalAccessor().unset(head, difference.getCanonicalAccessor().get(modified));
			}
			else if (difference.getType() == DifferenceType.REPLACED)
			{
				difference.getCanonicalAccessor().set(head, difference.getCanonicalAccessor().get(modified));
			}
			else if (difference.getType() == DifferenceType.UNTOUCHED)
			{
				// not touched - nothing to do
			}
			else if (difference.getType() == DifferenceType.CHANGED)
			{
				if (difference.hasChildren())
				{
					difference.visitChildren(this);
					visit.dontGoDeeper();
				}
				else
				{
					difference.getCanonicalAccessor().set(head, difference.getCanonicalAccessor().get(modified));
				}
			}
		}
	}
}
