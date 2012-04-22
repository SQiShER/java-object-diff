package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractFilteringVisitor implements DiffNode.Visitor
{
	private final Collection<DiffNode<?>> matches = new ArrayList<DiffNode<?>>(30);

	protected abstract boolean accept(final DiffNode<?> difference);

	protected void onAccept(final DiffNode<?> difference, final Visit visit)
	{
		matches.add(difference);
	}

	protected void onDismiss(final DiffNode<?> difference, final Visit visit)
	{
	}

	@Override
	public final void accept(final DiffNode<?> difference, final Visit visit)
	{
		if (accept(difference))
		{
			onAccept(difference, visit);
		}
		else
		{
			onDismiss(difference, visit);
		}
	}

	public Collection<DiffNode<?>> getMatches()
	{
		return matches;
	}
}
