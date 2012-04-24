package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class StateFilteringVisitor extends AbstractFilteringVisitor
{
	private final Node.State state;

	public StateFilteringVisitor(final Node.State state)
	{
		Assert.notNull(state, "state");
		this.state = state;
	}

	@Override
	protected boolean accept(final Node node)
	{
		return node.getState() == state;
	}

	@Override
	protected void onAccept(final Node node, final Visit visit)
	{
		super.onAccept(node, visit);
		visit.dontGoDeeper();
	}

	@Override
	protected void onDismiss(final Node node, final Visit visit)
	{
		super.onDismiss(node, visit);
		visit.dontGoDeeper();
	}
}
