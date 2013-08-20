package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface ReturnableNodeConfiguration
{
	ReturnableNodeConfiguration returnNodesWithState(DiffNode.State state, boolean enabled);

	ReturnableNodeConfiguration returnNodesWithState(DiffNode.State state);

	ReturnableNodeConfiguration omitNodesWithState(DiffNode.State state);
}
