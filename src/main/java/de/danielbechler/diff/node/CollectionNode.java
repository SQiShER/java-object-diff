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
