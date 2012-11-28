package de.danielbechler.diff;

import de.danielbechler.diff.node.*;

public class MapNodeFactory
{
	public MapNodeFactory()
	{
	}

	public MapNode createMapNode(final Node parentNode, final Instances instances)
	{
		return new MapNode(parentNode, instances.getSourceAccessor(), instances.getType());
	}
}
