package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.visitor.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public final class CollectionNode<T> extends DefaultNode<Collection<T>>
{
	public CollectionNode(final Accessor<?> accessor)
	{
		super(accessor);
	}

	@SuppressWarnings({"unchecked"})
	public Accessor<T> accessorForItem(final T item)
	{
		return new CollectionItemAccessor<T>(item);
	}

	public Collection<DiffNode<?>> getAdditions()
	{
		final AbstractFilteringVisitor visitor = new TypeFilteringVisitor(DifferenceType.ADDED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<DiffNode<?>> getRemovals()
	{
		final AbstractFilteringVisitor visitor = new TypeFilteringVisitor(DifferenceType.REMOVED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<DiffNode<?>> getChanges()
	{
		final AbstractFilteringVisitor visitor = new TypeFilteringVisitor(DifferenceType.CHANGED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	@Override
	public boolean isCollectionDifference()
	{
		return true;
	}

	@Override
	public CollectionNode<?> toCollectionDifference()
	{
		return this;
	}

	private static class TypeFilteringVisitor extends AbstractFilteringVisitor
	{
		private final DifferenceType differenceType;

		public TypeFilteringVisitor(final DifferenceType differenceType)
		{
			Assert.notNull(differenceType, "differenceType");
			this.differenceType = differenceType;
		}

		@Override
		protected boolean accept(final DiffNode<?> difference)
		{
			return difference.getType() == differenceType;
		}

		@Override
		protected void onAccept(final DiffNode<?> difference, final Visit visit)
		{
			super.onAccept(difference, visit);
			visit.dontGoDeeper();
		}

		@Override
		protected void onDismiss(final DiffNode<?> difference, final Visit visit)
		{
			super.onDismiss(difference, visit);
			visit.dontGoDeeper();
		}
	}
}
