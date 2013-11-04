package de.danielbechler.diff;

/** @author Daniel Bechler */
public class NodeStateMatcher implements NodeMatcher
{
	private final DiffNode.State state;

	public NodeStateMatcher(final DiffNode.State state)
	{
		this.state = state;
	}

	public boolean matches(final DiffNode node)
	{
		return node.getState() == state;
	}
}
