package de.danielbechler.diff;

/**
 * Allows to exclude nodes from being added to the object graph based on criteria that are only known after the diff
 * for
 * the affected node and all its children has been determined.
 * <p/>
 * Currently it is only possible to configure returnability based on the state (_added_, _changed_, _untouched_, etc.)
 * of the `DiffNode`. But this is just the beginning. Nothing speaks against adding more powerful options. It would be
 * nice for example to be able to pass some kind of matcher to determine returnability based on dynamic criteria at
 * runtime.
 *
 * @author Daniel Bechler
 */
public interface ReturnableNodeConfiguration
{
	ReturnableNodeConfiguration returnNodesWithState(DiffNode.State state, boolean enabled);

	ReturnableNodeConfiguration returnNodesWithState(DiffNode.State state);

	ReturnableNodeConfiguration omitNodesWithState(DiffNode.State state);
}
