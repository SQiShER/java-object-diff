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

	public final void accept(final Node node, final Visit visit)
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
