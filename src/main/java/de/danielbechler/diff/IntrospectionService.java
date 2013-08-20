package de.danielbechler.diff;

import java.util.*;

/** @author Daniel Bechler */
class IntrospectionService implements IntrospectionConfiguration, IsIntrospectableResolver
{
	private final Map<DiffNode.State, Boolean> suppressIntrospectionSettings;

	public IntrospectionService()
	{
		this.suppressIntrospectionSettings = new EnumMap<DiffNode.State, Boolean>(DiffNode.State.class);
		this.suppressIntrospectionSettings.put(DiffNode.State.IGNORED, false);
		this.suppressIntrospectionSettings.put(DiffNode.State.UNTOUCHED, false);
		this.suppressIntrospectionSettings.put(DiffNode.State.CIRCULAR, true);
		this.suppressIntrospectionSettings.put(DiffNode.State.ADDED, true);
		this.suppressIntrospectionSettings.put(DiffNode.State.REMOVED, true);
		this.suppressIntrospectionSettings.put(DiffNode.State.CHANGED, false);
	}

	public boolean isIntrospectable(final DiffNode node)
	{
		if (isIntrospectableType(node.getType()))
		{
			return true;
		}
		if (isIntrospectableState(node.getState()))
		{
			return true;
		}
		return false;
	}

	private static boolean isIntrospectableType(final Class<?> nodeType)
	{
		return nodeType != null && !nodeType.isPrimitive() && !nodeType.isArray();
	}

	private boolean isIntrospectableState(final DiffNode.State state)
	{
		final Boolean suppressed = suppressIntrospectionSettings.get(state);
		if (suppressed != null)
		{
			return !suppressed;
		}
		return false;
	}

	public IntrospectionConfiguration includeChildrenOfNodeWithState(final DiffNode.State state)
	{
		suppressIntrospectionSettings.put(state, false);
		return this;
	}

	public IntrospectionConfiguration excludeChildrenOfNodeWithState(final DiffNode.State state)
	{
		suppressIntrospectionSettings.put(state, true);
		return this;
	}
}
